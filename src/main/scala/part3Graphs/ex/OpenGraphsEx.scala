package part3Graphs.ex

import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Source, Sink}

object OpenGraphsEx extends App {

  /**
   * Ex 1: Create a complex flow that's composed from 2 other flows:
   * ~ one that add 1 to a number
   * ~ one that does number * 10
   */

  val flow1 = Flow[Int].map(_ + 1)
  val flow2 = Flow[Int].map(_ * 10)

  val flowGraph = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      /**
       * flow1 ~> flow2 will not work becose flow1 and flow2 are NOT a shape! so we need to copy them into a shape
       */
      val flow1Shape = builder.add(flow1)
      val flow2Shape = builder.add(flow2)

      flow1Shape ~> flow2Shape

      FlowShape(flow1Shape.in, flow2Shape.out)
    }
  )

  /**
   * Ex 2: Create a flow from a sink and a source
   * this is :  Flow.fromSinkAndSource
   */
  val source = Source(1 to 100)
  val sink = Sink.fold[Seq[Int], Int](Seq())((seq , elemnt) => {
    seq.appended(elemnt+1)
  })

  val flowGraphFromSourceAndSink = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val sourceShape = builder.add(source)
      val sinkShape = builder.add(sink)

      FlowShape(sinkShape.in, sourceShape.out)
    }
  )


}
