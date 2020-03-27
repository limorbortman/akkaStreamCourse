package part3Graphs

import akka.stream.scaladsl.{Broadcast, Concat, GraphDSL, Sink, Source}
import akka.stream.{SinkShape, SourceShape}

object OpenGraphs extends App {

  /**
   * A composite source that concatenates 2 sources
   */
  val source1 = Source(1 to 10)
  val source2 = Source(42 to 10000)

  /**
   * Creating a component from graph.
   * Here we create a complex source.
   */
  val sourceGraph = Source.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val concat = builder.add(Concat[Int](2))

      source1 ~> concat
      source2 ~> concat

      SourceShape(concat.out)
    }
  )

  //sourceGraph.to(Sink.foreach(print)).run()

  /**
   * Complex sink
   */
  val sink1 = Sink.foreach[Int](element => println(s"I am in sink1 $element"))
  val sink2 = Sink.foreach[Int](element => println(s"I am in sink2 $element"))
  val sinkGraph = Sink.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[Int](2))

      broadcast ~> sink1
      broadcast ~> sink2

      SinkShape(broadcast.in)
    }
  )

  source1.to(sinkGraph).run()

}
