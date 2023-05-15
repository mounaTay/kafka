package com.stream

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters.{iterableAsScalaIterableConverter, seqAsJavaListConverter}
import spray.json._

// KAFKA STREAMING
object Consume {

  case class Data(a: Long,
                  b: Double,
                  c: Double)

case class Store (store: Int,
  storetype: String,
  assortment: String,
  competitiondistance: Int,
  competitionopensinceMmonth: Int,
  competitionopensinceyear: Int,
  promo2: Int,
  promo2sinceweek: Int,
  promo2sinceyear: Int,
  promointerval: String)
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val dataFormat: RootJsonFormat[Store] = jsonFormat10(Store)
  }

  def main(args: Array[String]): Unit = {
    val db_host = sys.env.getOrElse("DB_HOST", "localhost")
    val db_port = sys.env.getOrElse("DB_PORT", "9042")

    val broker = sys.env.getOrElse("KAFKA_SERVICE", "localhost:9092,kafka-broker:9092,kafka-service:9092")
    val topics = Seq("dummy-topic", "test")

    val props = new Properties()
    props.put("group.id", "test-consumer-group")
    props.put("bootstrap.servers", broker)
    props.put("key.deserializer", classOf[StringDeserializer].getName)
    props.put("value.deserializer", classOf[StringDeserializer].getName)

    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(topics.asJava)

    import MyJsonProtocol._
    while (true) {
      val records = consumer.poll(Duration.ofMillis(1000))
      if(!records.isEmpty()) {
      records.asScala.foreach(record => {
        println(s"received $record")
        println(record.value().parseJson.convertTo[Store])
      })
      }

    }
  }
}
