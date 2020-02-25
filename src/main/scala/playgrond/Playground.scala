package playgrond

import akka.stream.scaladsl.{Sink, Source}

import infra.Implicits._

object Playground extends App {

  Source.single("Hello").to(Sink.foreach(println)).run()

}
