/*
 * Created by @alivcor (Abhinandan Dubey) on 2/26/21 
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

import java.util.concurrent.TimeUnit

import com.google.gson.GsonBuilder
import com.iresium.airavat.sickle.Sickle
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

        QueryPlanTuple(executionId,
            funcName,
            startTimestamp / 1000,
            System.currentTimeMillis()/1000,
            qe.sparkPlan.toString(),
            qe.logical.toString(),
            qe.optimizedPlan.toString(),
            qe.executedPlan.toString(),
            qe.stringWithStats,
            TimeUnit.SECONDS.convert(durationNs, TimeUnit.NANOSECONDS),
            gson.toJson(rwMetrics),
            gson.toJson(cherryBunch)
        )
    }
}
