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

class AiravatQueryPlan(tag: Tag) extends Table[(String, Long, String, Long, Long, String, String, String, String, String, Long, String, String, String)](tag, "AIRAVAT_QUERY_PLAN_INFO") {
    def appId = column[String]("appId")
    def executionId = column[Long]("executionId")
    def description = column[String]("description")
    def startTimestamp = column[Long]("startTimestamp")
    def endTimestamp = column[Long]("endTimestamp")
    def sparkPlan = column[String]("sparkPlan")
    def logicalPlan = column[String]("logicalPlan")
    def optimizedPlan = column[String]("optimizedPlan")
    def executedPlan = column[String]("executedPlan")
    def queryStats = column[String]("queryStats")
    def duration = column[Long]("duration")
    def metrics = column[String]("metrics")
    def serializedPlan = column[String]("serializedPlan")
    def exceptionStackTrace = column[String]("exceptionStackTrace")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (appId, executionId, description, startTimestamp, endTimestamp, sparkPlan, logicalPlan, optimizedPlan, executedPlan, queryStats, duration, metrics, serializedPlan, exceptionStackTrace)
}


