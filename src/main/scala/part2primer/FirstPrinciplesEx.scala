package part2primer

import akka.stream.scaladsl.Source
import infra.Implicits._

object FirstPrinciplesEx extends App {

  /**
   * Create a stream that takes names of persons. then you will keep the first 2 names with length > 5
   */
  val names = List("Bob", "Alice", "Charlie", "David", "Martin")

  Source(names)
    .filter(_.length > 5)  // val nameFlow = Flow[String].filter(_ > 5);  Source(names).via(nameFlow)
    .take(2)
    .runForeach(println)   // val nameSink = Sink.foreach[String](println)

}
