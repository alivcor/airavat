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

class AiravatQueries(tag: Tag) extends Table[(String, Long, String, String, Long, String, Long, Int, Long, Long, Long, Long, Long, Long, Long)](tag, "AIRAVAT_QUERY_INFO") {
    def appId = column[String]("appId")
    def executionId = column[Long]("executionId")
    def description = column[String]("description")
    def details = column[String]("details")
    def startTimestamp = column[Long]("startTimestamp")
    def sparkPlan = column[String]("sparkPlan")
    def endTimestamp = column[Long]("endTimestamp")
    def numTasks = column[Int]("numTasks")
    def totalDiskSpill = column[Long]("totalDiskSpill")
    def totalBytesRead = column[Long]("totalBytesRead")
    def totalBytesWritten = column[Long]("totalBytesWritten")
    def totalResultSize = column[Long]("totalResultSize")
    def totalShuffleReadBytes = column[Long]("totalShuffleReadBytes")
    def totalShuffleWriteBytes = column[Long]("totalShuffleWriteBytes")
    def duration = column[Long]("duration")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (appId, executionId, description, details, startTimestamp, sparkPlan, endTimestamp, numTasks, totalDiskSpill, totalBytesRead, totalBytesWritten, totalResultSize, totalShuffleReadBytes, totalShuffleWriteBytes, duration)
}


