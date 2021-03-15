/*
 * Created by @alivcor (Abhinandan Dubey) on 2/22/21
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

import slick.jdbc.PostgresProfile.api._

class AiravatQueryPlan(tag: Tag) extends Table[(String, String, String, Long, String, Long, Long, String, String, String, String, String, Long, String, String, String)](tag, "AIRAVAT_QUERY_PLAN_INFO") {
    def hostname = column[String]("hostname")
    def ipAddress = column[String]("ipAddress")
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
    def * = (hostname, ipAddress, appId, executionId, description, startTimestamp, endTimestamp, sparkPlan, logicalPlan, optimizedPlan, executedPlan, queryStats, duration, metrics, serializedPlan, exceptionStackTrace)
}


