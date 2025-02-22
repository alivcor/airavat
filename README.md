# Airavat
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.png?v=103)](https://github.com/alivcor/airavat)
[![Build Status](https://travis-ci.org/alivcor/airavat.svg?branch=master)](https://travis-ci.org/alivcor/airavat)

<p align="center">
<img src="https://github.com/alivcor/airavat/raw/master/assets/airavat_logo.png" width="150px"/>
</p>

Airavat is a metric interceptor and a job watchdog for Spark Applications. It also features an interactive UI which shows all Spark Applications running, jobs and SQL Queries along with their metrics.

![AiravatUI](ui/public/ui.png)


<a href="https://github.com/alivcor/airavat">:octocat: Link to GitHub Repo</a>

## Features

- :heavy_check_mark: Super-fast integration, just add the jar and you're good to go
- :heavy_check_mark: Monitor Jobs, Queries and Executions
- :heavy_check_mark: Collect Job Metrics such as Disk Spill, Shuffle Read Data, Result Size etc.
- :heavy_check_mark: Collect Query Plans and view them on the UI
- :heavy_check_mark: Set Limits / Thresholds for maximum shuffle / maximum result size / duration of a job.
- :heavy_check_mark: Kill potentially disruptive / resource hogging jobs before they impact the overall health and stability of your application.
- :heavy_check_mark: Trace which jobs belong to which queries and have this information persisted for analysis even after your application shuts down.
- :heavy_check_mark: Aggregate Data across all spark applications
- :heavy_check_mark: Works with all spark application modes - local, yarn-client, cluster etc.
- :heavy_check_mark: (Coming Soon) - Predict run times / duration for your spark queries!


### Prerequisites

 - Spark 2.3.0 + 
 - Scala / sbt to compile and build the jar
 - ReactJS, Node/NPM and dependencies for the frontend/UI
 - FastAPI, SQLAlchemy for the server.

### Installation

#### 1. Clone the project

```bash
      git clone https://github.com/alivcor/airavat.git
```

#### 2. Build the jar

```bash
      sbt clean package publishLocal
```

#### 3. Test Run

To test run, download the <a href="https://www.kaggle.com/shivamb/netflix-shows" target="_blank">Netflix Data CSV file from Kaggle</a> and place it under `data/` directory.

```bash
      sbt run
```

#### 4. Add The Jar to your Spark Application

Linux/MacOS

```bash
    --conf "spark.extraListeners=com.iresium.airavat.AiravatJobListener"
    --conf "spark.airavat.collectJobMetrics=true"
    --conf "spark.airavat.collectQueryMetrics=true"
    --conf "spark.airavat.collectQueryPlan=true"
    --conf "spark.airavat.collectJobMetrics=true"
    --jars /path/to/airavat-0.1.jar
```

Scala Application

```scala
val spark = SparkSession
        .builder()
        .master("local")
        .appName("My Spark Application")
        .config("spark.extraListeners", "com.iresium.airavat.AiravatJobListener")
        .config("spark.airavat.collectJobMetrics", "true")
        .config("spark.airavat.collectQueryMetrics", "true")
        .config("spark.airavat.collectQueryPlan", "true")
        .getOrCreate()
```


### Setting up the Backend Server


#### 1. Install Dependencies

```bash
      cd server
      pip install -r requirements.txt
```

#### 2. Start the server

```bash
      uvicorn main:app
```

You can also run it with nohup as a daemon process `nohup main:app >> airavat_server.log &`

### Setting up the Frontend Server


#### 1. Install Dependencies

```bash
      cd ui
      npm install
```

#### 2. Start the server

```bash
      npm start
```

### Building Airavat for Spark 3.0+

```
1. Change the `sparkVersion` to desired spark version
2. Build the sbt package again.
3. Make sure to update the jars.
```


### Configuration

### Configuring Limits

Currently, Airavat supports following limits:

- spark.airavat.maxTotalDuration
- spark.airavat.maxTotalTasks
- spark.airavat.maxTotalDiskSpill
- spark.airavat.maxTotalBytesRead
- spark.airavat.maxTotalBytesWritten
- spark.airavat.maxTotalResultSize
- spark.airavat.maxTotalShuffleReadBytes
- spark.airavat.maxTotalShuffleWriteBytes
- spark.airavat.maxJobDuration

### Configuring Database

Airavat uses <a href="http://scala-slick.org/" target="_blank">Slick - Functional Relational Mapping for scala</a> which can be configured to be used with different Databases.

Out of the box Airavat uses SQLite - (my favorite and the simplest DB in the world!). However, this can be changed based on resource and capacity requirements. By default, airavat creates and uses `airavat_db` as Database identifier which is picked from `application.conf` (under `src/main/resources/` in the source code), you can add a db configuration there, and then set `spark.airavat.dbName` if your identifier is anything other than `airavat_db`


```
airavat_db = {
  driver = "org.sqlite.JDBC",
  url = "jdbc:sqlite:/path/to/airavat.db",
  connectionPool = disabled
  keepAliveConnection = true
}
```

The backend server however uses SQLAlchemy to establish database connections. Please make sure to point your backend server to also point to the same database as your Spark Application!

The backend server uses a config file `config/server.conf`. Configure the SQLAlchemy URL to your database in the config file.

```python
[database]
url = sqlite:////path/to/airavat.db
```

#### Using AWS DynamoDB as a sink

DynamoDB can be used as a sink for Airavat. Set the SQLAlchemy URL as

```python
[database]
url = amazondynamodb:///?Access Key=xxx&Secret Key=xxx&Domain=amazonaws.com&Region=OREGON
```

Optionally, you can add support to use <a href="https://github.com/pynamodb/PynamoDB">PynamoDB</a> which provides a richer interface

 
### Configuring Settings

The setting `spark.airavat.dbTimeoutSeconds` dictates the timeout for Airavat to persist the metrics/data to the DB on a Job End event. All actions are best-effort and never guaranteed. Timeout is set to 120 seconds by default.

Airavat retains several maps in memory which hold job and query info. Most of the elements are evicted from the map automatically. However, if jobs are not attached to a SQL, or are not evicted because of a failure, airavat periodically evicts the maps to avoid creating a huge memory profile. The setting `spark.airavat.maxJobRetain` dictates how many jobs airavat retains in memory at any given moment.

## Contributing

Looking for contributors ! You are welcome to raise issues / send a pull-request.

I haven't been able to add comments to the code and a lot of the code here has been hastily written. Suggestions are welcome and pull requests are highly encouraged. 

Link to ScalaDoc/JavaDoc can be found <a href="https://alivcor.github.io/airavat/#com.iresium.airavat.package" target="_blank">here</a>

## Authors

* **Abhinandan Dubey** - *@alivcor*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

[![forthebadge](http://forthebadge.com/images/badges/makes-people-smile.svg)](https://github.com/alivcor/airavat/#)

<a href="https://www.buymeacoffee.com/abhinandandubey" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>