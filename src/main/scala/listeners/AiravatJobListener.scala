/*
 * Created by @alivcor (Abhinandan Dubey) on 2/3/21
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify,
 *  merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software
 *  is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, author's credentials and this
 * permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.iresium.airavat

import java.net._
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
    val airavatApplication = TableQuery[AiravatApplication]
    var jobMap = scala.collection.mutable.Map[Int, Int] ()
    var jobInfo = scala.collection.mutable.Map[Int, JobMetricTuple] ()
    var queryMap = scala.collection.mutable.Map[Long, Seq[Int]] ()
    var queryInfo = scala.collection.mutable.Map[Long, QueryMetricTuple]()
    var disposableJobsQueue = scala.collection.mutable.Queue[Int]()
    var killedJobsMap = scala.collection.mutable.Map[Int, Tuple2[JobMetricTuple, String]] ()
    var startTimestamp = 0L
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
                    airavatQueryMetric += (InetAddress.getLocalHost.getHostName,
                        InetAddress.getLocalHost.getHostAddress,
                        appId,
                        queryMetricTuple.executionId,
                        gson.toJson(queryMetricTuple.jobIds),
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

                Await.result(logQueryMetricsF, Try(conf.get("spark.airavat.dbTimeoutSeconds").toInt).getOrElse(120) seconds)

            } catch {
                case e: Exception => { logger.warn(s"Failed to log queryMetrics to sink: " + e.getMessage)}
            }
            //            finally db.close

    }

    override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = {
        logger.info(s"Tracking Application - " + applicationStart.appId)
        appId = applicationStart.appId.getOrElse("Unknown")
        SparkSession.builder().getOrCreate().sessionState.listenerManager.register(new AiravatQueryListener(conf))
        startTimestamp = System.currentTimeMillis / 1000
        try{
            val addAppSeq = DBIO.seq(
                airavatApplication += (InetAddress.getLocalHost.getHostName,
                    InetAddress.getLocalHost.getHostAddress,
                    appId, startTimestamp, 0L, SparkSession.builder().getOrCreate().sparkContext.master, conf.get("spark.driver.memory", "1"), conf.get("spark.driver.cores", "2"), conf.get("spark.executor.memory", "1"), conf.get("spark.executor.cores", "2"), conf.get("spark.executor.instances", "1"))
            )
            val logAppF = db.run(addAppSeq)


            logAppF onComplete {
                case Success(v) => logger.info("Logged app " + appId + " to the sink")
                case Failure(t) => logger.warn("An error occurred while logging jobMetrics to sink: " + t.getMessage)
            }

            Await.result(logAppF, Try(conf.get("spark.airavat.dbTimeoutSeconds").toInt).getOrElse(120) seconds)

        } catch {
            case e: Exception => { logger.warn(s"Failed to log app to sink: " + e.getMessage)}
        }

    }


    override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
        try{

            flushJobs() // Important, if there are any jobs which were killed and missed persistence

            val q = for { c <- airavatApplication if c.appId === appId && c.startTimestamp === startTimestamp && c.hostname === InetAddress.getLocalHost.getHostName  } yield c.endTimestamp
            val updateAction = q.update(System.currentTimeMillis / 1000)
            val updateAppF = db.run(updateAction)

            updateAppF onComplete {
                case Success(v) => logger.info("Logged app " + appId + " to the sink")
                case Failure(t) => logger.warn("An error occurred while logging jobMetrics to sink: " + t.getMessage)
            }

            Await.result(updateAppF, Try(conf.get("spark.airavat.dbTimeoutSeconds").toInt).getOrElse(120) seconds)

        } catch {
            case e: Exception => { logger.warn(s"Failed to log jobMetrics to sink: " + e.getMessage)}
        }

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
                val evictableJobId = disposableJobsQueue.dequeue()
                if(jobInfo.contains(evictableJobId) && !jobInfo(evictableJobId).killedCause.equals("")){
                    // Job isn't yet evictable, the job was cancelled by Airavat and a jobEnd event was never called, so the job just stayed in the queue.
                    persistJob(jobInfo(evictableJobId)) // check if it needs to be persisted.
                }
                evictJob(evictableJobId)
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

    private def persistJob(jobMetricTuple: JobMetricTuple): Unit = {
        if(Try(conf.get("spark.airavat.collectJobMetrics").toBoolean).getOrElse(false)){
            try{
                val addJobsSeq = DBIO.seq(
                    airavatJobMetric += (InetAddress.getLocalHost.getHostName,
                        InetAddress.getLocalHost.getHostAddress,
                        appId, jobMetricTuple.jobId, jobMetricTuple.numStages, jobMetricTuple.numTasks, jobMetricTuple.totalDuration, jobMetricTuple.totalDiskSpill, jobMetricTuple.totalBytesRead, jobMetricTuple.totalBytesWritten, jobMetricTuple.totalResultSize, jobMetricTuple.totalShuffleReadBytes, jobMetricTuple.totalShuffleWriteBytes, jobMetricTuple.killedCause, jobMetricTuple.timestamp)
                )
                val logJobMetricsF = db.run(addJobsSeq)


                logJobMetricsF onComplete {
                    case Success(v) => logger.info("Logged metrics for " + jobMetricTuple.jobId + " to the sink")
                    case Failure(t) => logger.warn("An error occurred while logging jobMetrics to sink: " + t.getMessage)
                }

                Await.result(logJobMetricsF, Try(conf.get("spark.airavat.dbTimeoutSeconds").toInt).getOrElse(120) seconds)

            } catch {
                case e: Exception => { logger.warn(s"Failed to log jobMetrics to sink: " + e.getMessage)}
            }
            //            finally db.close

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
                    val killCause = s"Airavat : Killing Job ${jobStart.jobId} because it breached maxJobDuration of ${Try(conf.get("spark.airavat.maxJobDuration").toLong).getOrElse(scala.Long.MaxValue)}"
                    jobInfo(jobStart.jobId) = JobMetricSerializer.markJobKilled(jobInfo(jobStart.jobId), killCause)
                }
            }
        }

    }

    override def onJobEnd(jobEnd: SparkListenerJobEnd) {
        logger.info("JobEnd " + jobEnd.jobId)
        persistJob(jobInfo(jobEnd.jobId))
    }

    override def onTaskEnd(taskEnd: SparkListenerTaskEnd) {

        val taskMetricTuple = TaskMetricSerializer.serialize(taskEnd, jobMap)

        if(jobMap.contains(taskEnd.stageId) && jobInfo.contains(jobMap(taskEnd.stageId))){
            jobInfo(jobMap(taskEnd.stageId)) = JobMetricSerializer.updateDerivedMetrics(jobInfo(jobMap(taskEnd.stageId)), taskEnd, taskMetricTuple)
            val killCause = MetricAnalyzer.analyzeJobMetrics(jobInfo(jobMap(taskEnd.stageId)))
            if (!killCause.isEmpty()){
                jobInfo(jobMap(taskEnd.stageId)) = JobMetricSerializer.markJobKilled(jobInfo(jobMap(taskEnd.stageId)), killCause)
            }
        }

    }

}
