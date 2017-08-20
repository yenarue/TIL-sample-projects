package com.yenarue.study

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext, SparkFiles}

import scala.concurrent.Await

object Test{
  System.out.println("test")

  //  override def main(args: Array[String]): Unit = {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local[*]")
    val sc = new SparkContext(conf)
    LogManager.getRootLogger.setLevel(Level.WARN)

    val listRDD = sc.parallelize(List(1, 2, 3, 4))
    println(listRDD.reduce((a, b) => a + b))
    println(listRDD.reduce((a, b) => a - b))
    println(listRDD.aggregate(0, 0)((a, b) => (a._1 + b, a._2 + b), (a, b) => a))

    println("====fruit RDD====")
    val fruitList = Array("apple", "banana", "grape", "apricot", "berry")
    val fruitListRDD = sc.parallelize(fruitList)
    val groupByRDD = fruitListRDD.groupBy(str => str.charAt(0))
    groupByRDD.foreach(println)

    println("====pair RDD====")
    val keyValueList = Array(("fruit", "apple"), ("fruit", "banana"), ("vegetable", "onion"))
    val pairRDD = sc.parallelize(keyValueList)

    println("- groupByKey")
    val groupByPairRDD = pairRDD.groupByKey()
    groupByPairRDD.foreach(println)

    println("- filter")
    val filterPairRDD = pairRDD.filter{case (key, value) => value.length < 10}
    filterPairRDD.foreach(println)

    println("- countByKey")
    pairRDD.countByKey().foreach(println)


    println("====countByValue====")
    val duplicatedList = sc.parallelize(List("a", "b", "c", "a", "b", "a"))
    duplicatedList.countByValue().foreach(println)

    println("====shared variables - accumulator")
    val fileRDD = sc.textFile("./README.md")
    val blankLines = sc.doubleAccumulator
    val callSigns = fileRDD.flatMap(line => {
      if (line == "") {
        blankLines.add(1)
      }
      line.split(" ")
    })
//    callSigns.persist()

    callSigns.saveAsTextFile("output.txt")  // Action
    println("Blank lines : " + blankLines.value)

    val calSigns2 = callSigns.map(line => line)

    calSigns2.saveAsTextFile("output2.txt")
    println("Blank lines : " + blankLines.value)

    println("====shared variables - broadcast")
    val intVal = 1234
    val broadcastVal = sc.broadcast(intVal)
    println(broadcastVal.value)

    println("====pipe - for R")
    val testScript = "./src/main/R/test.R"
    val testScriptName = "test.R"
    sc.addFile(testScript)
    val strListRDD = sc.parallelize(List("aaa", "bbb", "ccc", "ddd"))
    val pipeRDD = strListRDD.pipe(Seq(SparkFiles.get(testScriptName)))
    pipeRDD.foreach(println)

    println("====Stats====")
    val statCounter = listRDD.stats()
    val stddev = statCounter.stdev
    val mean = statCounter.mean
    val reasonableDistances = listRDD.filter(x => math.abs(x - mean) < 3 * stddev)
    println(reasonableDistances.collect().toList)

    println("====Job, Task, Job Stage====")
    val inputRDD = sc.textFile("./README.md")
    val tokenizedRDD = inputRDD.filter(line => line.size > 0)
                              .map(line => line.split(" "))
    val countsRDD = tokenizedRDD.map(words => (words(0), 1))
                              .reduceByKey((a, b) => a + b)
    println(inputRDD.toDebugString)
    println(countsRDD.toDebugString)
    countsRDD.cache()
    countsRDD.collect()
//    Thread.sleep(100000)
  }
}
