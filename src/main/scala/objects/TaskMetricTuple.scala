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

package com.iresium.airavat


case class TaskMetricTuple(
                        id: String,
                        taskId: Long,
                        stageId: Long,
                        jobId: Int,
                        executorId: Option[String],
                        attemptNumber: Option[Int],
                        duration: Option[Long],
                        gettingResultTime: Option[Long],
                        diskBytesSpilled: Option[Long],
                        executorCpuTime: Option[Long],
                        executorDeserializeCpuTime: Option[Long],
                        executorDeserializeTime: Option[Long],
                        executorRunTime: Option[Long],
                        inputBytesRead: Option[Long],
                        inputRecordsRead: Option[Long],
                        jvmGCTime: Option[Long],
                        memoryBytesSpilled: Option[Long],
                        outputBytesWritten: Option[Long],
                        outputRecordsWritten: Option[Long],
                        peakExecutionMemory: Option[Long],
                        resultSerializationTime: Option[Long],
                        resultSize: Option[Long],
                        shuffleReadFetchWaitTime: Option[Long],
                        shuffleReadLocalBlocksFetched: Option[Long],
                        shuffleReadLocalBytesRead: Option[Long],
                        shuffleReadOutputRecordsRead: Option[Long],
                        shuffleReadRemoteBlocksFetched: Option[Long],
                        shuffleReadRemoteBytesRead: Option[Long],
                        shuffleReadRemoteBytesReadToDisk: Option[Long],
                        shuffleWriteBytesWritten: Option[Long],
                        shuffleWriteRecordsWritten: Option[Long],
                        shuffleWriteWriteTime: Option[Long],
                        timestamp: String = java.time.LocalDateTime.now.toString)


