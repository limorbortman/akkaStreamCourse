package part3Graphs

import akka.NotUsed
import akka.stream.scaladsl.{Broadcast, Concat, GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import akka.stream.{ClosedShape, SinkShape, SourceShape, UniformFanInShape}

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

  //source1.to(sinkGraph).run()

  /**
   * Uniform Shape-> same types for all inputs and outputs
   * Max 3 Operator:
   *  - 3 inputs of type int
   *  - push out the max of the 3
   */
  val max3StaticGraph = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val max1 = builder.add(ZipWith[Int, Int, Int]((a, b) => Math.max(a, b)))
    val max2 = builder.add(ZipWith[Int, Int, Int]((a, b) => Math.max(a, b)))

    max1.out ~> max2.in0

    UniformFanInShape(max2.out, max1.in0, max1.in1, max2.in1)
  }

  val source1To10 = Source(1 to 10)
  val source5 = Source(1 to 10).map(_ => 5)
  val sourceRevers = Source((1 to 10).reverse)

  val maxSink = Sink.foreach[Int](max => println(s"Max is $max"))

  val max3RunnableGraph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val max3Shape = builder.add(max3StaticGraph)

      source1To10 ~> max3Shape.in(0)
      source5 ~> max3Shape.in(1)
      sourceRevers ~> max3Shape.in(2)
      max3Shape ~> maxSink

      ClosedShape
    }
  ).run()

  /**
   * Non uniform shapes -> return/get different types -TBD
   */



}
