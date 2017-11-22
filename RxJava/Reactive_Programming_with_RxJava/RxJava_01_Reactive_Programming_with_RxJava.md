01_RxJava를 활용한 리액티브 프로그래밍
==========

※ 개인적으로 이해한 내용을 적은 것이므로 잘못되었거나 부족한 내용이 있을 수 있습니다.

# RxJava는 어떻게 동작하는걸까?
* Reactive 방식을 지향하지만 Interactive 방식으로도 사용 가능하다.
* Lazy Excution : 즉시 동작하지 않고 게으르게(지연되어) 실행된다.
* 비동기/동기 방식 모두 사용 가능하다. (선택형)

## 밀어내기(Reactive, Push)
RxJava가 **리액티브**이기 위한 핵심 개념. `Observable`, `Observer`를 통해 구현된다.

* `Observable` : RxJava의 핵심. Observable 이라는 네이밍에서도 알 수 있듯이 **Observing 가능한 무언가**을 뜻하는 것으로, 좀 더 구체적으로는 **데이터/이벤트 스트림**을 뜻한다.

* `Observer` : **Observing 가능한 무언가를 Observing 하는 것**으로서, `Observable`을 구독하는 관찰자이다.

```java
interface Observable<T> {
	// Observable은 구독(subscribe)될 수 있다 :-)
	Subscription subscribe(Observer observer);
}

interface Observer<T> {
	// 0 ~ N번 호출 가능하다.
	void onNext(T t);
    // 종료 이벤트들. 둘 중 하나만 단 한 번 호출된다.
    void onError(Throwable t);
    void onCompleted();
}
```

## 끌어오기(Interactive, Pull)
대화형 끌어오기 (Interactive Pull)도 지원한다.
```java
interface Producer {
	void request(long n);
}

interface Subscriber<T> implements Observer<T>, Subscription {
	// 0 ~ N번 호출 가능하다.
	void onNext(T t);
    // 종료 이벤트들. 둘 중 하나만 단 한 번 호출된다.
    void onError(Throwable t);
    void onCompleted();
	...
    // Observable 스트림의 구독을 끊는다.
    void unsubscribe();
    // 흐름제어시 양방향 커뮤니케이션 채널을 구성할 때 사용한다.
    void setProducer(Producer p);
}
```

그런데 2.x대를 받아서 확인해보니 `Producer`가 삭제되었다. 대신 `request()`가 `Subscription`로 이동했다. 또한, `unsubscribe()`가 `cancel()`로 리네이밍 되었다.

1.6버전 기준으로 작성된 책이라 2.x의 사상이랑 조금 다를 수 있다는 점을 다시한번 리마인드 하도록 해야겠다.

## 비동기/동기와 블로킹/논블로킹
> 업로드하려고 보니 갑자기 이 부분에서 혼란이 왔다. 블로킹/논블로킹부분은 이해가 되는데 동기/비동기가 잘 이해되지 않는다. 일단 업로드하고 다음에 다시 제대로 알아보기

> TODO : 알아보기 - p.7의 HelloWorld 예제는 스레드를 블로킹을 하는데 왜 블로킹하지 않는다고 쓰여져있는걸까..........

RxJava가 비동기 로직에서 강력하다는 점 때문에 `Observable`이 비동기적으로만 동작 할 것만 같은 느낌을 받을 수 있다. 하지만 `Observable`은 사실 기본적으로는 동기적으로 동작한다. (`subscribeOn()`의 Default 값이 동기방식)

`Observable` 이벤트 생성의 중요한 기준은 **블로킹/논블로킹** 여부이다. 동기/비동기 여부가 아니다! 동기/비동기는 블로킹/논블로킹을 위한 수단일 뿐이다. [#동기/비동기_vs_블로킹/논블로킹에_관한_토론](https://www.slipp.net/questions/367)

### Observable의 설계
`Observable`은 의도적으로 아래와 같이 설계되었다.
* 동기/비동기를 구분하지 않는다.
* 동시성 여부를 따지지 않는다.
* 어디서부터 비롯되었는지 출처에 대해서도 따지지 않는다.

대신, 무엇이 최선일지는 Observable 사용자가 결정하도록 하였다.

```java
Observable.just("test1", "test2", "test3")
	.subscribeOn(Scheduler.io())	// 이렇게(?)
    .subscribe(str -> System.out.println(str));
```

동시성이란 언제나 발생할 수 있다. 만약 동기/비동기 방식을 구분하여 설계되었다면, 이미 비동기처리 되고있는 로직에 비동기방식으로 설계된 Observable을 사용하여 비동기성이 하나 더 더해지고 그에 따른 오버헤드가 생기게 되었을 것이다. ~~끔찍~~

이러한 군더더기들을 배제하기 위해 담백하게 설계하고, 사용자가 현재 상태에서의 최선의 설정을 선택하도록 설계한 것이다.

>**RxJava는 비동기성의 출처에 관심이 없다!**

그럼 이제 `Observable`이 동기 방식을 Default로 사용하는 두가지 이유를 살펴보자 : 캐싱 데이터, 동기적 방식의 연산자

### 메모리 내부(캐싱) 데이터
RxJava를 당장 적용하고 싶다는 마음은 이해되지만, 메모리에 캐싱되어있는 데이터를 굳이 비동기로 처리하는 것은 좋은 생각이 아니다. (물론 연습용 코드로는 짜도 된다.)
굳이 이미 메모리에 올라가있는 데이터에 접근하기 위해 스케줄링 비용을 추가로 낼 필요도 없고 괜히 오버헤드만 생기기 때문이다.

```java
int cache = 0;
Observable.create(emitter -> {
	// 동기적으로 발행
	emitter.onNext(cache);
    emitter.onComplete();
}).subscribe(System.out::println);
```

아래와 같이 데이터가 캐싱되어있을 때에는 동기적으로 보내고, 그 외에는 데이터를 요청하는 로직을 비동기적으로 넣을 수 있을 것이다.
```java
int cache = 0;
Observable.create(emitter -> {
	if (cache != null) {
    	// 동기적으로 발행
		emitter.onNext(cache);
    	emitter.onComplete();
    } else {
    	// 비동기로 데이터를 가져오는 requestData 호출
        requestData().subscribe(res -> {
        	emitter.onNext(cache)
            emitter.onComplete();
        }, e -> {
        	emitter.onError(e);
        });
    }
}).subscribe(System.out::println);
```

뭔가 굉장히 장황하고 힘들게 설명했지만, 결론은 걍 `Observable`은 동기/비동기와는 관련없다는 것이다. `Observable` 구현 시 사용자 맘대로 동기/비동기를 섞을 수 있다 :-)

### 동기적인 방식으로 계산하는 연산자들
위에서 `Observable`에서 데이터 발행시, 동기/비동기 방식 모두 사용 가능하다는 것을 확인하였다.

`Observable`의 연산(`map()`, `filter()` 등...)은 데이터를 발행하는 스레드를 따른다. (Upstream을 따른다) 즉, 연산자는 데이터를 발행하는 스레드에 동기적으로 실행된다. 연산에 대해서는 사실 당연한 부분인데, 연산이 데이터를 발행하는 스레드에 동기적으로 이루어지지 않으면 데이터 연산 자체가 꼬일 것이다..

```java
Observable.<Integer>create(s -> {
	// 비동기적인 로직을 호출!
    new Thread(() -> {
    	System.out.println("start=" + Thread.currentThread());	// 2
        s.onNext(42);
        System.out.println("end"); // 5
        }, "MyThread").start();
}).doOnNext(i -> System.out.println(Thread.currentThread())) // 3
  .filter(i -> i % 2 == 0)
  .map(i -> "Value " + i + " processed on " + Thread.currentThread())
  .subscribe(s -> System.out.println("SOME VALUE =>" + s)); // 4
System.out.println("Will print BEFORE values are emitted because Observable is async"); // 1
```
위 예시코드에서, `filter`와 `map`연산은 `onNext()`다음에 동기적으로 실행된다.
콘솔화면에 표시되는 내용은 다음과 같다. 출력 순서에 유의해서 보도록 하자.
```bash
Will print BEFORE values are emitted because Observable is async
start=Thread[MyThread,5,main]
Thread[MyThread,5,main]
SOME VALUE =>Value 42 processed on Thread[MyThread,5,main]
end=Thread[MyThread,5,main]
```

## 동시성과 병렬성