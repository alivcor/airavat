package com.iresium.airavat

/*
 * Created by @alivcor (Abhinandan Dubey) on 2/3/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

import scala.util.Try


object MetricAnalyzer extends Logging {
    def analyzeJobMetrics(jobMetricsTuple: JobMetricTuple) = {
        val spark = SparkSession.builder().getOrCreate()
        if(jobMetricsTuple.totalDuration > Try(spark.conf.get("spark.airavat.maxTotalDuration").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalDuration ${jobMetricsTuple.totalDuration} of ${Try(spark.conf.get("spark.airavat.maxTotalDuration").toLong).getOrElse(scala.Long.MaxValue)}")
spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")

        }

    }


    def analyzeTaskMetrics(taskMetricsTuple: TaskMetricTuple) = {
        val spark = SparkSession.builder().getOrCreate()



    }

}

