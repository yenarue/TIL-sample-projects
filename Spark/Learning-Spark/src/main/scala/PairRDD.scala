package com.yenarue.study

import org.apache.hadoop.io.compress.GzipCodec
import org.apache.log4j.{Level, LogManager}
import org.apache.spark._

object PairRDD {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")
    val sc = new SparkContext(conf)
    LogManager.getRootLogger.setLevel(Level.WARN)

    // gen
    val map = Map("fruit" -> "apple", "fruit" -> "banana", "vegetable" -> "onion")
    val mapRDD = sc.parallelize(map.toSeq)
    mapRDD.collect()foreach(println)

    // kakao
    val descRDD = sc.textFile("files/kakao_bank_desc.txt")
    val words = descRDD.flatMap(x => x.split(" "))
    val resultRDD = words.filter(x => x.matches("[(가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9)]+"))
      .map(x => (x, 1))
      .reduceByKey((x, y) => x + y)
      .sortBy(x => x._2, ascending = false)
//    resultRDD.saveAsTextFile("result")
    resultRDD.collect().foreach(println)
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

    // partitionBy
    {
      println("==without PartitionBy==")
      val userData = sc.parallelize(Array((1, "yeanrue"), (2, "tiffany"), (3, "steve"), (10, "json"), (11, "one"), (12, "bolim"), (13, "sunghye")))
          .repartition(5)
        .persist()
      val events = sc.parallelize(Array((1, "enter"), (2, "enter"), (1, "leave"), (3, "enter")))
      println("userData's partitions")
      userData.partitions.foreach(println)
      println("events's partitions")
      events.partitions.foreach(println)
      val joinedRDD = userData.join(events)
      println("joined's partitions")
      joinedRDD.partitions.foreach(println)
      joinedRDD.collect().foreach(println)
    }

    {
      println("==with PartitionBy==")
      val userData = sc.parallelize(Array((1, "yeanrue"), (2, "tiffany"), (3, "steve"), (10, "json"), (11, "one"), (12, "bolim"), (13, "sunghye")))
        .partitionBy(new HashPartitioner(5))
        .persist()
      val events = sc.parallelize(Array((1, "enter"), (2, "enter"), (1, "leave"), (3, "enter")))
      val rangePartitioner = new RangePartitioner(5, userData)
      val rangedEvents = userData.partitionBy(rangePartitioner)
//        .partitionBy(new HashPartitioner(7))
      println("userData's partitions")
      userData.partitions.foreach(println)
      println("numPartition=" + userData.getNumPartitions)
      println("structure=")
      userData.glom().collect().foreach(x => {
        print("[")
        x.foreach(print)
        println("]")
      })

      println("events's partitions")
      println("numPartition=" + rangedEvents.getNumPartitions)
      println("structure=")
      rangedEvents.glom().collect().foreach(x => {
        print("[")
        x.foreach(print)
        println("]")
      })

      val joinedRDD = userData.join(rangedEvents)
      println("joined's partitions")
      joinedRDD.partitions.foreach(println)
      joinedRDD.collect().foreach(println)
    }

    {
      println("==Partition종류==")
      val userData = sc.parallelize(Array((1, "yeanrue"), (2, "tiffany"), (3, "steve"), (10, "json"), (11, "one"), (12, "bolim"), (13, "sunghye")))

      val hashedUserData = userData.partitionBy(new HashPartitioner(5)).persist()

      println("==hash partitions==")
      hashedUserData.partitions.foreach(println)
      println("- numPartition=" + hashedUserData.getNumPartitions)
      println("- structure=")
      hashedUserData.glom().collect().foreach(x => {
        print("[")
        x.foreach(print)
        println("]")
      })

      val rangePartitioner = new RangePartitioner(2, userData)
      val rangedUserData = userData.partitionBy(rangePartitioner).persist()

      println("==range partitions==")
      println("- numPartition=" + rangedUserData.getNumPartitions)
      println("- structure=")
      rangedUserData.glom().collect().foreach(x => {
        print("[")
        x.foreach(print)
        println("]")
      })

//      val joinedRDD = userData.join(rangedEvents)
//      println("joined's partitions")
//      joinedRDD.partitions.foreach(println)
//      joinedRDD.collect().foreach(println)
    }

    {
      println("===Custom Partitioner===")
      class CustomPartitioner(numParts: Int) extends Partitioner {
        override def numPartitions: Int = numParts

        override def getPartition(key: Any): Int = {
          val code = key.hashCode() % numPartitions
          if (code < 0) {
            code + numPartitions
          } else {
            code
          }
        }

        override def equals(other: scala.Any): Boolean = other match {
          case custom: CustomPartitioner => custom.numPartitions == numPartitions
          case _ => false
        }
      }
    }
  }
}
