2. `Observable`에 대하여
=======
※ 개인적으로 이해한 내용을 적은 것이므로 잘못되었거나 부족한 내용이 있을 수 있습니다.

# Observable의 종류
발행가능한 데이터 개수별 Observable 종류는 다음과 같다.

|  | 0개 | 1개 | 여러개 |
|--------|--------|-------|-------|
| **동기적** | void doSometing() | T getData() | Iterable<T> getData() |
| **비동기적** | Completable doSometing() | Single<T> getData() | Observable<T> getData() |


일단은 발행 가능한 데이터 개수 별로 나눈 Observable 들을 어떻게 사용하는지 살펴보도록 하자.

## Completable
반환형 없이 성공/실패만을 반환할 때 사용하는 `Observable`이다. `Observable<Void>`라고 생각하면 편하다.
`onComplete()`와 `onError(Throwable)`만를 가지는 `CompletableObserver`로 구독한다.

Retrofit2와 함께 사용하려면 성공/실패만을 반환하는 PUT method API에 사용하면 된다.

```java
@PUT("/user")
Completable postUser(@Body User user);

userApi.postUser()
	.subscribe(() -> System.out.println("success!"),
    	t -> System.out.println("fail!"));
```

## Single
**단일 값** 스트림을 다룬다. `onSuccess(T)`와 `onError(Throwable)`만을 가지는 `SingleObserver`로 구독한다.
API를 설계할때 유용하다. 실제로 Retrofit2와 함께쓰면 매우 편하다.

```java
@GET("/user/{id}")
Single<User> getUser();

...

userApi.getUser()
	.subscribe(user -> System.out.println(user),
    	t -> System.out.println(t.getMessage()));
```

## Observable
**다중 값** 스트림을 다룬다. 다중 값 개수에는 제한이 없는 무한스트림이다.
`onNext(T)`, `onComplete()`, `onError(Throwable)`를 가지는 `Observer`로 구독한다.

* `onNext(T)`는 데이터를 발행하는 메서드로서 여러번 호출 될 수 있다. 
* `onComplete()`는 발행을 종료하고 Stream을 닫는 메서드로서 단 한번만 호출될 수 있다.
* `onError(Throwable)`은 Exception과 같이 에러가 발생하였을 때 Throwable을 발행하고 Stream을 닫는 메서드로서 단 한번만 호출 될 수 있다.

```java
Observable.just(1, 2, 3,4)
	.subscribe(num -> System.out.println("num"),
           t -> System.out.println(t.getMessage()),
           () -> System.out.println("completed!"));
```

데이터를 발행하는 Observable의 종류에 대해서 알아보았다. 사실은 '다중 값을 가지는 `Observable`'에도 더 다양한 종류가 있지만 일단은 발행 개수로 분류해서 살펴보았다. 더 다양한 `Observable`에 대해서는 추후에 살펴보기로하고 이어서 이러한 Observable들을 구독하는 것에 대해 알아보도록 하자.

# Observable 구독하기
하나의 Observable은 여러 구독자(Subscriber)를 가질 수 있다. 물론 구독자는 구독을 해지할수도 있다. 코드상으로 어떻게 해지하는지 알아보자!

## `Subscription` 과 `Subscriber<T>`
`Observable`을 구독(`subscribe()`)하면 `Subscription`이 반환된다. 말그대로 '구독'을 뜻하는 객체로서 이 인스턴스를 이용하여 구독을 해지할 수 있다.

```java
// 1.x
Subscription subscription = Observable.just(1, 2, 3, 4)
	.subscribe(num -> System.out.println("num"),
           t -> System.out.println(t.getMessage()),
           () -> System.out.println("completed!"));

subscription.unsubscribe();


// 2.x
Disposable disposable = Observable.just(1, 2, 3, 4)
	.subscribe(num -> System.out.println("num"),
           t -> System.out.println(t.getMessage()),
           () -> System.out.println("completed!"));

disposable.dispose();
```

메모리 릭이나 불필요한 부하를 피하려면 **구독 해지를 꼭 해줘야한다.**
RxJava 2.x버전에서는 `Disposable` 으로 변경되어 몇몇 `Observer`들은 `onComplete()`, `onError()` 시에 알아서 `dispose()`를 시키기도 한다.

> 코드를 까보니 그러하던데 모든 `Observer`가 그렇지는 않는 것 같다. 사용되는 `Observer`를 확인해보고 `dispose()`하는 습관을 들여야겠다. 확인해볼 시간이 없다면 그냥 `dispose()`하도록 하자.
> 어차피 `onComplete()`, `onError()` 시에 스트림이 종료되는데... 굳이 구독해지를 따로 해줘야하도록 되어있는 이유는 뭘까.....ㅠㅠ

# Observable 만들기
Observable을 만드는 방법은 다양하다 :-)

## 만드는 방법들
### `Observable.empty()`
아무런 값도 발행하지 않고 구독을 즉시 종료하는 Observable이다. 테스트 코드 작성시에만 가끔 써봤다..

```java
Observable.empty()
	.subscribe(obj -> System.out.println("onNext"),
    		t -> System.out.println("onError"),
            () -> System.out.println("onComplete"));

// =============== console ==============
// onComplete
```

### `Observable.never()`
아무런 값도 발행하지 않는다. 심지어 종료도 안한다. 아무것도 안한다. 테스트코드 작성시에만 가끔 써봤다..2

```java
Observable.never()
	.subscribe(obj -> System.out.println("it can't be called NEVER"),
    		t -> System.out.println("it can't be called NEVER"),
            () -> System.out.println("it can't be called NEVER"));
```

### `Observable.error()`
에러만을 발행하는 옵저버블이다. 구독 즉시 `onError()`가 호출된다. 테스트코드 작성시에 자주쓰인다.

```java
Observable.error(new Exception())
	.subscribe(obj -> System.out.println("onNext"),
    		t -> System.out.println("onError"),
            () -> System.out.println("onComplete"));

// =============== console ==============
// onError
```

### `Observable.range(from, n)`
from에서부터 n개까지의 정수가 나열된 스트림을 발행한다.
```java
Observable.range(10, 5)
	.subscribe(num -> System.out.print(num + " "),
    		t -> System.out.println("onError"),
            () -> System.out.println("onComplete"));

// =============== console ==============
// 10 11 12 13 14 onComplete
```

### `Observable.just(value...)`
Observable을 만드는 가장 간단한 방법이다. 테스트 코드 작성시 자주쓰인다.

```java
Observable.just(1, 2, 3)
	.subscribe(num -> System.out.print(num + " "),
    		t -> System.out.println("onError"),
            () -> System.out.println("onComplete"));

// =============== console ==============
// 1 2 3 onComplete
```

### `Observable.from(values)`
Observable from시리즈는 종류가 더 다양하다. 일단은 흐름상, `fromArray()`와 `fromIterable()`만을 살펴보자. 나머지는 `Observable.create()`와 함께 살펴보도록 하겠다!

#### `Observable.fromArray(values...)`
`just()`와 비슷하다. `just()`의 구현부를 열어보면 일일히 파라미터를 하나 씩 추가하며 오버로딩을 한 고생(?)이 보이는데, `fromArray()`는 스프레드 연산자(`...`)를 사용해서 좀 더 우아하게 풀어냈다.

```java
Observable.fromArray(1, 2, 3);
```

#### `Observable.fromIterable(iterable)`
`Iterable`을 상속하는 객체로 `Observable`을 생성한다. `Collection`들은 거의 다 할 수 있다. `List` 사용할 때 매우 편하다.

```java
List<Integer> integerList = Arrays.asList(1, 2, 3);
Observable.fromIterable(integerList);
```

## 만드는 방법 2 - next, error, complete 이벤트를 emit하는 방식
위에서는 기존 자료구조에서 손쉽게 `Observable`로 변환시키는 메서드들을 알아봤다. 그렇다면 DB에서 읽어오는 작업을 하거나 무거운 연산 이후에 발행할 데이터들을 가져오는 경우에는 어떻게 할까? 그런 작업들을 비동기처리하여 편하게 사용하려고 RxJava를 쓰는건데, `Observable`을 만들기 위한 전처리를 또 따로 처리해야한다니 좀 이상하다는 생각이 들었을 것이다.

당연히 RxJava에서는 `Observable`을 직접 만들 수 있는 `create()`, `fromCallable()`, `fromPublisher()`를 제공한다.

사실 실제 구현부에서는 이 방식으로 만드는 경우가 더 많다. ~~물론 때에 따라 다르지만!~~

### `Observable.create(subscriber)`
Observable을 직접 만드는(create) CREATOR 메서드이다. **로우 레벨의 저수준 연산자**로서 디테일한 부분까지 신경써서 만들어줘야한다. 그렇지 않으면 메모리 릭 등의 문제가 발생할 수 있다. 그렇다보니 학습용으로는 굉장히 좋다(!). 하지만 제대로 사용할 수 있을지 의문...이니 꼭 필요한 경우가 아니라면 최대한 `fromCallable()`을 사용하도록 하자.

> 1.3 이전 버전에는 `fromEmitter()` 라는게 존재했었다. 정말 잘 사용했었는데 1.3 이후 부터는 삭제되었더라. 왜 인지는 모르겠다. 문제가 있었나 보다ㅠㅠ

```java
Observable.create(subscriber -> {
	subscriber.onNext(1);
    subscriber.onNext(2);
    subscriber.onNext(3);
    subscriber.onComplete();
});
```

Observable의 동작 방식이 궁금하다면 아래와 같이 학습용으로 테스트해볼 수도 있겠다.

```java
Observable observable = Observable.create(subscriber -> {
	System.out.print(" Create");
    subscriber.onNext(1);
    subscriber.onComplete();
});
//observable.cache();

System.out.println("Start!");
observable.subscribe(num -> System.out.print(" [1]: " + num));
observable.subscribe(num -> System.out.println(" [2]: " + num));
System.out.println("End!");

// ============= console ==============
// Start!
// Create [1]: 1 Create [2]: 1
// End!

// after cache()
// ============= console ==============
// Start!
// Create [1]: 1 [2]: 1
// End!
```
위의 코드를 통해 `Observable`은 구독시 마다 재연산 된다는 것을 알 수 있었다. 재연산없이 발행되기를 원한다면 `cache()`연산자를 쓰도록하자.

다시 한번 말하자면, 공부용으로 사용하거나 정말로 Observable 동작방식을 완전히 마스터해서 전혀 문제없이 사용할 수 있는 정도일때만 사용하도록 하자...

### `Observable.fromCallable(func)`
함수를 이용해 단일 값을 반환하는 `Observable`을 만들 수 있다. `Subscriber`를 직접 컨트롤하는 것이 아니기 때문에 `create()`보다 훨씬 안전하다.

```java
Observable<List<String>> observable = Observable.fromCallable(() -> {
	List<String> list = new ArrayList<>();
    list.add("test1");
    list.add("test2");
    return list;
});

// 모델에서 데이터를 가져와서 Observable을 만들어야할때 쓰기 좋다.
Observable.fromCallable(UserDAO::findAll)
```

### `Observable.fromPublisher(publisher)`
사실 처음엔 `fromPublisher()`가 `create()`보다 더 안전한 메서드 일 줄 알았다. 하지만... [RxJava2 Document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#fromPublisher-org.reactivestreams.Publisher-)를 확인해보니, 아래와 같은 말이 나왔다.
> "If possible, use create(ObservableOnSubscribe) to create a source-like Observable instead."

나름대로 반전이었다. RxJava2 로 업데이트 되면서 `create()`가 좀 더 보완된걸까? 앞으로는 `create()`를 좀 더 예뻐해 줘야겠다.

그리고 또 다른 반전 :
> "Note that even though Publisher appears to be a functional interface, **it is not recommended to implement it through a lambda** as the specification requires state management that is not achievable with a stateless lambda."

람다표현식으로 만들지 말라고 한다(!!!!). Document확인의 중요성과 필요성..

`Publisher`를 제대로 사용하려면 꼭 **Reactive Stream 스펙**을 지켜야 한다고 한다. 그래서 더 까다롭게 다루라는 것 같다. 자신없으면 `create()`를 쓰라고..
>**The Publisher must follow the [Reactive-Streams specification](https://github.com/reactive-streams/reactive-streams-jvm#reactive-streams).** Violating the specification may result in undefined behavior.

내 추측으로는 RxJava1에서의 `fromEmitter()`가  RxJava2에서는 `create()`로 대체되고, RxJava1에서의 `create()`가 `fromPublisher()`로 바뀐게 아닐까싶다. `fromPublisher()`에 대한 문서를 읽을 수록 점점 예전의 `create()`와 비슷한 것 같아서... **문서에 `fromPublisher()`대신`create()`를 쓰라고 아예 명시되어있는 것만 봐도 그렇다.**

정확하게는 [RxJava 1.x의 create() JavaDoc](http://reactivex.io/RxJava/1.x/javadoc/rx/Observable.html#create-rx.observables.Observable.OnSubscribe-)과 [RxJava 2.x create() JavaDoc](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#create-io.reactivex.ObservableOnSubscribe-)을 비교해보도록 하자.
확실히 RxJava2에서는 `create()`가 꽤나 보완된 듯 하다.

# 중간 결론
확실히 알면알수록 1버전대와 2버전대는 꽤나 큰 차이를 가지는 것 같다. 물론 리액티브 사상과 개념은 동일하지만 사용법이나 성능 및 주의사항에 대해서는 거의 다른 라이브러리라는 생각으로 다뤄야 하는 것 같기도...

[RxJava1 버전의 Github README](https://github.com/ReactiveX/RxJava/blob/1.x/README.md)를 읽어보면 자신있게 Stable 버전이라고 적어뒀다.
> **Version 1.x is now a stable API** and will be supported for several years.

[RxJava2 버전의 Github README](https://github.com/ReactiveX/RxJava/blob/2.x/README.md)를 읽어보면 RxJava2도 Stable 버전으로 고려되고 있다고 한다. 확정짓지 않고 애매하게 적어두었다.
>**Version 2.x is now considered stable and final.** Version 1.x will be supported for several years along with 2.x.

RxJava3이 개발중에 있으니 슬슬 RxJava2도 완전히 안정화시키고 fixed처리 할 듯하다.