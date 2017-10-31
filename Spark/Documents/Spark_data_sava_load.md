# 데이터 불러오기/저장하기
스파크는 Hadoop API를 기반으로 다양한 데이터 포맷과 파일시스템, 데이터베이스 시스템과의 연동을 지원한다.

* 데이터 포맷 : 텍스트, JSON, ObjectFile, SequenceFile, csv 등...
* 파일시스템 : 로컬FS, HDFS, AWS의 S3, 오픈스택의 Swift 등...
* 데이터베이스 : MySQL, HBase, 일래스틱서치, JDBC, Cassandra, Hive, MongoDB 등...

## 데이터 포맷
| 데이터 포맷 | 구조화 여부 | 비고 |
|-----------|----------|----------------------------------------------------------------------------------|
|텍스트 파일  |  X      | 일반 텍스트 파일. 한 라인을 한 레코드로 간주한다.|
|JSON       |일부 구조화| 텍스트 기반 포맷. 반 구조화. 한 라인을 하나의 레코드로 간주한다.|
|CSV        |  O     | 텍스트 기반 포맷. 스프레드시트 앱에도 쓰임|
|시퀀스 파일  | O       | 키/값 데이터를 위한 일반적인 하둡데이터 포맷|
|프로토콜 버퍼 | O      | 빠르고 효율적으로 공간을 사용함. 다중 언어를 지원하는 포맷|
|오브젝트 파일 | O      | 공유된 코드끼리 스파크 작업의 데이터를 저장하는데에 유용. 자바의 객체 직렬화에 의존(클래스 변경하면 깨질 수 있음)|

### 텍스트 파일
#### 불러오기
각 라인이 RDD의 개별 데이터로 들어간다.
```scala
scala> val inputRDD = sc.textFile("file://home/yenarue/Markdown/Spark/Spark_PairRDD.md")
inputRDD: org.apache.spark.rdd.RDD[String] = file://home/yenarue/Markdown/Spark/Spark_PairRDD.md MapPartitionsRDD[3] at textFile at <console>:24
```
디렉터리 형태로 동시에 여러개의 텍스트 파일을 읽어 Pair RDD로 만들 수도 있다.
```scala
scala> val inputRDD = sc.wholeTextFiles("file:///home/yenarue/Markdown/Spark")
inputRDD: org.apache.spark.rdd.RDD[(String, String)] = file:///home/yenarue/Markdown/Spark MapPartitionsRDD[5] at wholeTextFiles at <console>:24
```
#### 저장하기
RDD의 데이터를 출력하여 텍스트 파일로 저장할수 있다. 경로를 지정해주면 디렉토리로 지정되며 그 하위에 텍스트파일이 분할되어 저장된다.
```scala
resultRDD.saveAsTextFile("file://home/yenarue/result")
```
```bash
yenarue@yenarue-ubuntu:/result$ ls
part-00000  part-00001  _SUCCESS
```
위와 같이 결과를 여러개의 파일에 저장함으로서 다음과 같은 이점이 있다.
* 병렬처리를 이용한 성능 향상
* 하나 저장하다가 오류가 발생해도 전체 작업에 미치는 영향이 최소화 됨

아래와 같이 압축파일로 저장도 가능하다.
```scala
resultRDD.saveAsTextFile("file://home/yenarue/result", classOf[GZipCodec])
```
```bash
yenarue@yenarue-ubuntu:/result$ ls
part-00000.gz  part-00001.gz  _SUCCESS
```
#### 주의사항
생성된 파일이 이미 존재할 경우에는 덮어씌워지지 않고 에러를 뿜어낸다;;;
```console
Exception in thread "main" org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory file:///home/yenarue/result already exists
	at org.apache.hadoop.mapred.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:132)
    ....
```
저장하기전에 기존에 생성된 디렉터리 및 파일을 **모두 다 삭제하고 진행**하도록 하자.
(사실 덮어쓰기 기능이 아예 지원되지 않는 건 아님. RDD에서 바로 세이브하지 않고, DataSet, DataFrame을 이용해서 하면 가능하다.)

### JSON
#### 불러오기
JSON은 사실 평범한 텍스트파일처럼 읽힌다. 그래서 오브젝트랑 매핑하려면 매핑라이브러리를 사용해서 아래와 같이 매핑해줘야 한다.
```scala
case class Person(name: String, age: Int, job: String)

val jsonRDD = sc.textFile("files/people.json")

jsonRDD.map(line => {
    new Gson().fromJson(line, classOf[Person])
}).foreach(x => println(x.name))
```
그렇다보니 읽어야할 Json 파일도 **한 줄에 하나의 레코드**를 가지고 있어야한다. 뉴라인을 만나면 다음 레코드로 인식한다.
```json
{"name":"Yenarue", "age":27, "job":"software engineer"}
{"name":"SonSeungHa", "age":30}
{"name":"Pinga", "age":4}
```

굉장히 많이쓰는 데이터 포맷인데 이따구라니 좀 호환이 구린 것 같아 보이지만, 
Spark SQL을 사용할때는 DataFrame으로 손쉽게 변환하는 API를 제공한다!
```scala
val sparkSession = SparkSession.builder.enableHiveSupport().getOrCreate()
val peopleDataFrame = sparkSession.read.json("files/people.json") // json 파일 내용에 enter line이 들어가면 다음 Row로 인식
```

#### 저장하기
저장하기도 마찬가지로 JSON 매핑 라이브러리를 이용해서 단순 텍스트로 저장한다.
```scala
personRDD.map(person => {
   new Gson().toJson(person)
}).saveAsTextFile("result-json")
```

### Object File
자바의 Serializable 인터페이스로 직렬화 처리가 되어있는 클래스의 인스턴스를 저장하고 불러올 수 있다.
#### 불러오기
```scala
case class Person(name: String, age: Int, job: String)

val personRDD = sc.objectFile[Person]("result-object")
```
#### 저장하기
데이터를 바이너리 형태로 저장한다.
```scala
personRDD.saveAsObjectFile("result-object")
```
```bash
yenarue@yenarue-ubuntu:/home/yenarue/result-object$ ls
part-00000  _SUCCESS
yenarue@yenarue-ubuntu:/home/yenarue/result-object$ file part-00000 
part-00000: data
```
#### 주의사항
다만, 직렬화 처리에 의존하다보니 클래스가 변경되면 이전에 만들어진 오브젝트 파일은 더이상 읽을 수 없게 된다.
이러한 점에 속도가 느리다는 단점이 더해져 자주 사용되지 않는 포맷이라고 한다.

### SequenceFile
하둡에서 자주 사용되는 대표적인 파일 포맷으로서, 키와 값으로 구성된 데이터를 저장하는 바이너리 파일 포맷이다.
오브젝트 파일처럼 바이너리형태로 데이터를 저장하지만 시퀀스 파일은 하둡에서 자제적으로 정의한 직렬화 프레임워크를 사용한다.
즉, 하둡의 Writable 인터페이스를 구현하고 있는 클래스의 인스턴스를 저장하고 불러올 수 있다.
#### 불러오기
```scala
val seqRDD = sc.sequenceFile[String, Int]("result-seq")
```
#### 저장하기
```scala
val abcRDD = sc.parallelize(List("a", "b", "c")).map((_, 1))
abcRDD.saveAsSequenceFile("result-seq")
```
### csv

## 파일시스템
### 로컬FS
### HDFS
### S3
### Swift

## 데이터베이스
### MySQL
### HBase
### Cassandra
### Hive
### MongoDB
MongoDB에서 제공하는 [Spark용 커넥터](https://www.mongodb.com/products/spark-connector)가 존재한다.
![MongoDB_Connector_for_Spark](https://www.mongodb.com/assets/images/products/spark-connector/spark-connector.png)

## 참고자료
- MongoDB Connector for Spark : https://docs.mongodb.com/spark-connector/master/
- spark로 mongoDB data넣기 : http://egloos.zum.com/always19/v/3058546