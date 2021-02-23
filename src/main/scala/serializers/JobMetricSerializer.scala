package com.iresium.airavat

import org.apache.spark.scheduler.{SparkListenerJobStart, SparkListenerTaskEnd}

import scala.util.Try
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

object JobMetricSerializer {
    def serialize(jobStart: SparkListenerJobStart) = {
        val rddInfoTuple = jobStart.stageInfos.flatMap { stageInfo =>
            stageInfo.rddInfos.map(
                rddInfoNode =>
                    RDDInfoTuple(rddInfoNode.id,
                        rddInfoNode.memSize,
                        rddInfoNode.diskSize,
                        rddInfoNode.numPartitions,
                        rddInfoNode.storageLevel)
            ).toSeq
        }

        val stageInfoTuple = jobStart.stageInfos.map { stageInfo =>
            StageInfoTuple(
                stageInfo.stageId,
                stageInfo.details,
                stageInfo.completionTime,
                stageInfo.submissionTime,
                stageInfo.numTasks
            )
        }

        JobMetricTuple(
            jobStart.jobId,
            Try(jobStart.properties.getProperty("spark.job.description")).getOrElse(""),
            jobStart.stageIds,
            jobStart.stageInfos.size,
            stageInfoTuple.toSeq,
            rddInfoTuple.toSeq, 0, 0, 0, 0, 0, 0, 0,
            stageInfoTuple.toSeq.map(_.numTasks).sum
        )


    }

    def updateDerivedMetrics(currentState: JobMetricTuple, taskEnd: SparkListenerTaskEnd, taskMetricTuple: TaskMetricTuple) = {
        val totalDuration: Long = currentState.totalDuration
        val totalDiskSpill: Long = currentState.totalDiskSpill
        val totalBytesRead: Long = currentState.totalBytesRead
        val totalBytesWritten: Long = currentState.totalBytesWritten
        val totalResultSize: Long = currentState.totalResultSize
        val totalShuffleReadBytes: Long = currentState.totalShuffleReadBytes
        val totalShuffleWriteBytes: Long = currentState.totalShuffleWriteBytes

        currentState.copy(totalDuration = totalDuration + taskMetricTuple.duration.getOrElse[Long](0),
            totalDiskSpill = totalDiskSpill + taskMetricTuple.diskBytesSpilled.getOrElse[Long](0),
            totalBytesRead = totalBytesRead + taskMetricTuple.inputBytesRead.getOrElse[Long](0),
            totalBytesWritten = totalBytesWritten + taskMetricTuple.outputBytesWritten.getOrElse[Long](0),
            totalResultSize = totalResultSize + taskMetricTuple.resultSize.getOrElse[Long](0),
            totalShuffleReadBytes = totalShuffleReadBytes + taskMetricTuple.shuffleReadLocalBytesRead.getOrElse[Long](0) + taskMetricTuple.shuffleReadRemoteBytesRead.getOrElse[Long](0),
            totalShuffleWriteBytes = totalShuffleWriteBytes + taskMetricTuple.shuffleWriteBytesWritten.getOrElse[Long](0)

        )

    }


}

