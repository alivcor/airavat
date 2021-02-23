name := "airavat"

version := "0.1"

scalaVersion := "2.11.5"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-hive" % "2.4.0"

libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "9.4.0.M0"
libraryDependencies += "org.eclipse.jetty" % "jetty-servlets" % "9.4.0.M0"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.6"

libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "3.3.0",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.0",
    "org.postgresql" % "postgresql" % "42.2.5", //org.postgresql.ds.PGSimpleDataSource dependency,
    "org.xerial" %  "sqlite-jdbc" % "3.31.1"
)