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
Blocking 방식 및 명령형 방식으로 구현된 기존 프로젝트에 RxJava를 결합하기 위해서는 `Observable`을 컬렉션으로 변환해야 하는 경우가 생긴다.

## 사용법
`toBlocking`연산으로 `Observable`을 `BlockingObservable`을 만들 수 있다.

```java
BlockingObservable<Integer> observable = Observable.just(1, 2, 3).toBlocking();
```

### single()
`BlockingObservable`에서 값을 가져온다. 딱 하나의 이벤트만을 받아온다는 것이 보장된다. `onCompleted()`이 호출될 때 까지 블록된다.

```java
List<Integer> intList = Observable.just(1, 2, 3).toList().toBlocking().single();
```

그렇다면 도중에 `onError()`가 발생하면 어떻게 될까?
```java
System.out.println("111");
Observable.error(new Exception("error!!!"))
	.doOnError(e -> System.out.println("error"))
    .doOnTerminate(() -> System.out.println("terminated"))
    .doOnCompleted(() -> System.out.println("completed"))
    .doOnUnsubscribe(() -> System.out.println("unsubscribe"))
    .toBlocking().first();
System.out.println("222");

// [output]
// 111
// error
// terminated
// unsubscribe
// Exception in thread "main" java.lang.RuntimeException: java.lang.Exception: error!!!
//	at rx.exceptions.Exceptions.propagate(Exceptions.java:57)
//	at rx.observables.BlockingObservable.blockForSingle(BlockingObservable.java:463)
//	at rx.observables.BlockingObservable.first(BlockingObservable.java:166)
//	at com.yenarue.rxjava.Main.main(Main.java:14)
// Caused by: java.lang.Exception: error!!!
//	at com.yenarue.rxjava.Main.main(Main.java:9)
```
Exception이 바로 throw되며 프로그램이 멈춘다. Blocking처리시 error처리에 유의하도록 하자.

### first()
`BlockingObservalbe`에서 하나의 값만 가져오고 나머지는 버린다.

```java
Integer integer = Observable.just(1, 2, 3).toBlocking().first();
System.out.println(integer);

// [output]
// 1
```

### 주의사항
Blocking은 Observable의 주요 사상을 크게 위반하는 것이므로 꼭 필요한 경우에만 사용하자.
꼭 사용해야 하는 경우에는 최대한 일반 `Observable`로 체이닝 연산을 진행하다가 `toBlocking()`연산은 최대한 나중에 연결하도록 하자.


# [Observable.defer()](http://reactivex.io/documentation/operators/defer.html)

`Observable`을 Lazy하게 만드는 연산자. `Observable`은 원래 Lazy Excution이지 않나? 싶겠지만 `defer()`는 `Observable`의 실제 **생성**을 늦춘다 (Lazy Initialize). 즉, 구독(subscribe)시 생성이 이루어지는 것이다.

Lazy Initailize라는 특성으로 인해 기존 프로젝트에 존재하는 블로킹 코드를 간단하게 `Observable`로 포장할 수 있다.

![](http://reactivex.io/documentation/operators/images/defer.c.png)

다른 create opterator들과의 차이가 잘 와닿지 않는다면 아래 예제를 보시라!

```java
setValue(1);
Observable<Integer> observable = Observable.just(getValue());
observable.toBlocking().subscribe(System.out::println);

setValue(2);
observable.toBlocking().subscribe(System.out::println);
```
어떤 결과가 예상되는가? `1`, `2`가 출력될 것이라 생각했는가?
두구두구두구 결과는!!
```console
1
1
```
왜 이런 결과가 나오는 것일까? 생성된 Observable의 연산자들이 Lazy Excution인 것일 뿐이지 `defer`를 제외한 [Observable을 생성하는 Operation들](http://reactivex.io/documentation/operators.html#creating) 들은 모두 조급하게 실행되기 때문이다.

플로우를 나타내보자면 아래와 같다.
> setValue(1) → **value(1)을 발행하는 Observable 생성** → subscribe →  1 발행
→ setValue(2) → subscribe → 1 발행

value가 1으로 set된 상태로 observable이 만들어 졌고, 그 observable에 대해서 subscribe 한 것이기 때문에 중간에 value를 2로 set해도 observable은 변하지 않는 것이다.

이제 위의 예제에 `Observable`의 생성을 지연시켜주는 `defer`를 적용해보도록 하겠다.
```java
setValue(1);
Observable<Integer> observable = Observable.defer(() -> Observable.just(getValue()));
observable.toBlocking().subscribe(System.out::println);

setValue(2);
observable.toBlocking().subscribe(System.out::println);
```
이제 `Observable.just(getValue())`에 대한 생성이 `defer`되었다. 해당 Observable은 실제 **구독(`subscribe`)시에 생성**된다.

이제 플로우는 아래와 같다.
> setValue(1) → subscribe → **1을 발행하는 Observable 생성** → 1 발행
→ setValue(2) → subscribe → **2를 발행하는 Observable 생성** → 2 발행

실행 결과는 다음과 같이 의도대로 나오게 된다 :-)
```console
1
2
```

## 사용예시
```java
Observable<Item> allItemObservable(int page) {
	return Observable.defer(() -> Observable.from(itemObservable(page)))
		.concatWith(defer() -> AllItemObservable(page + 1));
}
```

```java
observable<List<Person>> allages = Observable.range(0, Integer.MAX_VALUE)
	.map(this::listPeople)
    .takeWhile(list -> !list.isEmpty())
```

