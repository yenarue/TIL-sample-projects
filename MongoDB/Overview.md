MongoDB - Overview
====

# 개요
**처리 성능**과 **쉽고 가벼운 데이터 액세스**를 가장 중요한 목표로 갖는 확장성 있는 데이터베이스이다. 대량의 데이터를 가공하거나 분산처리 등의 무거운 연산을 수행해야 할 때 유용하다.

## MongoDB는 언제 쓸까?
* **대규모 데이터 스토리지가 요구되는 프로젝트에서 코드는 계속 늘어나지만 값 비싸고 강력한 대형의 하드웨어를 구입할 예산이 모자란 경우** 사용하면 좋다.
    * Why?

# 특징
## Document형 데이터베이스
* 데이터가 중첩된 상태로 영속성을 갖게 함 (데이터안에 또 다른 데이터)
* 중첩된 데이터를 애드혹 형태로 쿼리 가능 (즉석 쿼리)
    * 인덱싱 처리 되기 때문에 성능에 문제 없음

## 강제화된 스키마가 없다
유연하다. 새로운 컬럼 추가 시 부담이 없다.

## 빠르다
인덱싱, 그룹화, Mapreduce

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