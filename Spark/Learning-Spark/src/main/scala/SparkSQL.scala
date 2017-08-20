package com.yenarue.study

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

// HiveContext is Deprecated. Use SparkSession.builder.enableHiveSupport instead. Since 2.0.0.
//import org.apache.spark.sql.hive.HiveContext

/* 확실히 오래된 책이라 디프리케이티드 된 것들이 많다....ㅠㅠ */
object SparkSQL {
  //  override def main(args: Array[String]): Unit = {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")
    val sc = new SparkContext(conf)
    LogManager.getRootLogger.setLevel(Level.WARN)

    println("====Spark SQL====")
    val sparkSession = SparkSession.builder.enableHiveSupport().getOrCreate()
    val inputDataSet = sparkSession.read.json("files/testtweet.json") // json 파일 내용에 enter line 들어가면 인식못함

    // registerTempTable is deprecated. Use createOrReplaceTempView
//    inputDataSet.registerTempTable("tweets")
    inputDataSet.createOrReplaceTempView("tweets")
    val topTweets = sparkSession.sql("SELECT text, retweetCount FROM tweets ORDER BY retweetCount LIMIT 10")
    topTweets.show()
    topTweets.printSchema()

    inputDataSet.select("text").show()

    //    Thread.sleep(100000)
  }
}