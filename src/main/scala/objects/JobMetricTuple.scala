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

case class JobMetricTuple(
    jobId: Int,
    jobDescription: String,
    stageIds: Seq[Int],
    numStages: Long,
    stageInfo: Seq[StageInfoTuple],
    rddInfo: Seq[RDDInfoTuple],
    numTasks: Int,
    totalDuration: Long, // derived metric
    totalDiskSpill: Long, // derived metric
    totalBytesRead: Long, // derived metric
    totalBytesWritten: Long, // derived metric
    totalResultSize: Long, // derived metric
    totalShuffleReadBytes: Long, // derived metric
    totalShuffleWriteBytes: Long, // derived metric
    timestamp: String = java.time.LocalDateTime.now().toString
)

