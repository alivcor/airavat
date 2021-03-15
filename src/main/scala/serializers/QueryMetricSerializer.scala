/*
 * Created by @alivcor (Abhinandan Dubey) on 2/23/21
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

import org.apache.spark.sql.execution.ui.{SparkListenerSQLExecutionEnd, SparkListenerSQLExecutionStart, SparkPlanGraph}


object QueryMetricSerializer {

    private def _updateMetrics(executionEnd: SparkListenerSQLExecutionEnd, queryMetricTuple: QueryMetricTuple, jobIds: Seq[Int], jobInfo: scala.collection.mutable.Map[Int, JobMetricTuple] ) = {
        var jobMetricTuples = Seq[JobMetricTuple]()

        for(jobId <- jobIds){
            jobMetricTuples :+ jobInfo(jobId)
        }

        updateMetrics(executionEnd, queryMetricTuple, jobIds.map(jobInfo(_)))
    }



    def updateMetrics(executionEnd: SparkListenerSQLExecutionEnd, queryMetricTuple: QueryMetricTuple, jobMetricTuples: Seq[JobMetricTuple]): QueryMetricTuple = {
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
//        val queryExecution = SQLExecution.getQueryExecution(executionEnd.executionId)

        QueryMetricTuple(queryMetricTuple.executionId,
            jobMetricTuples.map(_.jobId),
            queryMetricTuple.description,
            queryMetricTuple.startTimestamp,
            queryMetricTuple.sparkPlan,
            executionEnd.time,
            numTasks, totalDiskSpill, totalBytesRead, totalBytesWritten, totalResultSize, totalShuffleReadBytes, totalShuffleWriteBytes
        )
    }



    def serialize(executionStart: SparkListenerSQLExecutionStart) = {
        val physicalPlanGraph = SparkPlanGraph (executionStart.sparkPlanInfo)
        val sqlPlanMetrics = physicalPlanGraph.allNodes.flatMap {node =>
            node.metrics.map (metric => metric.accumulatorId -> metric)
        }

        QueryMetricTuple(executionStart.executionId,
            Seq(),
            executionStart.description,
            executionStart.time,
            executionStart.sparkPlanInfo.simpleString)

    }
}
