import com.google.gson.Gson
import org.apache.log4j.{Level, LogManager}
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

case class Person(name: String, age: Int, job: String)

object DataSaveLoad {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkSQL").setMaster("local[*]")
    val sc = new SparkContext(conf)
    LogManager.getRootLogger.setLevel(Level.WARN)
    val sparkSession = SparkSession.builder.enableHiveSupport().getOrCreate()

    {
      // Load
      val textRDD = sc.textFile("files/README.md")
      val jsonRDD = sc.textFile("files/people.json")
      val personRDD = jsonRDD.map(line => new Gson().fromJson(line, classOf[Person]))
      val objectRDD = sc.objectFile[Person]("result-object")
      val seqRDD = sc.sequenceFile[String, Int]("result-seq")

      // Save
      textRDD.saveAsTextFile("result-text")
      personRDD.map(person => new Gson().toJson(person)).saveAsTextFile("result-json")
      objectRDD.saveAsObjectFile("result-object")
      seqRDD.saveAsSequenceFile("result-seq")
    }

    {
      val id = "yenarue"
      val accessKey = "pw"
      val master = "dd"
      val port = "222"

      // Local
      val localFile = sc.textFile("file:///home/yenarue/files/README.md")
      // S3
      sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", id)
      sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", accessKey)
      val awsFile = sc.textFile("s3n://myBucket/files/README.md")
      // HDFS
      val hdFile = sc.textFile("hdfs://" + master + ": " + port + "/files/README.md")
    }

    println("===Json===")
    val jsonRDD = sc.textFile("files/people.json")
    jsonRDD.map(line => {
      new Gson().fromJson(line, classOf[Person])
    }).foreach(x => println(x.name))

    val jsonRDD2 = sparkSession.read.json("files/people.json")
    val personRDD = jsonRDD2.toJSON.rdd.map(line => {
      new Gson().fromJson(line, classOf[Person])
    })

    personRDD.map(person => {
      new Gson().toJson(person)
    }).saveAsTextFile("result-json")

    println("===Object===")
    personRDD.saveAsObjectFile("result-object")
    val objectRDD = sc.objectFile[Person]("result-object")
    objectRDD.foreach(x => println(x.name))

    println("===Sequence===")
    val abcRDD = sc.parallelize(List("a", "b", "c")).map((_, 1))
    abcRDD.saveAsSequenceFile("result-seq")
    val seqRDD = sc.sequenceFile[String, Int]("result-seq")
    seqRDD.foreach(x => println(x._1))
  }
}
