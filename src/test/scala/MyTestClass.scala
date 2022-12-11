import org.apache.spark.streaming.{LocalStreamingContext, Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkFunSuite}
import org.scalatest.GivenWhenThen
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers

import java.io.{File, PrintWriter}
import scala.collection.mutable.ListBuffer

class MyTestClass extends SparkFunSuite with LocalStreamingContext with GivenWhenThen with Matchers with Eventually {
  val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("TechnoPolis")
  val streamingContext = new StreamingContext(conf, Seconds(1))

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    doThreadPreAudit()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
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

    Thread.sleep(3000)

    val expected = Set(("kkk", 1), ("jjjj", 1), ("yyy", 2), ("ghi", 2), ("def", 2), ("abc", 5))
    assertResult(expected.size)(result.length)
    assertResult(expected)(result.toSet)
  }

  def writeTextInFile(): Unit = {
    val inputFile1 = new PrintWriter(new File("logs/1.txt"))
    inputFile1.write("abc abc def abc\n")
    inputFile1.write("ghi abc ghi def\n")
    inputFile1.write("yyy jjjj kkk abc yyy\n")
    inputFile1.close()
  }
}
