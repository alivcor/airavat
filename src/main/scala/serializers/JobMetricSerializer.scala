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


import org.apache.spark.scheduler.{SparkListenerJobStart, SparkListenerTaskEnd}
import scala.util.Try

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
            stageInfoTuple.toSeq.map(_.numTasks).sum, ""
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


    def markJobKilled(jobMetricTuple: JobMetricTuple, killedCause: String): JobMetricTuple = {
        jobMetricTuple.copy(killedCause = killedCause)
    }


}

