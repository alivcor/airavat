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


