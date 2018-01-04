04_기존 프로젝트 Migration하기
=======
※ 개인적으로 이해한 내용을 적은 것이므로 잘못되었거나 부족한 내용이 있을 수 있습니다.


명령형 방식으로 작성되어있는 프로젝트에 리액티브 프로그래밍을 바로 도입하는 것은 어렵다. RxJava를 사용하여 천천히 도입하고 싶다면 Blocking처리를 사용하면 일단은 기존 프로젝트에 1:1 매칭으로 마이그레이션 할 수 있다.

* 데이터베이스 쿼리, 캐싱, 오류처리, 주기적인 작업 등...

# Collection to Observable
Observable은 Iterable에 대응되는 쌍대(dual)이므로 1:1 변환 가능하다.
```java
List<Person> getPeople() {
    return query("SELECT * FROM PEOPLE");
}

Observable<Person> getPeopleObservable() {
    List<Person> people = query("SELECT * FROM PEOPLE");
    return Observable.from(people);
}
```
위에서 기존 프로젝트에서 `getPeople()`을 `getPeopleObservable()`으로 처음부터 완전히 대체하기에는 시스템 구조에 따라 문제가 발생할 수 있다. 그러므로 RxJava는 최대한 이른 시점에 도입하는 것이 좋으며 기존 프로젝트에 조금씩 도입하는 경우에는 Blocking 처리를 해주며 서서히 도입하는 것이 좋겠다. 그렇다면 Blocking 처리는 어떻게 해야할까?

# BlockingObservable


