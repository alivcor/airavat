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

package com.iresium.airavat

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
        if(jobMetricsTuple.numTasks > Try(spark.conf.get("spark.airavat.maxTotalTasks").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalTasks ${jobMetricsTuple.numTasks} of ${Try(spark.conf.get("spark.airavat.maxTotalTasks").toLong).getOrElse(scala.Long.MaxValue)}")
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalDiskSpill > Try(spark.conf.get("spark.airavat.maxTotalDiskSpill").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalDiskSpill ${jobMetricsTuple.totalDiskSpill} of ${Try(spark.conf.get("spark.airavat.maxTotalDiskSpill").toLong).getOrElse(scala.Long.MaxValue)}")
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalBytesRead > Try(spark.conf.get("spark.airavat.maxTotalBytesRead").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalBytesRead ${jobMetricsTuple.totalBytesRead} of ${Try(spark.conf.get("spark.airavat.maxTotalBytesRead").toLong).getOrElse(scala.Long.MaxValue)}")
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalBytesWritten > Try(spark.conf.get("spark.airavat.maxTotalBytesWritten").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalBytesWritten ${jobMetricsTuple.totalBytesWritten} of ${Try(spark.conf.get("spark.airavat.maxTotalBytesWritten").toLong).getOrElse(scala.Long.MaxValue)}")
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalResultSize > Try(spark.conf.get("spark.airavat.maxTotalResultSize").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalResultSize ${jobMetricsTuple.totalResultSize} of ${Try(spark.conf.get("spark.airavat.maxTotalResultSize").toLong).getOrElse(scala.Long.MaxValue)}")
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalShuffleReadBytes > Try(spark.conf.get("spark.airavat.maxTotalShuffleReadBytes").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalShuffleReadBytes ${jobMetricsTuple.totalShuffleReadBytes} of ${Try(spark.conf.get("spark.airavat.maxTotalShuffleReadBytes").toLong).getOrElse(scala.Long.MaxValue)}")
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalShuffleWriteBytes > Try(spark.conf.get("spark.airavat.maxTotalShuffleWriteBytes").toLong).getOrElse(scala.Long.MaxValue)){
            logWarning(s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalShuffleWriteBytes ${jobMetricsTuple.totalShuffleWriteBytes} of ${Try(spark.conf.get("spark.airavat.maxTotalShuffleWriteBytes").toLong).getOrElse(scala.Long.MaxValue)}")
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }

    }


}

