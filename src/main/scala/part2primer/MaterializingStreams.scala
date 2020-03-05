package part2primer

import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.Future
import scala.util.control.NonFatal

object MaterializingStreams extends App {

  val source = Source(1 to 10)
  val sink = Sink.foreach(println)

  val simpleGraph = source.to(sink)
  /**
   * Materializing the graph by running it . In this case the value will be NotUse = Unit
   */
  //val simpleMaterializedValue = simpleGraph.run()

  //val sumFuture: Future[Int] = source.runWith(Sink.reduce((a, b) => a + b))

  /**
   * We can return number of materialize values from every graph. By default the left value will be return.
   * But we can return different values by using the viaMat method.
   * The viaMat get the flow for creating the graph and function from 2 materialize values to 1 and this will be the returning materialize value.
   * We also have toMat
   */
  val simpleFlow = Flow[Int].map(_ + 1)
  val graph = source.
    viaMat(simpleFlow)(Keep.right) // Keep.right == ((sourceMat, flowMat) => flowMat)
    .toMat(sink)(Keep.right)
  /**
   * This result will be all the numbers +1 (the result of simple flow and the print "Steam processing finished" (the result of the string)
   */
  //graph.run()
  //.map(_ => println("Steam processing finished"))
  //.recover {
  // case NonFatal(ex) => println(s"Stream processing failed with: $ex")
  // }

  /**
   * backwards
   */
  //sink.runWith(source)

  /**
   * Connect the flow both to source and to sink
   */
  simpleFlow.runWith(source, sink)
}
