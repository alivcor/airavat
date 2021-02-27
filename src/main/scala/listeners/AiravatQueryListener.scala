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
        }

        currentExecutionId += 1
    }



    def logQueryPlan(appId: String, serializedPlanInfo: QueryPlanTuple, exceptionStackTrace: String) = {

        try{
            val addPlanSeq = DBIO.seq(
                airavatQueryPlan  += (appId,
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