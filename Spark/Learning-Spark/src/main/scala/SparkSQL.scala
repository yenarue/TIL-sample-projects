package com.yenarue.study

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

// HiveContext is Deprecated. Use SparkSession.builder.enableHiveSupport instead. Since 2.0.0.
//import org.apache.spark.sql.hive.HiveContext

/* 확실히 오래된 책이라 디프리케이티드 된 것들이 많다....ㅠㅠ */
object SparkSQL {
  println("====Spark SQL====")
  //  override def main(args: Array[String]): Unit = {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")
    val sc = new SparkContext(conf)
    LogManager.getRootLogger.setLevel(Level.WARN)

    val sparkSession = SparkSession.builder.enableHiveSupport().getOrCreate()

    println("====Read json content====")
    val inputDataFrame = sparkSession.read.json("files/testtweet.json") // json 파일 내용에 enter line 들어가면 인식못함

    // Spark SQL 쿼리를 직접 날려보기
    // registerTempTable is deprecated. Use createOrReplaceTempView
//    inputDataSet.registerTempTable("tweets")
    inputDataFrame.createOrReplaceTempView("tweets")
    val topTweets = sparkSession.sql("SELECT text, retweetCount FROM tweets ORDER BY retweetCount LIMIT 10")
    topTweets.show()
    topTweets.printSchema()

    // 기본 데이터프레임 연산 사용해보기
    inputDataFrame.select("text", "retweetCount").show()

    println("====Getting first column====")
    val topTweetsRDD = topTweets.rdd
    val topTweetsText = topTweetsRDD.map(row => row.getString(0))
    topTweetsText.collect().foreach(println)

    println("====Multiline json contents====")
    val peopleDataFrame = sparkSession.read.json("files/people.json") // json 파일 내용에 enter line이 들어가면 다음 Row로 인식
    peopleDataFrame.select("*").show()
    peopleDataFrame.printSchema()

    println("====Convert DataFrame to RDD")
    val peopleRDD = peopleDataFrame.rdd

    println("====Convert RDD to DataFrame====")
    val peopleScheme = new StructType(Array(StructField("age", LongType, nullable = false),
                                            StructField("job", StringType, nullable = true),
                                            StructField("name", StringType, nullable = false)))
    val peopleDataFrame2 = sparkSession.createDataFrame(topTweetsRDD, peopleScheme)
    peopleDataFrame2.show()
  }
}
