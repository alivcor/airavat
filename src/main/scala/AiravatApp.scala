package com.iresium.airavat

import org.apache.spark.sql.SparkSession
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


    def createJobsTable() = {

        val airavatJobs = TableQuery[AiravatJobs]
        val db = Database.forConfig("sqlite1")
//        val config: Config = ConfigFactory.load("assets/application.conf")

        //#create
        val setup = DBIO.seq(
            // Create the tables, including primary and foreign keys
            (airavatJobs.schema).create
//            // Insert some suppliers
//            airavatJobs += ("test", 1,1L,0,131L,0L,66536L,0L,8826L,0L,1L,"2021-02-22T17:06:20.756")
        )

        val setupFuture = db.run(setup)

        setupFuture onComplete {
            case Success(v) => println(v)
            case Failure(t) => println("An error has occurred: " + t.getMessage)
        }

        Await.result(setupFuture, Duration.Inf)
    }


    def createQueriesTable() = {
        val airavatQueries = TableQuery[AiravatQueries]
        val db = Database.forConfig("sqlite1")
        //        val config: Config = ConfigFactory.load("assets/application.conf")

        //#create
        val setup = DBIO.seq(
            // Create the tables, including primary and foreign keys
            (airavatQueries.schema).create
        )

        val setupFuture = db.run(setup)

        setupFuture onComplete {
            case Success(v) => println(v)
            case Failure(t) => println("An error has occurred: " + t.getMessage)
        }

        Await.result(setupFuture, Duration.Inf)
    }

//    createQueriesTable()
    val spark = SparkSession
        .builder()
        .master("local")
        .appName("My Spark Application")
        .config("spark.extraListeners", "com.iresium.airavat.AiravatJobListener")
        .config("spark.airavat.maxTotalDuration", "3600")
        .config("spark.airavat.collectJobMetrics", "true")
        .getOrCreate()

//    spark.sessionState.listenerManager.register(new AiravatQueryListener)
    val df = spark.read.option("header", true).csv("data/netflix_titles.csv")

    df.show()

    df.createOrReplaceTempView("netflix")

    val bollywoodShows = spark.sql("SELECT * FROM netflix WHERE country='India'")
    bollywoodShows.show()



}
