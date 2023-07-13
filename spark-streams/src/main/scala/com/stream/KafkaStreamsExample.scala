package com.stream


// serialize messages to json
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.kstream.{GlobalKTable, JoinWindows}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.kstream.Printed

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Properties


import io.confluent.kafka.streams.serdes.avro._
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde


object KafkaStreamsExample {
  private object Domain {
    type UserId = String
    type Profile = String
    type Product = String
    type OrderId = String
    type Status = String

    case class Order(orderId: OrderId, userId: UserId, products: List[Product], amount: Double)

    case class Discount(profile: Profile, amount: Double)

    case class Payment(orderId: OrderId, status: Status)
  }

  private object Topics {
    val OrdersByUser = "orders-by-user"
    val DiscountProfilesByUser = "discount-profiles-by-user"
    val Discounts = "discounts"
    val Payments = "payments"
    val PaidOrders = "paid-orders"
  }

  // stream processors:
  // 1. source = emits elements
  // 2. flow = transforms element along the way (e.g map)
  // 2. sink = "ingests" elements

  import Domain._
  import Topics._

  // create serdes to serialize and deserialize incoming objects to bytes
  implicit def serde[A >: Null : Decoder : Encoder]: Serde[A] = {
    val serializer = (a: A) => a.asJson.noSpaces.getBytes()
    val deserializer = (bytes: Array[Byte]) => {
      val string = new String(bytes)
      decode[A](string).toOption
    }
    Serdes.fromFn[A](serializer = serializer, deserializer = deserializer) // SERializer, DESerializer
  }

  def main(args: Array[String]): Unit = {

    // topology
    val builder = new StreamsBuilder()

    // KStream: describes a linear flow from a kafka topic
    // KStream[key, value]
    val usersOrdersStream: KStream[UserId, Order] = builder.stream[UserId, Order](OrdersByUser) // serdes are implicit so no need to add to build.stream

    usersOrdersStream.print(Printed.toSysOut[String, Order].withLabel("Consumed record"))
    // KTable: analogous to KStream except the elements that flow through the stream are static and correspond to a compacted topic
    // KTable: is distributed
    val userProfilesTable: KTable[UserId, Profile] = builder.table[UserId, Profile](DiscountProfilesByUser)

    // GlobalKTable: not partitioned but copied to all the nodes
    val discountProfilesGT: GlobalKTable[Profile, Discount] = builder.globalTable[Profile, Discount](Discounts)

    // KStream transformation:
    val expensiveOrders = usersOrdersStream.filter { (_, order) => order.amount > 1000 }
    val listsOfProducts = usersOrdersStream.mapValues { order => order.products }
    val productsStream = usersOrdersStream.flatMapValues(_.products)
    val ordersWithUserProfiles = usersOrdersStream.join(userProfilesTable) { (order, profile) =>
      (order, profile)
    }
    val discountsOrderStream = ordersWithUserProfiles.join(discountProfilesGT)(
      { case (userId, (order, profile)) => profile }, // key of the join
      { case ((order, profile), discount) => order.copy(amount = order.amount - discount.amount) } // values of the matched records
    )

    // pick another identifier to use as key
    val ordersStream = discountsOrderStream.selectKey((userId, order) => order.orderId) // orderId will be the key here for joins ...
    val paymentsStream = builder.stream[OrderId, Payment](Payments)

    val joinWindow = JoinWindows.of(Duration.of(5, ChronoUnit.MINUTES))

    val joinOrdersPayments = (order: Order, payment: Payment) => if (payment.status == "PAID") Option(order) else Option.empty[Order]

    // join on orderId since the selected key is orderId
    val ordersPaid = ordersStream.join(paymentsStream)(joinOrdersPayments, joinWindow)
      .flatMapValues(maybeOrder => maybeOrder.toIterable)

    // sink
    ordersPaid.to(PaidOrders)

    val topology = builder.build()
    println(topology.describe())

    val props = new Properties
    val bootstrapServers = sys.env.getOrElse("BOOTSTRAP_SERVERS", "localhost:9092")

    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "orders-application")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass)

    val application = new KafkaStreams(topology, props)
    application.start()
  }
}