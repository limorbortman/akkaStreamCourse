package part3Graphs.ex

import akka.NotUsed
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Balance, Broadcast, GraphDSL, Merge, RunnableGraph, Sink, Source}
import part3Graphs._
import scala.concurrent.duration._

object GraphBasicsEx extends App {

  /**
   * Ex 1: feed a source into 2 sinks at the same time
   */
  val shapeEx1 = { implicit builder: GraphDSL.Builder[NotUsed] =>

    import GraphDSL.Implicits._

    val broadcast = builder.add(Broadcast[Int](2))

    Source(1 to 1000) ~> broadcast ~> Sink.foreach[Int](number => println(s"In sink1 with $number"))
    broadcast ~> Sink.foreach[Int](number => println(s"In sink2 with $number")) //implicit port numbering

    ClosedShape
  }

  //RunnableGraph.fromGraph(GraphDSL.create()(shapeEx1)).run()

  /**
   * Ex 2: 2 source (fast and slow) ~> merge ~> balance ~> 2 sinks
   */
  val fastSource = Source(10 to 10000)
  val slowSource = fastSource.throttle(2, 1.seconds)

  val sink1 = Sink.fold(0) { (counter, _: Int) =>
    println(s"In sink1 counter is $counter")
    counter + 1
  }

  val sink12 = Sink.fold(0) { (counter, _: Int) =>
    println(s"In sink2 counter is $counter")
    counter + 1
  }

  val shapeEx2 = { implicit builder: GraphDSL.Builder[NotUsed] =>

    import GraphDSL.Implicits._

    val merge = builder.add(Merge[Int](2))
    val balance = builder.add(Balance[Int](2))

    fastSource ~> merge
    slowSource ~> merge
    merge ~> balance ~> sink1
    balance ~> sink12

    ClosedShape
  }

  RunnableGraph.fromGraph(GraphDSL.create()(shapeEx2)).run()


}
