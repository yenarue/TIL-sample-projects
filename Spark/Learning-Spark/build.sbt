name := "spark-test"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.2.0",
  "org.apache.spark" % "spark-hive_2.11" % "2.2.0"
//  "org.apache.spark" % "spark-sql_2.11" % "2.2.0"
)
