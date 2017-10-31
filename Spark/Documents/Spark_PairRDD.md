# Pair RDD 연산
## Pair RDD 란?
RDD의 요소가 키와 값의 쌍을 이루는 경우, 이를 **Pair RDD**라고 부른다.
데이터 처리 시, 키/값 형태로 된 데이터를 처리하게 되는 순간들을 많이 만나게 될 것이다. (집합연산, ETL(Extract, Transform and Load) 등)
스파크는 이러한 경우를 위한 연산들을 제공하고 있고, 그것을 **Pair RDD연산** 이라고부른다.
이러한 연산들을 사용하면 아래와 같은 장점들이 생긴다 :-)
- 각 키별로 병렬처리가 될 수 있다. => 빠름!
- 키와 값을 다루기 위한 Boilerplate 코드들을 작성하지 않아도 된다. => 깔끔! 직관적!

## Pair RDD 만들어보기
### Tuple 배열을 parallelize해서 만들기
```scala
val keyValueList = Array(("fruit", "apple"), ("fruit", "banana"), ("vegetable", "onion"))
val pairRDD = sc.parallelize(keyValueList)
pairRDD.collect().foreach(println)
// (fruit, apple)
// (fruit, banana)
// (vegetable, onion)
```

### map을 parallelize해서 만들기
```scala
val map = Map("fruit" -> "apple", "fruit" -> "banana", "vegetable" -> "onion")
val mapRDD = sc.parallelize(map.toSeq)
mapRDD.collect().foreach(println)
// (vegetable,onion)
// (fruit,banana)

```

### map 연산을 이용하기
```scala
val stringList = Array("fruit-apple", "fruit-banana", "vegetable-onion")
val stringsRDD = sc.parallelize(stringList)
val pairRDD = stringsRDD.map(string => (string.split("-")(0), string))
pairRDD.collect().foreach(println)
// (fruit,fruit-apple)
// (fruit,fruit-banana)
// (vegetable,vegetable-onion)
```

## Pair RDD 연산 알아보기
Pair RDD에서도 일반 RDD에서 사용할 수 있는 모든 연산들을 사용할 수 있다. 여기서는 **Pair RDD에서만 사용할 수 있는 연산**들을 알아보자!
### 액션
##### countByKey()
각 키에 대한 값의 개수를 센다.
```scala
val keyValueList = Array(("fruit", "apple"), ("fruit", "banana"), ("vegetable", "onion"))
val pairRDD = sc.parallelize(keyValueList)
pairRDD.countByKey().foreach(println)
// (vegetable,1)
// (fruit,2)
```
##### collectAsMap()
`collect()`대신 사용할 수 있는 액션으로, Array를 반환하는 `collect()`와는 달리, 결과를 Map 형태로 반환한다.
Map형식으로 반환하다보니, 중복되는 키가 존재하면 가장 마지막에 들어온 밸류로 덮어씌워져 버리니 주의하자!
```scala
scala> pairRDD.collect()
res55: Array[(String, String)] = Array((fruit,apple), (fruit,banana), (vegetable,onion))

scala> pairRDD.collectAsMap()
res54: scala.collection.Map[String,String] = Map(fruit -> banana, vegetable -> onion)
```

##### lookup(key)
매개변수로 받아온 키에 매칭되는 밸류값들을 Array형식으로 반환한다.
```scala
scala> pairRDD.lookup("fruit")
res56: Seq[String] = WrappedArray(apple, banana)

```


### 트랜스포메이션
#### 1개의 Pair RDD에 대한 트랜스포메이션
##### keys
Pair RDD 의 키들을 가진 RDD를 반환한다
```scala
scala> pairRDD.keys.collect()
res45: Array[String] = Array(fruit, fruit, vegetable, fruit)

```
##### values
Pair RDD 의 값들을 가진 RDD를 반환한다
```scala
scala> pairRDD.values.collect()
res43: Array[String] = Array(apple, banana, onion, orange)
```
##### sortByKey()
Pair RDD의 키로 정렬한다. ascending 파라미터로 정렬방식을 지정할 수 있다.
```scala
scala> pairRDD.sortByKey().collect()
res47: Array[(String, String)] = Array((fruit,apple), (fruit,banana), (fruit,orange), (vegetable,onion))

scala> pairRDD.sortByKey(false).collect()
res48: Array[(String, String)] = Array((vegetable,onion), (fruit,apple), (fruit,banana), (fruit,orange))
```

##### reduceByKey(func), foldByKey(defaultValue, func)
동일 키에 대한 값들을 reduce한다.
```scala
val keyValueList = Array(("fruit", "apple"), 
                         ("fruit", "banana"), 
                         ("vegetable", "onion"))
val reduceRDD = pairRDD.reduceByKey((x,y) => x + " & " + y)
reduceRDD.foreach(println)
// (fruit,apple & banana & orange)
// (vegetable,onion)
```

```scala
val keyValueList = Array(("fruit", "apple"), 
                         ("fruit", "banana"), 
                         ("vegetable", "onion"))
val foldRDD = pairRDD.foldByKey("default")((x,y) => x + " & " + y)
foldRDD.foreach(println)
// (vegetable,default & onion)
// (fruit,default & apple & default & banana & default & orange)

```

##### combineByKey(), aggregateByKey()
맵리듀스의 컴바이너 개념에 익숙한 사람들은 컴바이너 지정을 줘야하지 않을까 싶을 수 있다. 하지만 스파크에서는 자동으로 병합을 해준다!
만약 지정해주고싶다면 `comineByKey()`를 이용하여 병합 로직을 직접 작성하면 된다.


##### groupByKey()
동일한 key를 가진 아이템들의 value를 Iterable 형태로 그룹화 한다.
```scala
val keyValueList = Array(("fruit", "apple"), 
                         ("fruit", "banana"), 
                         ("vegetable", "onion"))
val pairRDD = sc.parallelize(keyValueList)
val groupByPairRDD = pairRDD.groupByKey()
groupByPairRDD.foreach(println)
// (vegetable,CompactBuffer(onion))
// (fruit,CompactBuffer(apple, banana))
```

##### mapValues(func)
`map((key, value) => (key, func(value)))`와 같은 연산이다.
key의 변경없이, Value에만 특정 로직을 처리하고 싶은 경우에 사용한다.
```scala
val keyValueList = Array(("fruit", "apple"), 
                         ("fruit", "banana"), 
                         ("vegetable", "onion"))
val pairRDD = sc.parallelize(keyValueList)
pairRDD.mapValues(x => "my " + x)
       .foreach(println)
// (fruit,my apple)
// (fruit,my banana)
// (vegetable,my onion)
// (fruit,my orange)
```


##### flatMapValues(func)
`mapValues(func)`을 수행한 뒤 `flatMap(x => x)`을 수행한 것과 같은 연산이다.
```scala
val keyValueList = Array(("vegetable", "onion"))
val flatMapValueRDD = pairRDD.flatMapValues(x => "my " + x)
                             .foreach(println)
// (vegetable,m)
// (vegetable,y)
// (vegetable, )
// (vegetable,o)
// (vegetable,n)
// (vegetable,i)
// (vegetable,o)
// (vegetable,n)
```

#### 실제 활용사례
##### 워드카운팅
Pair RDD는 본문의 단어의 갯수를 셀 때 유용하다!
재미로 구글플레이 현재 인기앱 1위인 [카카오뱅크의 디스크립션](https://play.google.com/store/apps/details?id=com.kakaobank.channel&hl=ko)으로 워드카운팅을 해보기로 했다.

```scala
val descRDD = sc.textFile("kakao_bank_desc.txt")
val words = descRDD.flatMap(x => x.split(" "))
val result = words.filter(x => x.matches("[(가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9)]+"))	// 특문제거
                  .map(x => (x, 1))
                  .reduceByKey((x, y) => x + y)
                  .sortBy(x => x._2, ascending = false)
                  .collect()
                  .foreach(println)

```
```text
(및,8)
(카카오뱅크,5)
(수,4)
(수수료,4)
(면제,4)
(은행,3)
(선택,3)
(내,2)
(이체,2)
(쉬운,2)
(제휴,2)
(더,2)
(OS를,2)
(ATM,2)
(앱,2)
(혜택,2)
(접근권한은,2)
(경우,2)
(송금,2)
(이상,2)
...이하생략...

```

#### 2개의 Pair RDD에 대한 트랜스포메이션
##### subtractByKey(rdd)
파라미터로 들어온 Pair RDD와 같은 키를 가지는 아이템들을 제거한다. 이때, 없는 키는 무시한다.
```scala
val nameRDD = sc.parallelize(Array(("1111", "yena"), ("2222", "seungha"), ("3333", "pinga")))
val scoreRDD = sc.parallelize(Array(("1111", 100), ("2222", 50), ("4444", 10)))
nameRDD.subtractByKey(scoreRDD).collect().foreach(println)
// (3333,pinga)
```
##### cogroup(rdd)
여러개의 RDD가 동일한 타입의 키를 가지고 있을 경우, 해당 키로 데이터를 그룹화 할 수 있다.
한쪽 RDD에 존재하지 않는 키가 있다면 결과 RDD의 Value배열에 그냥 빈칸으로 추가간다.
```scala
val pairRDD1 = sc.parallelize(Array(("a", 1), ("b", 2)))
val pairRDD2 = sc.parallelize(Array(("c", 1), ("b", 4)))
pairRDD1.cogroup(pairRDD2).foreach(println)
// (a,(CompactBuffer(1),CompactBuffer()))
// (b,(CompactBuffer(2),CompactBuffer(4)))
// (c,(CompactBuffer(),CompactBuffer(1)))
```
`cogroup(rdd)`은 조인을 구현하기 위해 자주쓰인다!

##### join(rdd)
Pair RDD로 작업 시, 조인을 해야하는 경우를 만날때가 많다. 조인은 아래에서 따로 다루도록 하겠다.


## 데이터 조인
동일한 타입의 키를 가지는 Pair RDD가 여러개 있을때, 해당 키로 조인하는 경우가 많을 것이다!
스파크에서는 모든 종류의 조인을 지원한다. (Left/Right Outer Join, Cross Join, Inner Join 등)
```scala
val nameRDD = sc.parallelize(Array(("1111", "yenarue"), ("2222", "tiffany"), ("3333", "steve")))
val scoreRDD = sc.parallelize(Array(("1111", 100), ("2222", 50), ("4444", 10)))
nameRDD.join(scoreRDD).collect().foreach(println)	// inner join
// (1111,(yenarue,100))
// (2222,(tiffany,50))
```
Outer Join 시, 한쪽에 해당 키나 값이 없으면 Some/None으로 처리한다.
```scala
nameRDD.leftOuterJoin(scoreRDD).collect().foreach(println)
// (1111,(yenarue,Some(100)))
// (2222,(tiffany,Some(50)))
// (3333,(steve,None))
nameRDD.rightOuterJoin(scoreRDD).collect().foreach(println)
// (4444,(None,10))
// (1111,(Some(yenarue),100))
// (2222,(Some(tiffany),50))
```

## 파티션 개수 지정하기! (병렬화 퀼리티 최적화 하기!)
모든 RDD는 고정된 개수의 파티션을 가지고 있으며, 이에따라 RDD연산이 처리될 때 병렬 작업의 퀼리티가 결정된다.
기본적으로 스파크는 현재 클러스터의 사이즈에 맞는 적절한 파티션의 개수를 찾아 동작하지만,  더 나은 퍼포먼스를 내기 위해서는 직접 지정해 줘야 할 경우도 있을 것이다.
Pair RDD의 트랜스포메이션 연산 시, 특정 개수의 파티션을 사용할 것을 지정할 수 있다! 어떻게 지정하는지 알아보자!
일반적으로, 마지막 파라미터로 파티션 개수를 받는다.
```scala
pairRDD.reduceByKey((x,y) => x + y)			// 스파크가 알아서 판단한 수준의 병렬화 (기본)
pairRDD.reduceByKey((x,y) => x + y, 10)		// 파티션 10개! (병렬화 수준을 지정했다!)
```

#### repartition()
해당 연산시 지정하지 않고 그 이후에 지정하고 싶다면 `repartition()` 을 호출하여 재파티셔닝 하면된다.
다만, 이미 파티션의 개수가 정해진 RDD를 **재**파티셔닝 하는 것이므로 기존 파티션들에 배정된 데이터들도 새로운 파티션으로 **재**셔플링이 일어난다! 너무 남발하면 성능에 영향을 줄 수 있으므로 `repartition()`을 사용해야 할 때에는 이 부분을 조심하도록 하자!
```scala
pairRDD.reduceByKey((x,y) => x + y)			// 스파크가 알아서 판단한 수준의 병렬화 (기본)
pairRDD.repartition(10)						// 파티션 10개!
```

#### coalesce()
`repartition()` 처럼 파티션의 수를 설정하지만 `coalesce()`는 파티션 수를 줄이는 것만 가능하다.
`coalesce()`는 강제로 셔플링 설정을 하지 않는 이상, 셔플을 진행하지 않는다. 그렇기에 `repartition()`보다 성능이 좋다.
파티션 수를 줄일때는 `coalesce()`를, 늘릴때는 `repartition()`을 쓰도록 하자!




