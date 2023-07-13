//import java.util.Properties
//import java.util.concurrent.TimeUnit
//
//import org.apache.kafka.streams.kstream.Materialized
//import org.apache.kafka.streams.scala.ImplicitConversions._
//import org.apache.kafka.streams.scala._
//import org.apache.kafka.streams.scala.kstream._
//import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
//
//object WordCountApplication extends App {
//  import Serdes._
//  val brokers = sys.env.getOrElse("KAFKA_SERVICE", "localhost:9092,kafka-broker:9092,kafka-service:9092")
//
//  val props: Properties = new Properties()
//    .put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application")
//    .put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
//
//  val builder: StreamsBuilder = new StreamsBuilder
//  val textLines: KStream[String, String] = builder.stream[String, String]("TextLinesTopic")
//  val wordCounts: KTable[String, Long] = textLines
//    .flatMapValues(textLine => textLine.toLowerCase.split("\\W+"))
//    .groupBy((_, word) => word)
//    .count()(Materialized.as("counts-store"))
//  wordCounts.toStream.to("WordsWithCountsTopic")
//
//  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
//  streams.start()
//
//  sys.ShutdownHookThread {
//    streams.close(10, TimeUnit.SECONDS)
//  }
//}