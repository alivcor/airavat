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
    totalShuffleWriteBytes: Long, // derived metric,
    killedCause: String, // derived metric,
    timestamp: String = java.time.LocalDateTime.now().toString
)

