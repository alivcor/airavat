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

package com.iresium.airavat

import slick.jdbc.PostgresProfile.api._

class AiravatApplication(tag: Tag) extends Table[(String, String, String, Long, Long)](tag, "AIRAVAT_APPLICATIONS") {
    def hostname = column[String]("hostname")
    def ipAddress = column[String]("ipAddress")
    def appId = column[String]("appId")
    def startTimestamp = column[Long]("startTimestamp")
    def endTimestamp = column[Long]("endTimestamp")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (hostname, ipAddress, appId, startTimestamp, endTimestamp)
}


