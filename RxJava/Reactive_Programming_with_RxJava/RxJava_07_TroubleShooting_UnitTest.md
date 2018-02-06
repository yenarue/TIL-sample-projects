07_TroubleShooting & Unit Test
====

# TroubleShooting (271p)

## [리액티브 선언문](https://www.reactivemanifesto.org/ko)
리액티브 시스템의 4가지 특성 (응답성, 탄력성, 유연성, 메세지 중심적)

### 응답성
문제를 신속하게 탐지하고 효과적으로 처리할 수 있음을 의미.
신속하고 일관된 응답 시간.
오류처리 단순화

### 탄력성
시스템이 장애가 발생해도 응답성을 유지하는 것을 의미.
일부 시스템이 실패하여도 전체 시스템에 손상을 주지 않아야 한다.
또한 전체 시스템 손상없이 일부 시스템에 대한 복구가 가능해야 한다.
클라이언트에 오류를 처리하는 부담을 주지 않는다. (뷰단 단순화)

## 예외 처리
`onError` 외에도 다양한 방법으로 예외처리를 할 수 있다.

### 암묵적 예외 (Unchecked Exception)
메서드 선언시 명시할 필요가 없는 예외. ex) NullPointerException

### 명시적 예외 (Checked Exception)
메서드 선언시 명시해야하는 예외. ex) RuntimeException을 상속받지 않는 Throwable.

### `onError` 콜백을 정의하지 않은 `subscribe`이면?
`subscribe`를 호출한 스레드에서 `onErrorNotImplementedException` 발생.

### `Observable.create`가 예외를 던지면?
```java
// 1.
Observable.create(emitter -> {
	try {
    	emitter.onNext(1/0);
    } catch (Exception e) {
    	emitter.onError(e); // onError로 넘기기
    }
});

// 2. 내부적으로 잡아내서 오류 알림으로 변경해줌
Observable.create(emitter -> emitter.onNext(1/0));
```
그래도 1번처럼 `onError`를 통해 명시적으로 에러를 명시하는게 더 좋다.
그것보다 더  나은 건 내부적으로 에러처리를 해주는 `fromCallable`을 사용하는 것이다.

### 업스트림에서 예외가 발생하면?
다운스트림으로 전파된다.
```java
Observable.just("test", null)
	.map(String::length)
    .subscribe(str -> System.out.println("[onNext] " + str),
    	err -> {
        	System.out.println("[onError]"
        	err.printStackTrace());
        });
```
```output
[onNext] 4
[onError]
java.lang.NullPointerException
	at rx.internal.operators.OnSubscribeMap$MapSubscriber.onNext(OnSubscribeMap.java:69)
.....
Caused by: rx.exceptions.OnErrorThrowable$OnNextValue: OnError while emitting onNext value: null
	at rx.exceptions.OnErrorThrowable.addValueAsLastCause(OnErrorThrowable.java:118)
	at rx.internal.operators.OnSubscribeMap$MapSubscriber.onNext(OnSubscribeMap.java:73)
	... 37 more
```

## 오류를 대체하기!!
오류 발생 시, 특정 값으로 대체하거나 보조 스트림으로 대체할 수 있다.

### `onErrorReturn()`로 오류를 대체값으로 바꾸기
오류 발생 시, 대체값 사용하고 싶은 경우에 사용한다. try-catch 절 보다 가독성이 좋다.
```java
Observable.just("test", null)
	.map(String::length)
    .onErrorReturn(err -> 123)
    .subscribe(str -> log("[onNext] " + str),
    	err -> log("[onError] " + err));
```
```output
[onNext] 4
[onNext] 123
```

### `onErrorResumeNext()`로 오류를 보조스트림으로 대체하기
오류 발생 시, 보조스트림으로 대체한다. 대체 값을 느긋하게 계산하고 싶은 경우 사용한다.

```java
Observable.just("test", null)
	.map(String::length)
    .onErrorResumeNext(err -> Observable.just(123, 321))
    .subscribe(str -> log("[onNext] " + str),
    	err -> log("[onError] " + err));
```
```output
[onNext] 4
[onNext] 123
[onNext] 321
```

#### `flatMap()` 으로 onError, onComplete 시의 스트림 대체하기
```java
Observable.just(1, 2, 3)
	.flatMap(Observable::just,
    	err -> Observable.empty(), 		//오류발생시 빈 Observable로 대체 (오류무시)
        () -> Observable.just(4, 5, 6));	//완료시 다른스트림으로 대체
```
```output
[onNext] 1
[onNext] 2
[onNext] 3
[onNext] 4
[onNext] 5
[onNext] 6
[onCompleted]
```
`flatMap`으로도 오류를 대체할 수 있지만 가독성을 위해서는 `onErrorResumeNext()`를 사용하는 것이 더 낫겠다.

## 이벤트가 발생하지 않을 때 TimeOut처리하기
`timeout` 연산자로 타임아웃처리를 할 수 있다.
```java
Observable.just(1, 2, 3)
	.delay(3000, TimeUnit.MILLISECONDS)
    .timeout(100, TimeUnit.MILLISECONDS)
    .subscribe(str -> log("[onNext] " + str),
    	err -> log("[onError] " + err),
        () -> log("[onCompleted]"));
```
```output
[onError] java.util.concurrent.TimeoutException
```


------

# 단위테스트 (293p)
RxJava는 비동기식 이벤트 기반 아키텍처임에도 단위테스트를 훌륭하게 지원한다!

시간 관리, 순수 함수, 함수 합성을 중점적으로 두면 테스트 경험이 크게 향상된다.

## 방출 이벤트 검증하기 (284p)
* 이벤트가 올바른 순서로 방출되는지
* 오류가 제대로 전달되는지
* 연산자가 예상대로 구성되었는지
* 이벤트가 적절한 타이밍에 표시되는지
* BackPressure가 지원되는지

### 이벤트나 오류가 올바르게 방출되는가
Observable이 이벤트를 올바르게 방출하는지 확인하는 테스트를 작성해보자.

#### Blocking 처리 후 값 Assertion 하기
```java
@Test
public void 값검증하기_Blocking버전() throws Exception {
	List<String> list = Observable.just(1, 2, 3)
		.concatMap(x -> Observable.just(x, -x))
        .map(Object::toString)
        .toList()
        .toBlocking()
        .single();

	assertThat(list).containsExactly("1", "-1", "2", "-2", "3", "-3");
}
```

다음은 오류를 방출하는 경우를 테스트해보자. 그를 위해 조금 특별한 연산자를 사용하여 Observable을 만들어보도록 하겠다.

#### `concatMapDelayError`로 오류 미루기
스트림이 종료되기 전까지 발생한 모든 에러를 모아서 스트림 종료시 `CompositeException` 형태로 방출한다.

![](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/concatMapDelayError.o.png)

```java
public Observable<Integer> concatMapDelayErrorObservable() {
	return Observable.just(3, 0, 1, 0)
//		.map(x -> 100 / x)
        .concatMapDelayError(x -> Observable.fromCallable(() -> 100 / x));
}

public void concatMapDelayErrorSubscribe() {
	concatMapDelayErrorObservable()
    	.subscribe(str -> log("[onNext] " + str),
        	err -> log("[onError] " + err),
            () -> log("[onCompleted]"));
}
```
```output
[onNext] 33
[onNext] 100
[onError] rx.exceptions.CompositeException: 2 exceptions occurred.
```

위 Observable에 대한 테스트 코드를 작성해보자!

일단 예상대로 동작하는지 확인하기 위해 Event의 종류를 확인할 필요가 있다. (onNext, onError, onCompleted)

이벤트 종류를 확인하기 위해 `materialize()`연산을 부가적으로 추가하자.

![](http://reactivex.io/documentation/operators/images/materialize.c.png)

```java
@Test
public void concatMapDelayError_Blocking버전() throws Exception {
	List<Notification.Kind> notificationKinds = concatMapDelayErrorObservable()
        .materialize()
        .map(Notification::getKind)
        .toList()
        .toBlocking()
        .single();

	log(notificationKinds.toString());
    assertThat(notificationKinds).containsExactly(Notification.Kind.OnNext, Notification.Kind.OnNext, Notification.Kind.OnError);
}
```

##### 문제점
1. 값을 받기위해 블로킹 처리를 추가했다.
2. 이벤트 종류를 확인하기 위해 부가적인 연산([materialize()](http://reactivex.io/documentation/operators/materialize-dematerialize.html))이 추가되었다.

=> 테스트 코드의 **가독성**이 떨어진다.

#### TestSubscriber
* 테스트 대상 Observable의 구독자로 사용하여 내용을 검사한다.

* 내부적으로 받은 모든 이벤트를 저장한다.

* Assertion 메서드들을 제공한다.

`TestSubscriber`를 이용하면 테스트 코드에서 불필요한 블로킹 처리를 제거하고 테스트를 위한 부가적인 연산을 제거하여 가독성을 향상시킬 수 있다.

```java
@Test
public void concatMapDelayError_testSubscriber버전() throws Exception {
	TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

    concatMapDelayErrorObservable().subscribe(testSubscriber);

	testSubscriber.assertValues(33, 100);
    testSubscriber.assertError(CompositeException.class);
    testSubscriber.assertTerminalEvent();
```

#### TestScheduler
* `Schedulers.test()`
* 시간에 따른 이벤트의 흐름을 관찰할때 유용하다. (162p)
* 배압 처리가 유용하다. (300p)

##### advanceTimeBy(Time)
Sleep과 유사함. 실제로 Sleep되지는 않고 어떤 행동이 일어나기를 기다림.

Shedulers.immediate()는 동시성을 파괴하므로 좋지 않음.

##### 절대 끝나지 않는 Observable TimeOut테스트
```java
@Test
public void 이벤트가_발행되지_않을때_Timeout처리() throws Exception {
	TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
    TestScheduler testScheduler = Schedulers.test();

    Observable.never()
            .timeout(4000, TimeUnit.MILLISECONDS, Observable.empty(), testScheduler)
            .subscribe(testSubscriber);

    testScheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
    testSubscriber.assertNoTerminalEvent();
    testScheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
    testSubscriber.assertCompleted();
    testSubscriber.assertNoValues();
    testSubscriber.assertTerminalEvent();
}
```

#### 기존 Scheduler 대체하기

##### RxJava 1.1.6 이전
RxJavaPlugin 사용
http://fedepaol.github.io/blog/2015/09/13/testing-rxjava-observables-subscriptions/
```java
    @Before
    public void setup() {
            testScheduler = new TestScheduler();

        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getComputationScheduler() {
                return testScheduler;
            }

            @Override
            public Scheduler getIOScheduler() {
                return testScheduler;
            }

            @Override
            public Scheduler getNewThreadScheduler() {
                return testScheduler;
            }
        });
    }

    @After
    public void tearDown() {
        RxJavaPlugins.getInstance().reset();
    }}
```

##### RxJava 1.1.6 이후
RxJavaHooks 사용
http://syung1104.blog.me/221147813397
```java
@Before
public void setUp() throws Exception {
    RxJavaHooks.reset();
    RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());
    RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());

    // RxAndroid
    RxAndroidPlugins.getInstance().reset();
    RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
        @Override
        public Scheduler getMainThreadScheduler() {
            return Schedulers.immediate();
        }
    });
}

@After
public void tearDown() throws Exception {
    RxJavaHooks.reset();
    RxAndroidPlugins.getInstance().reset();
}
```

##### RxJava 2
https://medium.com/@peter.tackage/an-alternative-to-rxandroidplugins-and-rxjavaplugins-scheduler-injection-9831bbc3dfaf