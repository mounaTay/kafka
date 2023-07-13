package com.stream

import java.util.Properties
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._


object KStreamCapitalize {
  def main(args: Array[String]): Unit = {
    val config = new Properties()
    val bootstrapServers = sys.env.getOrElse("BOOTSTRAP_SERVERS", "localhost:9092")
    val inputTopic = sys.env.getOrElse("INPUT_TOPIC", "input-topic")
    val outputTopic = sys.env.getOrElse("OUTPUT_TOPIC", "output-topic")

    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-example")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)

    // topology
    val builder: StreamsBuilder = new StreamsBuilder()

    val stream: KStream[String, String] = builder.stream(inputTopic)
    val transformedStream: KStream[String, String] = stream.mapValues(value => value.toUpperCase())
    transformedStream.to(outputTopic)

    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    streams.start()

    // // Add shutdown hook to gracefully close the streams application
    // Runtime.getRuntime.addShutdownHook(new Thread(() => {
    //   streams.close()
    // }))
  }
}
