/*
 * Created by @alivcor (Abhinandan Dubey) on 2/11/21
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

import org.apache.spark.scheduler._

object TaskMetricSerializer {
    def serialize(taskEnd: SparkListenerTaskEnd, jobMap: scala.collection.mutable.Map[Int, Int]) = {

        val taskMetric = taskEnd.taskMetrics

        TaskMetricTuple(
            taskEnd.taskInfo.id: String,
            taskEnd.taskInfo.taskId: Long,
            taskEnd.stageId: Long,
            jobMap(taskEnd.stageId): Int,
            Option(taskEnd.taskInfo.executorId): Option[String],
            Option(taskEnd.taskInfo.attemptNumber): Option[Int],
            Option(taskEnd.taskInfo.duration): Option[Long],
            Option(taskEnd.taskInfo.gettingResultTime): Option[Long],
            Option(taskMetric.diskBytesSpilled): Option[Long],
            Option(taskMetric.executorCpuTime): Option[Long],
            Option(taskMetric.executorDeserializeCpuTime): Option[Long],
            Option(taskMetric.executorDeserializeTime): Option[Long],
            Option(taskMetric.executorRunTime): Option[Long],
            Option(taskMetric.inputMetrics.bytesRead): Option[Long],
            Option(taskMetric.inputMetrics.recordsRead): Option[Long],
            Option(taskMetric.jvmGCTime): Option[Long],
            Option(taskMetric.memoryBytesSpilled): Option[Long],
            Option(taskMetric.outputMetrics.bytesWritten): Option[Long],
            Option(taskMetric.outputMetrics.recordsWritten): Option[Long],
            Option(taskMetric.peakExecutionMemory): Option[Long],
            Option(taskMetric.resultSerializationTime): Option[Long],
            Option(taskMetric.resultSize): Option[Long],
            Option(taskMetric.shuffleReadMetrics.fetchWaitTime): Option[Long],
            Option(taskMetric.shuffleReadMetrics.localBlocksFetched): Option[Long],
            Option(taskMetric.shuffleReadMetrics.localBytesRead): Option[Long],
            Option(taskMetric.shuffleReadMetrics.recordsRead): Option[Long],
            Option(taskMetric.shuffleReadMetrics.remoteBlocksFetched): Option[Long],
            Option(taskMetric.shuffleReadMetrics.remoteBytesRead): Option[Long],
            Option(taskMetric.shuffleReadMetrics.remoteBytesReadToDisk): Option[Long],
            Option(taskMetric.shuffleWriteMetrics.bytesWritten): Option[Long],
            Option(taskMetric.shuffleWriteMetrics.recordsWritten): Option[Long],
            Option(taskMetric.shuffleWriteMetrics.writeTime): Option[Long])

    }

}
