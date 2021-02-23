package com.iresium.airavat
/*
 * Created by @alivcor (Abhinandan Dubey) on 2/11/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
