import akka.actor.ActorSystem

import scala.concurrent.ExecutionContextExecutor

package object part3Graphs {

  implicit val system: ActorSystem = ActorSystem("ForCourse")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
}
