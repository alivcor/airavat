/*
 * Created by @alivcor (Abhinandan Dubey) on 2/23/21
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

import org.apache.spark.sql.execution.SQLExecution
import org.apache.spark.sql.execution.ui.{SparkListenerSQLExecutionEnd, SparkListenerSQLExecutionStart, SparkPlanGraph}

import scala.util.Try


object QueryMetricSerializer {

    def updateMetrics(executionEnd: SparkListenerSQLExecutionEnd, queryMetricTuple: QueryMetricTuple, jobIds: Seq[Int], jobInfo: scala.collection.mutable.Map[Int, JobMetricTuple] ) = {
        var jobMetricTuples = Seq[JobMetricTuple]()

        for(jobId <- jobIds){
            jobMetricTuples :+ jobInfo(jobId)
        }

        _updateMetrics(executionEnd, queryMetricTuple, jobIds.map(jobInfo(_)))
    }



    def _updateMetrics(executionEnd: SparkListenerSQLExecutionEnd, queryMetricTuple: QueryMetricTuple, jobMetricTuples: Seq[JobMetricTuple]): QueryMetricTuple = {
        val duration = executionEnd.time - queryMetricTuple.startTimestamp
        var numTasks = 0
        var totalDiskSpill = 0L
        var totalBytesRead = 0L
        var totalBytesWritten = 0L
        var totalResultSize = 0L
        var totalShuffleReadBytes = 0L
        var totalShuffleWriteBytes = 0L
        for(jobMetricTuple <- jobMetricTuples){
            numTasks += jobMetricTuple.numTasks
            totalDiskSpill += jobMetricTuple.totalDiskSpill
            totalBytesRead += jobMetricTuple.totalBytesRead
            totalBytesWritten += jobMetricTuple.totalBytesWritten
            totalResultSize += jobMetricTuple.totalResultSize
            totalShuffleReadBytes += jobMetricTuple.totalShuffleReadBytes
            totalShuffleWriteBytes += jobMetricTuple.totalShuffleWriteBytes
        }
        val queryExecution = SQLExecution.getQueryExecution(executionEnd.executionId)


        QueryMetricTuple(queryMetricTuple.executionId,
            queryMetricTuple.description,
            queryMetricTuple.startTimestamp,
            queryMetricTuple.sparkPlan,
            executionEnd.time,
            numTasks, totalDiskSpill, totalBytesRead, totalBytesWritten, totalResultSize, totalShuffleReadBytes, totalShuffleWriteBytes, Try(queryExecution.logical.toString()).getOrElse(""), Try(queryExecution.optimizedPlan.toString()).getOrElse(""), Try(queryExecution.executedPlan.toString()).getOrElse(""), Try(queryExecution.stringWithStats).getOrElse(""), duration
        )
    }



    def serialize(executionStart: SparkListenerSQLExecutionStart) = {
        val physicalPlanGraph = SparkPlanGraph (executionStart.sparkPlanInfo)
        val sqlPlanMetrics = physicalPlanGraph.allNodes.flatMap {node =>
            node.metrics.map (metric => metric.accumulatorId -> metric)
        }

        QueryMetricTuple(executionStart.executionId,
            executionStart.description,
            executionStart.time,
            executionStart.sparkPlanInfo.simpleString)

    }
}
