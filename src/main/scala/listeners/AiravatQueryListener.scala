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


import com.iresium.airavat.sickle.Sickle
import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.QueryExecution
import org.apache.spark.sql.util.QueryExecutionListener
import org.slf4j.LoggerFactory


class AiravatQueryListener extends QueryExecutionListener with Logging {

    val logger = LoggerFactory.getLogger(this.getClass)


    override def onSuccess(funcName: String, qe: QueryExecution, durationNs: Long): Unit = {
        val rwMetrics = Sickle.getAggregatedQueryMetrics(qe.executedPlan)
        val cherryBunch = Sickle.cherryPick(qe.executedPlan)
//        logInfo(rwMetrics.toString())
//        qe.executedPlan.find(_.isInstanceOf[PushedFilters])
        logInfo(s"durationNs " + durationNs)

    }

    override def onFailure(funcName: String, qe: QueryExecution, exception: Exception): Unit = {
        logInfo(s"funcName " + funcName)
        logInfo(s"sparkPlan " + qe.sparkPlan.toString())
    }
}