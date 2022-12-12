import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkFunSuite}
import org.scalatest.concurrent.Eventually

import java.io.{File, PrintWriter}
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

class MyTestClass extends SparkFunSuite with Eventually {
  val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("TechnoPolis")
  val streamingContext = new StreamingContext(conf, Seconds(1))
  val expected: Set[(String, Int)] = Set(("kkk", 1), ("jjjj", 1), ("yyy", 2), ("ghi", 2), ("def", 2), ("abc", 5))

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    doThreadPreAudit()
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    doThreadPostAudit()
    if (streamingContext != null) {
      streamingContext.stop(true)
    }
  }

  test("check all words from file count") {
    val result = ListBuffer.empty[(String, Int)]

    StreamingApp.getWordCount(streamingContext).foreachRDD(rdd => {
      rdd.collect().foreach(pair => {
        result += pair
      })
    })

    streamingContext.start()

    writeTextInFile()

    eventually(timeout(2000.milliseconds), interval(100.milliseconds)) {
      assertResult(expected.size)(result.length)
      assertResult(expected)(result.toSet)
    }
  }

  def writeTextInFile(): Unit = {
    val inputFile1 = new PrintWriter(new File("logs/1.txt"))
    inputFile1.write("abc abc def abc\n")
    inputFile1.write("ghi abc ghi def\n")
    inputFile1.write("yyy jjjj kkk abc yyy\n")
    inputFile1.close()
  }
}
