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
import org.apache.spark.SparkConf
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
    val db = Database.forConfig("sqlite1")
    val airavatJobs = TableQuery[AiravatJobs]
    val airavatQueries = TableQuery[AiravatQueries]
    var jobMap = scala.collection.mutable.Map[Int, Int] ()
    var jobInfo = scala.collection.mutable.Map[Int, JobMetricTuple] ()
    var queryMap = scala.collection.mutable.Map[Long, Seq[Int]] ()
    var queryInfo = scala.collection.mutable.Map[Long, QueryMetricTuple]()
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
                    queryInfo(executionEnd.executionId) = QueryMetricSerializer._updateMetrics(executionEnd, queryInfo(executionEnd.executionId), (queryMap(executionEnd.executionId)).map(jobInfo(_)))
                    logger.info(queryInfo(executionEnd.executionId).queryStats)
                    // TODO : Evict Jobs associated
                } else {
                    logger.warn("Execution ID " + executionEnd.executionId + " not found in queryMap")
                }



                if(Try(conf.get("spark.airavat.collectQueryMetrics").toBoolean).getOrElse(false)) logQueryMetrics(queryInfo(executionEnd.executionId))
            case _ =>
        }
    }


    def logQueryMetrics(queryMetricTuple: QueryMetricTuple) = {

            try{

                val addQuerySeq = DBIO.seq(
                    airavatQueries += (appId,
                        queryMetricTuple.executionId,
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
                        queryMetricTuple.totalShuffleWriteBytes,
                        queryMetricTuple.logicalPlan,
                        queryMetricTuple.optimizedPlan,
                        queryMetricTuple.executedPlan,
                        queryMetricTuple.queryStats,
                        queryMetricTuple.duration)
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
        SparkSession.builder().getOrCreate().sessionState.listenerManager.register(new AiravatQueryListener)
    }


    private def evictJobs(jobs: Seq[Int]): Unit = {
        try {
//            jobInfo -= jobId
            jobs.foreach(jobInfo.remove)
        } catch {
            case e: Exception => { logger.warn(s"Failed to evict job Ids " + jobs + " from job Metrics : " + e.getMessage)}
        }
    }

    private def evictJobMetrics(jobId: Int): Unit = {
        // TODO - Evict Jobs which don't have an execution ID but are clogging up and complete/failed
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
        }

        val jobMetricTuple: JobMetricTuple = JobMetricSerializer.serialize(jobStart)
        for(stageId <- jobMetricTuple.stageIds){
            jobMap += (stageId -> jobStart.jobId)
        }

        jobInfo += (jobStart.jobId -> jobMetricTuple)

//        MetricAnalyzer.analyzeTaskCount(jobMetricTuple) #TODO
//        val statusTracker = spark.sparkContext.statusTracker

    }

    override def onJobEnd(jobEnd: SparkListenerJobEnd) {

        logger.info("JobEnd " + jobEnd.jobId)
        if(Try(conf.get("spark.airavat.collectJobMetrics").toBoolean).getOrElse(false)){
            try{
                val jobDetails = jobInfo(jobEnd.jobId)
                val addJobsSeq = DBIO.seq(
                    airavatJobs += (appId, jobDetails.jobId, jobDetails.numStages, jobDetails.numTasks, jobDetails.totalDuration, jobDetails.totalDiskSpill, jobDetails.totalBytesRead, jobDetails.totalBytesWritten, jobDetails.totalResultSize, jobDetails.totalShuffleReadBytes, jobDetails.totalShuffleWriteBytes, jobDetails.timestamp)
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

        if(Try(conf.get("spark.airavat.collectTaskMetrics").toBoolean).getOrElse(false)){

        }

        if(jobMap.contains(taskEnd.stageId) && jobInfo.contains(jobMap(taskEnd.stageId))){
            jobInfo(jobMap(taskEnd.stageId)) = JobMetricSerializer.updateDerivedMetrics(jobInfo(jobMap(taskEnd.stageId)), taskEnd, taskMetricTuple)
            MetricAnalyzer.analyzeTaskMetrics(taskMetricTuple)
            MetricAnalyzer.analyzeJobMetrics(jobInfo(jobMap(taskEnd.stageId)))
        }

    }

}
