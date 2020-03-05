import akka.actor.ActorSystem

import scala.concurrent.ExecutionContextExecutor

package object part2primer {

  implicit val system: ActorSystem = ActorSystem("ForCourse")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

}
