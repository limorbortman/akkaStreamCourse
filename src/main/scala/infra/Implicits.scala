package infra

import akka.actor.ActorSystem

package object Implicits {

  implicit val system = ActorSystem("ForCourse")
  implicit val ex = system.dispatcher

}
