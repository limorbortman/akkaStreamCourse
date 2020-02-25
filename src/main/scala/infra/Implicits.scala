package infra

import akka.actor.ActorSystem

object Implicits {

  implicit val system = ActorSystem("ForCourse")

}
