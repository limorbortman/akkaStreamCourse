package part3Graphs

import akka.NotUsed
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}

object GraphBasics extends App {

  /**
   * How can we run a number of  different computations on the same input in parallel
   */
  val input = Source(1 to 1000)
  val incrementer = Flow[Int].map(_ + 1)
  val multiplier = Flow[Int].map(_ * 10)
  val output = Sink.foreach[(Int, Int)](println)

  /**
   * This is anther way to build a more complicated graph. RunnableGraph let us use the run().
   * Steps for creating a runnable graph:
   * 1. Create shape - contains all the components of the graph
   * 2. Create a static graph form the shape
   * 3. Create a runnable graph form the static graph
   */
  /**
   * About the shape creator:
   * 1. The builder is a MUTABLE data structure. This method should change the builder and add all the component.
   * But when we return the shape we Freeze the builder's shape and the builder becomes immutable
   */
  val shape = { implicit builder: GraphDSL.Builder[NotUsed] =>

    import GraphDSL.Implicits._

    /**
     * Broadcast - a fan-out operator takes 1 input and 2 outputs
     * Zip - a fan-in operator takes 2 input and 1 output
     */
    val broadcast = builder.add(Broadcast[Int](2))
    val zip = builder.add(Zip[Int, Int]())


    input ~> broadcast
    broadcast.out(0) ~> incrementer ~> zip.in0
    broadcast.out(1) ~> multiplier ~> zip.in1
    zip.out ~> output

    /**
     * Freeze the builder's shape and the builder becomes immutable
     */
    ClosedShape
  }

  val staticGraph = GraphDSL.create()(shape)
  val graph = RunnableGraph.fromGraph(staticGraph)
  graph.run()
}

