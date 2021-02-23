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

import scala.util.Try
import com.google.gson._
import org.apache.spark.internal.Logging
import org.apache.spark.scheduler.{SparkListener, SparkListenerEvent, _}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.ui.{SparkListenerSQLExecutionEnd, SparkListenerSQLExecutionStart, SparkPlanGraph}
import org.joda.time.DateTime;


class AiravatListener extends SparkListener with Logging {

    var jobMap = scala.collection.mutable.Map[Int, Int] ()
    var jobInfo = scala.collection.mutable.Map[Int, JobMetricTuple] ()

    override def onOtherEvent(event: SparkListenerEvent): Unit = {
        event match {
            case executionStart: SparkListenerSQLExecutionStart =>
            val gson = new GsonBuilder()
                            .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListSerializer)
                            .registerTypeHierarchyAdapter(classOf[Map[Any, Any]], new MapSerializer)
                            .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionSerializer)
                            .registerTypeAdapter(classOf[DateTime], new DateTimeSerializer)
                            .create()
            val physicalPlanGraph = SparkPlanGraph (executionStart.sparkPlanInfo)
            val sqlPlanMetrics = physicalPlanGraph.allNodes.flatMap {node =>
                node.metrics.map (metric => metric.accumulatorId -> metric)
            }
            case executionEnd: SparkListenerSQLExecutionEnd =>
            case _ =>
        }
    }

    override def onJobStart(jobStart: SparkListenerJobStart) {

        logInfo("JobStart " + jobStart.jobId)
        val spark = SparkSession.builder().getOrCreate()
        val jobMetricTuple: JobMetricTuple = JobMetricSerializer.serialize(jobStart)

        for(stageId <- jobMetricTuple.stageIds){
            jobMap += (stageId -> jobStart.jobId)
        }

        jobInfo += (jobStart.jobId -> jobMetricTuple)

        val gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListSerializer)
            .registerTypeHierarchyAdapter(classOf[Map[Any, Any]], new MapSerializer)
            .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionSerializer)
            .registerTypeAdapter(classOf[DateTime], new DateTimeSerializer)
            .create()

//        MetricAnalyzer.analyzeTaskCount(jobMetricTuple) #TODO
        val statusTracker = spark.sparkContext.statusTracker


    }

    override def onJobEnd(jobEnd: SparkListenerJobEnd) {
        val spark = SparkSession.builder().getOrCreate()
        logInfo("JobEnd " + jobEnd.jobId)
        if(Try(spark.conf.get("spark.airavat.collectJobMetrics").toBoolean).getOrElse(false)){
            val gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListSerializer)
                .registerTypeHierarchyAdapter(classOf[Map[Any, Any]], new MapSerializer)
                .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionSerializer)
                .registerTypeAdapter(classOf[DateTime], new DateTimeSerializer)
                .create()

            logInfo(gson.toJson(jobInfo(jobEnd.jobId)))


        }

        try {
            jobInfo -= jobEnd.jobId
        } catch {
            case e: Exception => { logWarning(s"Failed to evict job Id from job Metrics")}
        }
    }

    override def onTaskEnd(taskEnd: SparkListenerTaskEnd) {
        val spark = SparkSession.builder().getOrCreate()
        val gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListSerializer)
            .registerTypeHierarchyAdapter(classOf[Map[Any, Any]], new MapSerializer)
            .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionSerializer)
            .registerTypeAdapter(classOf[DateTime], new DateTimeSerializer)
            .create()
        val taskMetricTuple = TaskMetricSerializer.serialize(taskEnd, jobMap)


        if(Try(spark.conf.get("spark.airavat.collectTaskMetrics").toBoolean).getOrElse(false)){

        }

        if(jobMap.contains(taskEnd.stageId) && jobInfo.contains(jobMap(taskEnd.stageId))){
            jobInfo(jobMap(taskEnd.stageId)) = JobMetricSerializer.updateDerivedMetrics(jobInfo(jobMap(taskEnd.stageId)), taskEnd, taskMetricTuple)
            MetricAnalyzer.analyzeTaskMetrics(taskMetricTuple)
            MetricAnalyzer.analyzeJobMetrics(jobInfo(jobMap(taskEnd.stageId)))
        }

    }

}
