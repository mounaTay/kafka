version := "1.0"

lazy val root = (project in file(".")).
  settings(
    name := "spark-stream",
    organization := "com.stream",
    version := "1.0",
    scalaVersion := "2.12.12",
    sbtVersion := "1.6.1"
  )

val sparkVersion = "3.2.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "com.datastax.spark" %% "spark-cassandra-connector" % "3.2.0",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.kafka" %% "kafka" % "3.2.1",
  "org.apache.kafka" % "connect-runtime" % "3.1.0",
  "io.spray" %%  "spray-json" % "1.3.2",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.1",
  // "com.datastax.cassandra" % "cassandra-driver-core" % "4.0.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion
)


dependencyOverrides ++= Seq(
  "com.google.guava" % "guava" % "18.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.17.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.17.0"
)
