//package com.stream
//
//import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types.{IntegerType, LongType}
//import java.util.Properties
//
//case class Store (store: Int,
//  storetype: String,
//  assortment: String,
//  competitiondistance: Int,
//  competitionopensinceMmonth: Int,
//  competitionopensinceyear: Int,
//  promo2: Int,
//  promo2sinceweek: Int,
//  promo2sinceyear: Int,
//  promointerval: String)
//
//object Producer {
//
//  def main(args: Array[String]): Unit = {
//
//    val spark_master = sys.env.getOrElse("SPARK_MASTER_URL", "local[*]")
//    val broker = sys.env.getOrElse("KAFKA_SERVICE", "localhost:9092,kafka-broker:9092,kafka-service:9092")
//    val topic = sys.env.getOrElse("TOPIC", "dummy-topic")
//
//    val props = new Properties()
//    props.put("bootstrap.servers", broker)
//    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//
//    val producer = new KafkaProducer[String, String](props)
//    val spark = SparkSession.builder()
//      .appName("Producer")
//      .config("spark.master", spark_master)
//      .getOrCreate()
//
//    spark.sparkContext.setLogLevel("ERROR")
//
//    import spark.implicits._
//
//    while (true) {
//      val x = new scala.util.Random
//      val _record = Seq(Store(x.nextInt(),
//                              "type",
//                              "assortment",
//                              x.nextInt(),
//                              x.nextInt(),
//                              x.nextInt(),
//                              x.nextInt(),
//                              x.nextInt(),
//                              x.nextInt(),
//                              "interval"))
//        .toDS()
//        .toJSON
//        .collect()
//        .head
//
//      println(_record)
//      val record = new ProducerRecord(topic, x.nextInt(10).toString, _record)
//      producer.send(record)
//      Thread.sleep(10000)
//    }
//
//    producer.close()
//
//  }
//
//}