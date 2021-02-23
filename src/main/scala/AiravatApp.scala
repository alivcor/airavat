package com.iresium.airavat

import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

// Use H2Profile to connect to an H2 database

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

object AiravatApp extends App {
//
//    val spark = SparkSession
//        .builder()
//        .master("local")
//        .appName("My Spark Application")
//        .config("spark.extraListeners", "com.iresium.airavat.AiravatListener")
//        .config("spark.airavat.maxTotalDuration", "3600")
//        .config("spark.airavat.collectJobMetrics", "true")
//        .getOrCreate()
//
//    val df = spark.read.option("header", true).csv("data/netflix_titles.csv")
//    df.show()
//


    val airavatJobs = TableQuery[AiravatJobs]
    val db = Database.forConfig("sqlite1")


    //#create
    val setup = DBIO.seq(
        // Create the tables, including primary and foreign keys
        (airavatJobs.schema).create,

        // Insert some suppliers
        airavatJobs += (1,1L,0,131L,0L,66536L,0L,8826L,0L,1L,"2021-02-22T17:06:20.756")

    )

    val setupFuture = db.run(setup)

    setupFuture onComplete {
        case Success(v) => println(v)
        case Failure(t) => println("An error has occurred: " + t.getMessage)
    }

    Await.result(setupFuture, Duration.Inf)
}
