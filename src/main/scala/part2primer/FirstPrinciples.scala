package part2primer

import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

object FirstPrinciples extends App {

  /**
   * Source def (import scaladsl...)
   * Sources can ammit any kind of element as long its immutable and serializable (Just like akka messages), Except for null.
   * If we will try to run graph with null source we will get NPE
   */
  val source = Source(1 to 10)

  /**
   * Various kinds of sources
   */
  val oneElementSource = Source.single(1)
  val finiteCollectionSource = Source(List(1, 2, 3, 4))
  val emptySource = Source.empty
  val infiniteCollectionSource = Source(LazyList.from(1))
  val futureSource = Source.future(Future.successful(42))

  /**
   * Sink def
   **/
  val sink = Sink.foreach[Int](println)

  /**
   * Various kinds of sinks
   */
  val doNothing = Sink.ignore
  val foreachSink = Sink.foreach(println)
  val returnValueSink = Sink.head // Return the head anc CLOSE the stream
  val computeValueSink = Sink.fold[String, Int]("0")((a, b) => a + b)
  /**
   * Connect source to sink -> creating a graph. When creating a graph nothing is running!!!!  For the graph to run we need to use the run method
   */
  val graph = source.to(sink)
  //graph.run()


  /**
   * Flows : transform elements
   */
  val mapFlow = Flow[Int].map(_ + 1)

  /**
   * Attache flow to source. This action will create new source
   */
  val sourceWithFlow = source.via(mapFlow)
  //sourceWithFlow.to(sink).run()
  /**
   * Attache flow to sink. This action will create new sink
   */
  val sinkWithFlow = mapFlow.to(sink)
  //source.to(sinkWithFlow).run()

  /**
   * Various kinds of flows usually mapped to collection operators
   */
  val map2Flow = Flow[Int].map(2 * _)
  val takeFlow = Flow[Int].take(5) // take only the first n elements and transform the stream to finite stream
  //we have drop, filter and more BUT we DON'T have flatMap (we can create sub stream)

  /**
   * Example
   */
  source.via(map2Flow).via(takeFlow).to(sink).run()
}
