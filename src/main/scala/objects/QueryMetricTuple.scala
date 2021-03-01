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

case class QueryMetricTuple(executionId: Long,
                            jobIds: Seq[Int],
                            description: String,
                            startTimestamp: Long,
                            sparkPlan: String,
                            endTimestamp: Long = 0L,
                            numTasks: Int= 0,
                            totalDiskSpill: Long= 0L, // derived metric
                            totalBytesRead: Long= 0L, // derived metric
                            totalBytesWritten: Long= 0L, // derived metric
                            totalResultSize: Long= 0L, // derived metric
                            totalShuffleReadBytes: Long = 0L, // derived metric
                            totalShuffleWriteBytes: Long= 0L)

// derived metric
//                            logicalPlan: String = "",
//                            optimizedPlan: String =  "",
//                            executedPlan: String = "",
//                            queryStats: String = "",
//                            duration: Long = 0L
//                            )

//case class QueryMetricTuple(executionId: Long,
//                            description: String,
//                            details: String,
//                            startTimestamp: Long,
//                            simpleString: String,
//                            endTimestamp: Long = 0L,
//                            shuffleRecordsWritten: Long = 0L,
//                            shuffleWriteTime: Long = 0L,
//                            recordsRead: Long = 0L,
//                            localBytesRead: Long = 0L,
//                            fetchWaitTime: Long = 0L,
//                            remoteBytesRead: Long = 0L,
//                            localBlocksRead: Long = 0L,
//                            remoteBlocksRead: Long = 0L,
//                            remoteBytesReadToDisk: Long = 0L,
//                            shuffleBytesWritten: Long = 0L,
//                            numberOfOutputRows: Long = 0L,
//                            duration: Long = 0L,
//                            numberOfFilesRead: Long = 0L,
//                            dynamicPartitionPruningTime: Long = 0L,
//                            metadataTime: Long = 0L,
//                            sizeOfFilesRead: Long = 0L,
//                           )
