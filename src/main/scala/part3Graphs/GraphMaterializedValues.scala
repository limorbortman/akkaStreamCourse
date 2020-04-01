package part3Graphs

import akka.stream.{ClosedShape, SinkShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Sink, Source}

import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object GraphMaterializedValues extends App {

  val wordSource = Source(List("Bla", "Akka", "streams"))
  val printer = Sink.foreach[String](println)
  val counter = Sink.fold[Int, String](0)((count, _) => count + 1)

  /**
   * Create a composite component (sink):
   *  - prints out all the strings witch are in lowercase
   *  - count the strings that are short ( < 5 chars)
   */
  val complexWordSink = Sink.fromGraph(
    /**
     * In order to change the materialized value of a graph we need to give the GraphDSL.create the component we want to materialize.
     * This will change the sink from Sink[Sting, NotUsed] to Sink[String, Int] and this is happen in 3 steps:
     * 1. Pass the component with the value you want to the GraphDSL.create method
     * 2. Change the second parameter list of  GraphDSL.create  to be he order function from builder to shape=>shep
     * 3. Use the shape of the component you need
     */
    GraphDSL.create(counter) { implicit builder =>
      counterShape =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[String](2))
        val lowercaseFilter = builder.add(Flow[String].filter(word => word == word.toLowerCase()))
        val shortStringFilter = builder.add(Flow[String].filter(_.length < 5))

        broadcast ~> lowercaseFilter ~> printer
        broadcast ~> shortStringFilter ~> counterShape

        SinkShape(broadcast.in)
    }
  )

  // val futureCount = wordSource.toMat(complexWordSink)(Keep.right).run()
  //  .map(count => println(s"Count is $count"))
  // .recover {
  //  case NonFatal(ex) => println(s"Stream processing failed with: $ex")
  //}

  /**
   * Compos multiply component materializes will be done by passing the GraphDSL.create  multiply component
   * but then you need to add a method to combine all the values together
   */
  val multiplyMaterializeValuesSink = Sink.fromGraph(
    GraphDSL.create(printer, counter)((printerMatValues, counterMatValues) => counterMatValues) { implicit builder =>
      (printerShape, counterShape) =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[String](2))
        val lowercaseFilter = builder.add(Flow[String].filter(word => word == word.toLowerCase()))
        val shortStringFilter = builder.add(Flow[String].filter(_.length < 5))

        broadcast ~> lowercaseFilter ~> printerShape
        broadcast ~> shortStringFilter ~> counterShape

        SinkShape(broadcast.in)
    }
  )


}
