import sbt._

object Dependencies {

  private val akkaVersion = "2.6.3"
  private val scalaTestVersion = "3.1.1"

  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion


  val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val scalaTest = "org.scalatest" %% "scalatest"  % scalaTestVersion
}