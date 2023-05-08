package com.stream

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.{SparkSession, Encoders}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.sql.functions._

// SPARK STREAMING
object Consumer {

  def main(args: Array[String]): Unit = {
    val db_host = sys.env.getOrElse("DB_HOST", "localhost")
    val db_port = sys.env.getOrElse("DB_PORT", "9042")
    val spark_master = sys.env.getOrElse("SPARK_MASTER_URL", "local[*]")

    val broker = sys.env.getOrElse("KAFKA_SERVICE", "localhost:9092,kafka-broker:9092,kafka-service:9092")
    val topic = sys.env.getOrElse("TOPIC", "dummy-topic")

    val conf = new SparkConf().setMaster(spark_master).setAppName("consumer")
    val spark = SparkSession
          .builder()
          .config(conf)
          .config("spark.cassandra.connection.host", db_host)
          .config("spark.cassandra.connection.port", db_port)
          .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")
    val df = spark.readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", broker)
            .option("subscribe", topic)
            .option("startingOffsets", "earliest") // From starting
            .load()

    df.selectExpr("CAST(value AS STRING)")
      .select(from_json(col("value"), Encoders.product[Store].schema).as("data"))
      .select("data.*")
      .withColumn("storetype", concat(lit("streamed_"), col("storetype")))
      .writeStream
      .format("console")
      // .format("org.apache.spark.sql.cassandra")
      .outputMode("append")
      .start()
      .awaitTermination()
  }
}
