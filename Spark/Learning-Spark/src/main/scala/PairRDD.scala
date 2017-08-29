package com.yenarue.study

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}

object PairRDD {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")
    val sc = new SparkContext(conf)
    LogManager.getRootLogger.setLevel(Level.WARN)

    val descRDD = sc.textFile("kakao_bank_desc.txt")
    val words = descRDD.flatMap(x => x.split(" "))
    val result = words.filter(x => x.matches("[(가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9)]+"))
      .map(x => (x, 1))
      .reduceByKey((x, y) => x + y)
      .sortBy(x => x._2, ascending = false)
      .collect()
      .foreach(println)
    words.repartition(10)

    // cogroup
    val pairRDD1 = sc.parallelize(Array(("a", 1), ("b", 2)))
    val pairRDD2 = sc.parallelize(Array(("c", 1), ("b", 4)))
    pairRDD1.cogroup(pairRDD2).collect().foreach(println)

    // join
    println("==join==")
    val nameRDD = sc.parallelize(Array(("1111", "yena"), ("2222", "seungha"), ("3333", "pinga")))
    val scoreRDD = sc.parallelize(Array(("1111", 100), ("2222", 50), ("4444", 10)))
    println("==inner join==")
    nameRDD.join(scoreRDD).collect().foreach(println)
    println("==left outer join==")
    nameRDD.leftOuterJoin(scoreRDD).collect().foreach(println)
    println("==right outer join==")
    nameRDD.rightOuterJoin(scoreRDD).collect().foreach(println)

    // subtractByKey
    println("==subtractByKey==")
    nameRDD.subtractByKey(scoreRDD).collect().foreach(println)

    // actions
    val keyValueList = Array(("fruit", "apple"), ("fruit", "banana"), ("vegetable", "onion"))
    val pairRDD = sc.parallelize(keyValueList)

    println("==countByKey==")
    pairRDD.countByKey().foreach(println)
    println("==collectAsMap==")
    pairRDD.collectAsMap().foreach(println)
  }
}
