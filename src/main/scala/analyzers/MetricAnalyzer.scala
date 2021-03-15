/*
 * Created by @alivcor (Abhinandan Dubey) on 2/3/21
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

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

import scala.util.Try


object MetricAnalyzer extends Logging {

    def analyzeJobMetrics(jobMetricsTuple: JobMetricTuple) = {
        val spark = SparkSession.builder().getOrCreate()
        var killCause = ""
        if(jobMetricsTuple.totalDuration > Try(spark.conf.get("spark.airavat.maxTotalDuration").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalDuration ${jobMetricsTuple.totalDuration} of ${Try(spark.conf.get("spark.airavat.maxTotalDuration").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.numTasks > Try(spark.conf.get("spark.airavat.maxTotalTasks").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalTasks ${jobMetricsTuple.numTasks} of ${Try(spark.conf.get("spark.airavat.maxTotalTasks").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalDiskSpill > Try(spark.conf.get("spark.airavat.maxTotalDiskSpill").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalDiskSpill ${jobMetricsTuple.totalDiskSpill} of ${Try(spark.conf.get("spark.airavat.maxTotalDiskSpill").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalBytesRead > Try(spark.conf.get("spark.airavat.maxTotalBytesRead").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalBytesRead ${jobMetricsTuple.totalBytesRead} of ${Try(spark.conf.get("spark.airavat.maxTotalBytesRead").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalBytesWritten > Try(spark.conf.get("spark.airavat.maxTotalBytesWritten").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalBytesWritten ${jobMetricsTuple.totalBytesWritten} of ${Try(spark.conf.get("spark.airavat.maxTotalBytesWritten").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalResultSize > Try(spark.conf.get("spark.airavat.maxTotalResultSize").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalResultSize ${jobMetricsTuple.totalResultSize} of ${Try(spark.conf.get("spark.airavat.maxTotalResultSize").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalShuffleReadBytes > Try(spark.conf.get("spark.airavat.maxTotalShuffleReadBytes").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalShuffleReadBytes ${jobMetricsTuple.totalShuffleReadBytes} of ${Try(spark.conf.get("spark.airavat.maxTotalShuffleReadBytes").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        if(jobMetricsTuple.totalShuffleWriteBytes > Try(spark.conf.get("spark.airavat.maxTotalShuffleWriteBytes").toLong).getOrElse(scala.Long.MaxValue)){
            killCause = s"Airavat : Killing Job ${jobMetricsTuple.jobId} because it breached maxTotalShuffleWriteBytes ${jobMetricsTuple.totalShuffleWriteBytes} of ${Try(spark.conf.get("spark.airavat.maxTotalShuffleWriteBytes").toLong).getOrElse(scala.Long.MaxValue)}"
            logWarning(killCause)
            spark.sparkContext.cancelJob(jobMetricsTuple.jobId, "Potentially disruptive query - Killed by Airavat")
        }
        killCause
    }


}

