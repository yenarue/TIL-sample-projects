MongoDB - Overview
====

'[만들면서 파악하는 NoSQL 7 데이터베이스 (원제 : Seven Databases In Seven Weeks)](http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=24714891)'를 읽고 정리한 글입니다.


# 개요
**처리 성능**과 **쉽고 가벼운 데이터 액세스**를 가장 중요한 목표로 갖는 확장성 있는 데이터베이스이다. 대량의 데이터를 가공하거나 분산처리 등의 무거운 연산을 수행해야 할 때 유용하다.

## MongoDB는 언제 쓸까?
* **대규모 데이터 스토리지가 요구되는 프로젝트에서 코드는 계속 늘어나지만 값 비싸고 강력한 대형의 하드웨어를 구입할 예산이 모자란 경우** 사용하면 좋다.
    * Why?

# 특징
## Document형 데이터베이스
* 데이터가 **중첩**된 상태로 영속성을 갖게 함 (데이터안에 또 다른 데이터)
* 중첩된 데이터를 애드혹 형태로 쿼리 가능 (즉석 쿼리)
    * 인덱싱 처리 되기 때문에 성능에 문제 없음

## 강제화된 스키마가 없다
유연하다. 새로운 컬럼 추가 시 부담이 없다.

## 빠르다
인덱싱, 그룹화, MapReduce, 쿼리 병렬처리 등...

## Join 연산을 지양한다
Join 연산 자체가 비효율적인 연산이기 때문이다. Reference 관계를 허용하지 않는 다는 뜻이 아니다. 필요한 경우, `{ $ref: "컬렉션명", $id "참조id" }` 형태와 같이 사용하고 아래와 같이 가져오면 된다.

```javascript
// // Document : towns
// {
//     city : "portland",
//     country : {
//         "$ref" : "countries",
//         "$id" : "us"
//     }
// }

var portland = db.towns.findOne({"city" : "portland"})
var countryOfPortland = db[portland.country.$ref].findOne({ _id : portland.country.$id })
```

## 커스텀 코드
`find` 나 `where`, `match` 등의 Select관련 쿼리시 Custom method 를 사용할 수 있다.

```javascript
db.towns.find(function() {
    return this.population > 5000 && this.population < 500000;
})

db.towns.find("this.population > 5000 && this.population < 500000")

db.towns.find({
    $where : "this.population > 5000 && this.population < 500000",
    famous_for : /groundhog/
})
```

커스텀 코드는 **최후의 수단**으로 사용하는 것이 좋다. 연산 수행으로 인해 **속도가 매우 느려지며**, **인덱스를 사용할 수도 없고**, **자동 최적화도 되지 않기 때문**이다.

# 인덱싱
MongoDB의 유용한 내장 기능 중 하나. 쿼리의 성능을 높혀준다.
* 인덱싱 타입 : B-tree, GeoSpatial 등..
* 인덱싱 정보 : `system.indexes` 컬렉션에서 확인 가능
* 인덱싱 생성 : `ensureIndex(fields, options)`
    * 자동으로 `_id` 필드가 인덱싱되지만 실제 상황에서 `_id`로 `find()`하는 경우는 드물 것이다. `ensureIndex`를 사용하여 자주 조회되는 특정 필드를 인덱싱 하자.
* 성능 측정 : `explain()` 메소드 호출로 가능 - 인덱싱 전후 성능을 측정해보자.

## 주의 사항
대형 컬렉션의 경우, 인덱싱 생성이 느리고 자원을 많이 사용하게 된다.
* 시스템이 덜 바쁠 때, 백그라운드 작업으로 생성하자.
* 자동 생성보다는 수동 생성이 낫다.

# 그룹화
## 집계 쿼리
count(), distinct, group 등...

# 전환 (Diversion)
* 클라이언트가 아닌 서버에서 실행되도록 전환
* PostgreSQL의 stored procedure와 유사 => ?

## 서버 측 명령 : `eval(function)`, `runCommand()`
### 클라이언트 측 실행
아래 코드는 클라이언트 측에서 읽고 처리하면서 하나씩 하나씩 원격 서버(mongodb)에 저장하게 된다.
```js
const update_area = function() {
    db.phones.find().forEach(function(phone) {
        // phone 을 이용한 각종 연산들...
        db.phones.update({ _id : phone._id}, phone, false);
    })
}
```
### 서버(여기서는 MongoDB) 측 실행
아래 코드는 `update_area` 함수의 로직을 서버단에서 실행한다. 클라이언트와 서버와의 커뮤니케이션 비용이 감소된다!
```js
db.eval(update_area)
```

### `system.js`에 함수 저장하기
javascript 함수를 `system.js` 컬렉션에 저장할 수 있다.
```js
db.system.js.save({
    _id : 'getLast',
    value: function(collection) {
        return collection.find({}).sort({'_id' : 1}).limit(1)[0]
    }
})

db.eval('getLast(db.phones)')
// = db.system.js.findOne({'_id' : 'getLast'}).value(db.phones)
```

### 주의사항
* `eval()`은 mongod 서버 실행에 방해가 되므로 일상적인 실무 절차로 사용하는 것 보다는 코드 테스트 시에 틈틈히 사용하는 것이 좋다.

# MapReduce
함수형 프로그래밍의 `map`연산과 `reduce`연산을 주로 사용하여 만들어진 분산/병렬 처리 관련 소프트웨어 프레임워크이다.
이름때문에 프레임워크와 실제 `MapReduce`연산이 혼동되는 경우가 많은 듯 하다...^^:
* 개념을 잘 설명한 동영상 : https://www.youtube.com/watch?v=gI4HN0JhPmo
* 전형적인 분할 정복

# Sharding - 하나의 컬렉션을 여러개로 분할
컬렉션의 모든 값을 하나의 서버가 갖는 것이 아니고 값의 범위에 따라 다른 서버들로 분할 하는 것.

## 예
숫자 컬렉션의 0~100까지는 서버A에, 101~200까지는 서버 B에

MongoDB는 이러한 분할과정을 자동 샤딩으로 쉽게 분할해준다. (Auto Sharding)

## 샤딩 서버 실행해보기
### 샤딩이 가능한 서버 만들기 : `--shardsvr`
```bash
$ mkdir ./mongo4 ./mongo5
$ mongod --shardsvr --dbpath ./mongo4 --port 27014
$ mongod --shardsvr --dbpath ./mongo5 --port 27015
```

### 구성 서버 만들기 (Config Server) : `--configsvr`
```bash
$ mkdir ./mongoconfig
$ mongod --configsvr --dbpath ./mongoconfig --port 27016
```

### mongos 실행하기
클라이언트를 위한 point of entry (단일 진입점)이다. mongoconfig 서버에 저장된 샤딩 정보를 사용한다.

```bash
$ mongos --configdb localhost:27016 --chunkSize 1 --port 27016
```

#### 근데 mogos가 뭐지?
mongod 서버의 클론. mongod의 모든 command를 사용할 수 있으면서도 mongod보다 리소스를 적게 사용하는 점(경량)이 장점이다. 이러한 면으로 인해 샤딩 서버들에 클라이언트를 연결시켜주는 브릿지 역할로 딱이다.

```chart
클라이언트
    |
 [        mongos        ] ------ [ 구성서버 - mongod ]
    |               |
[ 샤딩서버1 ]    [ 샤딩서버2 ]
[ (mongod) ]    [ (mongod) ]
```

## 문제점
컬렉션의 한 부분이 유실되면 전체가 위태로워질 수 있다. => ReplicaSet으로 해결

# ReplicaSet - 데이터를 다른 서버에 복사
하나의 인스턴스만 실행되는 경우는 거의 없다. 여러 서버에 걸쳐 실행되면서 저장된 데이터를 복제한다.

* Primary : Master Server. READ/WRITE
* Secondary : READ/WRITE 모두 불가능

마스터 노드에 장애가 생기면, 가장 최신의 데이터를 갖는 것이 새로운 마스터로 선출된다.

* 대부분의 노드에 마스터의 데이터가 복사되어야 하지만 실제 데이터로 인지한다.
    * 다른 노드에 복사되지 않고 마스터 노드에만 데이터가 존재하는 경우, 해당 오퍼레이션은 제외되어 버린다. 
    * 위와 같은 이유로 과반수가 성립될 수 있도록 ReplicaSet의 개수는 언제나 홀수로 유지되어야 한다. (3, 5 ...) 그렇지 않으면 에러가 나며 정상적으로 동작하지 않는다. (MongoDB의 철학)

## 다시한번 살펴보는 MongoDB의 특성
* 스케일 아웃 형태의 여러 서버들로 실행되도록 만들어져있다.
* 데이터 일관성 유지, 분할 장애 대응 (partition tolerance)
* 초대형 데이터셋을 안전하고 빠르게 처리하는 것.
    * 값의 범위에 따른 수평적 분할 (Sharding)이 되어야 가능

이러한 MongoDB의 특성은 Sharding과 ReplicaSet이 있었기 때문에 가능했던 것이다.



# 그 외
## GeoSpatial
## GridFS

# 마무리
## 장점
## 단점