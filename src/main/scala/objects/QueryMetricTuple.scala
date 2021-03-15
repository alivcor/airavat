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
