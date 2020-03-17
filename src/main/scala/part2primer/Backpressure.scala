package part2primer

import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import scala.concurrent.duration._

object Backpressure extends App {

  val fastSource = Source(10 to 10000)
  val slowSink = Sink.foreach[Int] { element =>
    Thread.sleep(10000)
    println(s"Sink $element")
  }

  /**
   * This is a fusing so all the graph runs on the same actor  => No need for backpressure
   */
  // fastSource.to(slowSink).run()

  /**
   * This graph will run on 2 actors => backpressure is been used, but its transparent to the uer
   * In this case fastSource can slow down
   */
  //fastSource.async.to(slowSink).run()

  val simpleFlow = Flow[Int].map { element =>
    println(s"In Flow with element $element")
    element + 1
  }

  /**
   * In this case the slowSink will signal to the simpleFlow to use the backpressure mechanism
   * In this case simpleFlow can't control the rate it get elements
   * The simpleFlow will buffer number of elements (16 by default) before sending them to the slowSink letting the slowSink time to process
   */
  //fastSource.async
  //  .via(simpleFlow).async
  //  .to(slowSink)
  //  .run()

  /**
   * Reactions to backpressure (in order):
   * 1. Try to slow down if possible. That happened in fastSource.async.to(slowSink)
   * 2. Buffer elements
   * 3. Drop down elements from the buffer -> As a user we can control what will happen here
   * 4. Tear down/kill the whole stream (Failure)
   */

  /**
   * OverflowStrategy
   */

  /**
   * In this case the buffer will hold only 10 elements and will drop the oldest elements
   * 1-16 : no body is backpressuerd because the sink id buffering the first 16 elements
   * 17-26 : the flow will buffer , flow sill start dropping at the next element
   * 26-1000: flow will always drop the oldest element
   */
  val bufferFlow = simpleFlow.buffer(10, OverflowStrategy.dropHead)

  fastSource.async
    .via(bufferFlow).async
    .to(slowSink)
    .run()

  /**
   * Throttling - set the maximum rate for emitting messages
   * In this case, a source will provide at most 2 elements per 1 second
   */
  fastSource.throttle(2, 1.seconds)
}
