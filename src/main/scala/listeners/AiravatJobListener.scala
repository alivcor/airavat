/*
 * Created by @alivcor (Abhinandan Dubey) on 2/3/21
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iresium.airavat

import com.google.gson._
import org.apache.spark.{JobExecutionStatus, SparkConf}
import org.apache.spark.scheduler.{SparkListener, SparkListenerEvent, _}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.SQLExecution
import org.apache.spark.sql.execution.ui.{SparkListenerSQLExecutionEnd, SparkListenerSQLExecutionStart}
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}


class AiravatJobListener(conf: SparkConf) extends SparkListener {

    var appId = ""
    val db = Database.forConfig(Try(conf.get("spark.airavat.dbName")).getOrElse("airavat_db"))
    val airavatJobMetric = TableQuery[AiravatJobMetric]
    val airavatQueryMetric = TableQuery[AiravatQueryMetric]
    var jobMap = scala.collection.mutable.Map[Int, Int] ()
    var jobInfo = scala.collection.mutable.Map[Int, JobMetricTuple] ()
    var queryMap = scala.collection.mutable.Map[Long, Seq[Int]] ()
    var queryInfo = scala.collection.mutable.Map[Long, QueryMetricTuple]()
    var disposableJobsQueue = scala.collection.mutable.Queue[Int]()
    val gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListSerializer)
        .registerTypeHierarchyAdapter(classOf[Map[Any, Any]], new MapSerializer)
        .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionSerializer)
        .registerTypeAdapter(classOf[DateTime], new DateTimeSerializer)
        .create()

    val logger = LoggerFactory.getLogger(this.getClass)
    
    override def onOtherEvent(event: SparkListenerEvent): Unit = {
        event match {
            case executionStart: SparkListenerSQLExecutionStart =>
                logger.debug(s"Adding executionId " + executionStart.executionId + " to queryInfo")
                queryInfo += (executionStart.executionId -> QueryMetricSerializer.serialize(executionStart))

            case executionEnd: SparkListenerSQLExecutionEnd =>
                logger.debug(s"Looking for executionId " + executionEnd.executionId + " in queryInfo : " + queryInfo.contains(executionEnd.executionId))
                logger.debug(s" jobMap : " + jobMap)
                logger.debug(s" queryMap : " + queryMap)

                if(queryMap.contains(executionEnd.executionId)){
                    try {
                        queryInfo(executionEnd.executionId) = QueryMetricSerializer.updateMetrics(executionEnd, queryInfo(executionEnd.executionId), (queryMap(executionEnd.executionId)).map(jobInfo(_)))
                        if(Try(conf.get("spark.airavat.collectQueryMetrics").toBoolean).getOrElse(false)) logQueryMetrics(queryInfo(executionEnd.executionId))
                    }
                    catch {
                        case e: Exception => logger.warn(s"An error occurred while generating metrics for query " + e.getStackTrace)
                    } finally {
                        queryMap(executionEnd.executionId).foreach(evictJob(_))
                        evictQuery(executionEnd.executionId)
                        flushJobs()
                    }
                } else {
                    logger.warn("Execution ID " + executionEnd.executionId + " not found in queryMap")
                }

            case _ =>
        }
    }


    def logQueryMetrics(queryMetricTuple: QueryMetricTuple) = {

            try{

                val addQuerySeq = DBIO.seq(
                    airavatQueryMetric += (appId,
                        queryMetricTuple.executionId,
                        queryMetricTuple.jobIds,
                        queryMetricTuple.description,
                        queryMetricTuple.startTimestamp,
                        queryMetricTuple.sparkPlan,
                        queryMetricTuple.endTimestamp,
                        queryMetricTuple.numTasks,
                        queryMetricTuple.totalDiskSpill,
                        queryMetricTuple.totalBytesRead,
                        queryMetricTuple.totalBytesWritten,
                        queryMetricTuple.totalResultSize,
                        queryMetricTuple.totalShuffleReadBytes,
                        queryMetricTuple.totalShuffleWriteBytes)
                )
                val logQueryMetricsF = db.run(addQuerySeq)


                logQueryMetricsF onComplete {
                    case Success(v) => logger.info("Logged metrics for Query " + queryMetricTuple.executionId + " to the sink")
                    case Failure(t) => logger.warn("An error occurred while logging queryMetrics to sink: " + t.getMessage)
                }

                Await.result(logQueryMetricsF, 120 seconds)

            } catch {
                case e: Exception => { logger.warn(s"Failed to log queryMetrics to sink: " + e.getMessage)}
            }
            //            finally db.close

    }

    override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = {
        logger.info(s"Tracking Application - " + applicationStart.appId)
        appId = applicationStart.appId.getOrElse("Unknown")
        SparkSession.builder().getOrCreate().sessionState.listenerManager.register(new AiravatQueryListener(conf))
    }


    private def evictJob(evictableJob: Int): Unit = {
        try {
            jobMap.remove(evictableJob)
            jobInfo.remove(evictableJob)
        } catch {
            case e: Exception => { logger.warn(s"Failed to evict job Id " + evictableJob + " : " + e.getMessage)}
        }
    }

    private def flushJobs() = {
        var maxJobsRetain = Try(conf.get("spark.airavat.maxJobRetain").toInt).getOrElse(100)
        try {
            while(maxJobsRetain > 0 && disposableJobsQueue.length > 0){
                evictJob(disposableJobsQueue.dequeue())
                maxJobsRetain -= 1
            }
        } catch {
            case e: Exception => { logger.warn(s"Failed to flush Jobs " + e.getMessage)}
        }
    }

    private def evictQuery(evictableQuery: Long): Unit = {
        try {
            queryMap.remove(evictableQuery)
            queryInfo.remove(evictableQuery)
        } catch {
            case e: Exception => { logger.warn(s"Failed to evict query Id " + evictableQuery + " : " + e.getMessage)}
        }
    }

    override def onJobStart(jobStart: SparkListenerJobStart) {

        logger.info("JobStart " + jobStart.jobId)
        val executionIdString = jobStart.properties.getProperty(SQLExecution.EXECUTION_ID_KEY)
        if (executionIdString != null) {
            // This job is created by SQL Query
            val executionId = executionIdString.toLong
            if(queryMap.contains(executionId)){
                queryMap(executionId) :+ jobStart.jobId
            } else {
                queryMap += (executionId -> Seq(jobStart.jobId))
            }
        } else {
            disposableJobsQueue.enqueue(jobStart.jobId)
        }


        val jobMetricTuple: JobMetricTuple = JobMetricSerializer.serialize(jobStart)
        for(stageId <- jobMetricTuple.stageIds){
            jobMap += (stageId -> jobStart.jobId)
        }

        jobInfo += (jobStart.jobId -> jobMetricTuple)

        if(conf.contains("spark.airavat.maxJobDuration")) {
            val statusTracker = SparkSession.builder().getOrCreate().sparkContext.statusTracker
            val f = Future {
                var timeLeft = Try(conf.get("spark.airavat.maxJobDuration").toInt).getOrElse(Integer.MAX_VALUE)
                while(timeLeft >= 0 && !statusTracker.getJobInfo(jobStart.jobId).isEmpty && (statusTracker.getJobInfo(jobStart.jobId).get.status() == JobExecutionStatus.RUNNING || statusTracker.getJobInfo(jobStart.jobId).get.status() == JobExecutionStatus.UNKNOWN))
                {
                    Thread.sleep(1000)
                    timeLeft -= 1
                }
                if(!statusTracker.getJobInfo(jobStart.jobId).isEmpty && (statusTracker.getJobInfo(jobStart.jobId).get.status() == JobExecutionStatus.RUNNING || statusTracker.getJobInfo(jobStart.jobId).get.status() == JobExecutionStatus.UNKNOWN))
                {
                    SparkSession.builder().getOrCreate().sparkContext.cancelJob(jobStart.jobId)
                }
            }
        }

    }

    override def onJobEnd(jobEnd: SparkListenerJobEnd) {

        logger.info("JobEnd " + jobEnd.jobId)
        if(Try(conf.get("spark.airavat.collectJobMetrics").toBoolean).getOrElse(false)){
            try{
                val jobDetails = jobInfo(jobEnd.jobId)
                val addJobsSeq = DBIO.seq(
                    airavatJobMetric += (appId, jobDetails.jobId, jobDetails.numStages, jobDetails.numTasks, jobDetails.totalDuration, jobDetails.totalDiskSpill, jobDetails.totalBytesRead, jobDetails.totalBytesWritten, jobDetails.totalResultSize, jobDetails.totalShuffleReadBytes, jobDetails.totalShuffleWriteBytes, jobDetails.timestamp)
                )
                val logJobMetricsF = db.run(addJobsSeq)


                logJobMetricsF onComplete {
                    case Success(v) => logger.info("Logged metrics for " + jobDetails.jobId + " to the sink")
                    case Failure(t) => logger.warn("An error occurred while logging jobMetrics to sink: " + t.getMessage)
                }

                Await.result(logJobMetricsF, 120 seconds)

            } catch {
                case e: Exception => { logger.warn(s"Failed to log jobMetrics to sink: " + e.getMessage)}
            }
//            finally db.close

        }

    }

    override def onTaskEnd(taskEnd: SparkListenerTaskEnd) {

        val taskMetricTuple = TaskMetricSerializer.serialize(taskEnd, jobMap)

        if(jobMap.contains(taskEnd.stageId) && jobInfo.contains(jobMap(taskEnd.stageId))){
            jobInfo(jobMap(taskEnd.stageId)) = JobMetricSerializer.updateDerivedMetrics(jobInfo(jobMap(taskEnd.stageId)), taskEnd, taskMetricTuple)
            MetricAnalyzer.analyzeJobMetrics(jobInfo(jobMap(taskEnd.stageId)))
        }

    }

}
