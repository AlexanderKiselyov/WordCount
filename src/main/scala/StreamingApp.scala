import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("TechnoPolis")
    val ssc = new StreamingContext(conf, Seconds(1))
    Logger.getRootLogger.setLevel(Level.ERROR)

    getWordCount(ssc).print()

    ssc.start()
    ssc.awaitTermination()
  }

  def getWordCount(ssc: StreamingContext): DStream[(String, Int)] = {
    //val lines = ssc.socketTextStream("localhost", 9999)
    val lines = ssc.textFileStream("logs")
    val wc = lines.flatMap(_.split("\\s+"))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
    wc
  }
}
