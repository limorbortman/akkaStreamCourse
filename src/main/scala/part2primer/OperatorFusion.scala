package part2primer

import akka.stream.scaladsl.{Flow, Sink, Source}

object OperatorFusion extends App {

  val simpleSource = Source(1 to 100000)
  val flow1 = Flow[Int].map {
    Thread.sleep(1000)
    _ + 1
  }
  val flow2 = Flow[Int].map {
    Thread.sleep(1000)
    _ * 10
  }
  val simpleSink = Sink.foreach(println)

  /**
   * This run on the SAME ACTOR. This is called operator/component fusion.
   * This is good because it save the time of the async movement between different actors
   * BUT if the operations take more time it can be painful
   */
  //simpleSource.via(flow1).via(flow2).to(simpleSink)

  /**
   * The async method return a component that runs on a new actor -> Async boundary
   */
  //simpleSource.via(flow1).async
  //.via(flow2) // run on a new actor
  //.async
  //.to(simpleSink) // run on a new actor (#3)
  //.run()

  /**
   * Ordering guarantees
   */
  /**
   * In this case we don't use the async boundary so everything is processed on the same actor in this order
   */
  //  Source(1 to 3)
  //  .map { element =>
  //    println(s" Flow A : $element")
  //  element
  //}
  //.map { element =>
  // println(s" Flow B : $element")
  //element
  // }
  //.map { element =>
  // println(s" Flow C : $element")
  //element
  //}
  //.runWith(Sink.ignore)

  /**
   * Lets add async boundary => the code runs on different actors so no ordering BUT because the source is responsible of the ordering of the element
   * number 1 will be print before number 2 and number 2 will be print before number 3 => The relevant ordering of the elements is kept
   */
  Source(1 to 3)
    .map { element =>
      println(s" Flow A : $element")
      element
    }.async
    .map { element =>
      println(s" Flow B : $element")
      element
    }.async
    .map { element =>
      println(s" Flow C : $element")
      element
    }.async
    .runWith(Sink.ignore)
}
