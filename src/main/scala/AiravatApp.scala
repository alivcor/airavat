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

package com.iresium.airavat

import org.apache.spark.sql.SparkSession
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object AiravatApp extends App {

    val DB_NAME = "airavat_db"
    def createJobsTable() = {

        val airavatJobs = TableQuery[AiravatJobMetric]
        val db = Database.forConfig(DB_NAME)

        val setup = DBIO.seq(
            (airavatJobs.schema).createIfNotExists
        )

        val setupFuture = db.run(setup)

        setupFuture onComplete {
            case Success(v) => println(v)
            case Failure(t) => println("An error has occurred: " + t.getMessage)
        }

        Await.result(setupFuture, Duration.Inf)
    }


    def createQueriesTable() = {
        val airavatQueries = TableQuery[AiravatQueryMetric]
        val db = Database.forConfig(DB_NAME)
        //        val config: Config = ConfigFactory.load("assets/application.conf")

        val setup = DBIO.seq(
            (airavatQueries.schema).createIfNotExists
        )

        val setupFuture = db.run(setup)

        setupFuture onComplete {
            case Success(v) => println(v)
            case Failure(t) => println("An error has occurred: " + t.getMessage)
        }

        Await.result(setupFuture, Duration.Inf)
    }


    def createPlansTable() = {
        val airavatPlans = TableQuery[AiravatQueryPlan]
        val db = Database.forConfig(DB_NAME)

        val setup = DBIO.seq(
            (airavatPlans.schema).createIfNotExists
        )

        val setupFuture = db.run(setup)

        setupFuture onComplete {
            case Success(v) => println(v)
            case Failure(t) => println("An error has occurred: " + t.getMessage)
        }

        Await.result(setupFuture, Duration.Inf)
    }

    def createAppsTable() = {
        val airavatApps = TableQuery[AiravatApplication]
        val db = Database.forConfig(DB_NAME)

        val setup = DBIO.seq(
            (airavatApps.schema).createIfNotExists
        )

        val setupFuture = db.run(setup)

        setupFuture onComplete {
            case Success(v) => println(v)
            case Failure(t) => println("An error has occurred: " + t.getMessage)
        }

        Await.result(setupFuture, Duration.Inf)
    }

    createQueriesTable()
    createJobsTable()
    createPlansTable()
    createAppsTable()


    val spark = SparkSession
        .builder()
        .master("local")
        .appName("My Spark Application")
        .config("spark.extraListeners", "com.iresium.airavat.AiravatJobListener")
        .config("spark.airavat.collectJobMetrics", "true")
        .config("spark.airavat.collectQueryMetrics", "true")
        .config("spark.airavat.collectQueryPlan", "true")
        .config("spark.debug.maxToStringFields", 20000)
        .getOrCreate()

    val df = spark.read.option("header", true).csv("data/netflix_titles.csv")

    df.show()

    df.createOrReplaceTempView("netflix")

    val joinQueryDF = spark.sql("SELECT m1.title AS Movie1, m2.title AS Movie2 FROM netflix m1, netflix m2 WHERE m1.release_year = m2.release_year AND m1.type = 'Movie' and m2.type = 'Movie' ORDER BY m1.country")
    joinQueryDF.show(truncate = false)


    System.in.read()


}
