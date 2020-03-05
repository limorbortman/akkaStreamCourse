package playgrond

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}

object Playground extends App {

  implicit val system: ActorSystem = ActorSystem("ForCourse")
  Source.single("Hello").to(Sink.foreach(println)).run()

}
