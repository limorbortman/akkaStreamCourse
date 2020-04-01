package part3Graphs.ex

import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink}

import scala.concurrent.Future

object GraphMaterializedValuesEx extends App {

  /**
   * implement enhanceFlow that take a flow and  return the output of the flow and the number of elements it processed
   */
  def enhanceFlow[A, B](flow: Flow[A, B, _]): Flow[A, B, Future[Int]] = {
    val counter = Sink.fold[Int, B](0)((count, _) => count + 1)

    Flow.fromGraph(
      GraphDSL.create(counter) { implicit builder =>
        counterShape =>
          import GraphDSL.Implicits._

          val broadcast = builder.add(Broadcast[B](2))
          val orgFlowShape = builder.add(flow)

          orgFlowShape ~> broadcast ~> counterShape
          FlowShape(orgFlowShape.in, broadcast.out(1))
      }
    )
  }
}