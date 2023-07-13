version := "1.0"

lazy val root = (project in file(".")).
  settings(
    name := "kafka-stream",
    organization := "com.stream",
    scalaVersion := "2.12.7",
    sbtVersion := "1.6.1"
  )
val kafka = "3.4.0"
val kafkaAvro = "6.2.0"
val circeVersion = "0.14.5"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.apache.kafka" %% "kafka" % kafka,
  "org.apache.kafka" % "kafka-clients" % kafka,
  "org.apache.kafka" % "kafka-streams" % kafka,
  "org.apache.kafka" %% "kafka-streams-scala" % kafka
)