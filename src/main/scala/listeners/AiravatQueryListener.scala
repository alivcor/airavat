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


import java.net.InetAddress

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.QueryExecution
import org.apache.spark.sql.util.QueryExecutionListener
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}



class AiravatQueryListener(conf: SparkConf)  extends QueryExecutionListener with Logging {

    val logger = LoggerFactory.getLogger(this.getClass)
    var currentExecutionId = 0L
    val db = Database.forConfig(Try(conf.get("spark.airavat.dbName")).getOrElse("airavat_db"))
    val airavatQueryPlan = TableQuery[AiravatQueryPlan]


    override def onSuccess(funcName: String, qe: QueryExecution, durationNs: Long): Unit = {
        val serializedPlanInfo: QueryPlanTuple = QueryPlanSerializer.serialize(currentExecutionId, funcName, qe, durationNs)
        logInfo(s"Execution ID = " + currentExecutionId)
        if(Try(conf.get("spark.airavat.collectQueryPlan").toBoolean).getOrElse(false)) logQueryPlan(qe.sparkSession.sparkContext.applicationId, serializedPlanInfo, "")
        currentExecutionId += 1
    }

    override def onFailure(funcName: String, qe: QueryExecution, exception: Exception): Unit = {
        try {
            val serializedPlanInfo: QueryPlanTuple = QueryPlanSerializer.serialize(currentExecutionId, funcName, qe, 0)
            logInfo(s"Execution ID = " + currentExecutionId)
            if(Try(conf.get("spark.airavat.collectQueryPlan").toBoolean).getOrElse(false)) logQueryPlan(qe.sparkSession.sparkContext.applicationId, serializedPlanInfo, exception.getStackTrace.toString)
        } catch {
            case e: Exception => logWarning(s"An error occured serializing the plan " + e.getStackTrace())
        }

        currentExecutionId += 1
    }



    def logQueryPlan(appId: String, serializedPlanInfo: QueryPlanTuple, exceptionStackTrace: String) = {

        try{
            val addPlanSeq = DBIO.seq(
                airavatQueryPlan  += (InetAddress.getLocalHost.getHostName,
                    InetAddress.getLocalHost.getHostAddress,
                    appId,
                    serializedPlanInfo.executionId,
                    serializedPlanInfo.description,
                    serializedPlanInfo.startTimestamp,
                    serializedPlanInfo.endTimestamp,
                    serializedPlanInfo.sparkPlan,
                    serializedPlanInfo.logicalPlan,
                    serializedPlanInfo.optimizedPlan,
                    serializedPlanInfo.executedPlan,
                    serializedPlanInfo.queryStats,
                    serializedPlanInfo.duration,
                    serializedPlanInfo.metrics,
                    serializedPlanInfo.serializedPlan,
                    exceptionStackTrace)
            )
            val logQueryPlanF = db.run(addPlanSeq)


            logQueryPlanF onComplete {
                case Success(v) => logger.info("Logged metrics for Query " + serializedPlanInfo.executionId + " to the sink")
                case Failure(t) => logger.warn("An error occurred while logging queryMetrics to sink: " + t.getMessage)
            }

            Await.result(logQueryPlanF, 120 seconds)

        } catch {
            case e: Exception => { logger.warn(s"Failed to log queryMetrics to sink: " + e.getMessage)}
        }
        //            finally db.close

    }

}