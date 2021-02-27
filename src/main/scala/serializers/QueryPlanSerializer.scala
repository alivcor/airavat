/*
 * Created by @alivcor (Abhinandan Dubey) on 2/26/21 
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

import java.util.concurrent.TimeUnit

import com.google.gson.GsonBuilder
import com.iresium.airavat.sickle.Sickle
import objects.QueryInfoTuple
import org.apache.spark.sql.execution.QueryExecution
import org.joda.time.DateTime


object QueryPlanSerializer {

    def serialize(executionId: Long, funcName: String, qe: QueryExecution, durationNs: Long) = {
        val rwMetrics = Sickle.getAggregatedQueryMetrics(qe.executedPlan)
        val cherryBunch = Sickle.cherryPick(qe.executedPlan)
        val startTimestamp = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(durationNs, TimeUnit.NANOSECONDS)
        val gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListSerializer)
            .registerTypeHierarchyAdapter(classOf[Map[Any, Any]], new MapSerializer)
            .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionSerializer)
            .registerTypeAdapter(classOf[DateTime], new DateTimeSerializer)
            .create()

        QueryInfoTuple(executionId,
            funcName,
            startTimestamp / 1000,
            System.currentTimeMillis()/1000,
            qe.sparkPlan.toJSON,
            qe.logical.toJSON,
            qe.optimizedPlan.toJSON,
            qe.executedPlan.toJSON,
            qe.stringWithStats,
            TimeUnit.SECONDS.convert(durationNs, TimeUnit.NANOSECONDS),
            gson.toJson(rwMetrics),
            gson.toJson(cherryBunch)
        )
    }
}
