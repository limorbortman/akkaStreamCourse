package part3Graphs

import akka.stream.{BidiShape, ClosedShape}
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}

object BidirectionalFlows extends App {

  /**
   * Bi directional graph is the result of connecting 2 graphs that go in to the opposite direction
   */
  def encrypt(n: Int)(string: String) = string.map(c => (c + n).toChar)

  def decrypt(n: Int)(string: String) = string.map(c => (c - n).toChar)

  val bidiCryptoStaticGraph = GraphDSL.create() { implicit builder =>

    val encryptFlowShape = builder.add(Flow[String].map(encrypt(3)))
    val decryptFlowShape = builder.add(Flow[String].map(decrypt(3)))

    // BidiShape(encryptFlowShape.in, encryptFlowShape.out, decryptFlowShape.in, decryptFlowShape.out)
    BidiShape.fromFlows(encryptFlowShape, decryptFlowShape)
  }

  val unencryptedStrings = List("Akka", "Scala", "awseome")
  val unencryptedSource = Source(unencryptedStrings)
  val encryptedSource = Source(unencryptedStrings.map(encrypt(3)))

  val bidiCryptoGraph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val unencryptedSourceShape = builder.add(unencryptedSource)
      val encryptedSourceShape = builder.add(encryptedSource)
      val bidiShape = builder.add(bidiCryptoStaticGraph)
      val encryptedSinkShape = builder.add(Sink.foreach[String](string => println(s"Encrypted : $string")))
      val decryptedSinkShape = builder.add(Sink.foreach[String](string => println(s"Decrypted : $string")))

      unencryptedSourceShape ~> bidiShape.in1
      bidiShape.out1 ~> encryptedSinkShape
      bidiShape.in2 <~ encryptedSourceShape
      decryptedSinkShape <~ bidiShape.out2

      ClosedShape
    }
  ).run()
}
