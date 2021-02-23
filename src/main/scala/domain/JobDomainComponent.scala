package com.iresium.airavat

/*
 * Created by @alivcor (Abhinandan Dubey) on 2/22/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import slick.jdbc.PostgresProfile.api._

class AiravatJobs(tag: Tag) extends Table[(String, Int, Long, Int, Long, Long, Long, Long, Long, Long, Long, String)](tag, "AIRAVAT_JOB_INFO") {
    def appId = column[String]("appId")
    def jobId = column[Int]("jobId") // This is the primary key column
    def numStages = column[Long]("numStages")
    def numTasks = column[Int]("numTasks")
    def totalDuration = column[Long]("totalDuration")
    def totalDiskSpill = column[Long]("totalDiskSpill")
    def totalBytesRead = column[Long]("totalBytesRead")
    def totalBytesWritten = column[Long]("totalBytesWritten")
    def totalResultSize = column[Long]("totalResultSize")
    def totalShuffleReadBytes = column[Long]("totalShuffleReadBytes")
    def totalShuffleWriteBytes = column[Long]("totalShuffleWriteBytes")
    def timestamp = column[String]("timestamp")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (appId, jobId, numStages, numTasks, totalDuration, totalDiskSpill, totalBytesRead, totalBytesWritten, totalResultSize, totalShuffleReadBytes, totalShuffleWriteBytes, timestamp)
}


