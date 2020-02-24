package playgrond

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}

object Playground extends App {

  implicit val actorSystem = ActorSystem("Playground")

  Source.single("Hello").to(Sink.foreach(println)).run()

}
