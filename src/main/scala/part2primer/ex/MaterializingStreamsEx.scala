package part2primer.ex

import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import part2primer._

object MaterializingStreamsEx extends App {

  /**
   * 1. Return the last element out of a source
   */
  Source(1 to 10)
    .runWith(Sink.last)
    .foreach(println)

  /**
   * 2.Compute the total word count out of a stream of sentences
   */
  val sentenceSource = Source(List("I am the one.", "No I am the one", "No, it is I."))
  //Op1:
  sentenceSource
  .map(countWords)
  .reduce((a, b) => a + b)
  .runForeach(println)

  //Op2:
  sentenceSource
    .toMat(Sink.fold(0)((currentWordsCont, newSentence) => currentWordsCont + countWords(newSentence)))(Keep.right) //This solution == to sentenceSource.runWith(Sink.fold(0)((currentWordsCont, newSentence) => currentWordsCont + countWords(newSentence))))
    .run()
    .foreach(println)

  //Op3:
  sentenceSource
    .via(Flow[String].fold(0)((currentWordsCont, newSentence) => currentWordsCont + countWords(newSentence)))
    .runWith(Sink.head)
    .foreach(println)


  def countWords(string: String): Int = string.split(" ").length

}
