03_RxJava의 연산자들에 대하여
=======
※ 개인적으로 이해한 내용을 적은 것이므로 잘못되었거나 부족한 내용이 있을 수 있습니다.

> 사실 RxJava 1.x를 처음 익힐 때 작성해둔 연산자 포스팅이 있다. 그 포스팅이 있으니 일단 여기서는 코드를 생략하도록 하겠음...
[[Java] RxJava - Observable 변환](http://syung1104.blog.me/220960831140?Redirect=Log&from=postView)

# RxJava의 연산자들
RxJava가 강력한 이유중의 하나! 그것은 바로 RxJava가 수많은 내장 연산자들을 제공하고 사용자 정의 연산자를 추가할 수 있게 되어있다는 것이다.

그럼 어떤 연산자들이 있는지 알아보도록 하자!_! 슝슝

## 변환 연산자
`Observable` 스트림을 변환(변경)하는 연산자이다.

### filter
![](http://reactivex.io/documentation/operators/images/filter.png)

### map
![](http://reactivex.io/documentation/operators/images/map.png)

### flatMap
![](http://reactivex.io/documentation/operators/images/mergeMap.png)

업스트림의 데이터 내부의 특정 필드(혹은 자기 자신)를 flat하게 펼쳐서 또 다른 Observable을 만드는 오퍼레이션 :-)
쉽게 생각하면 Stream이 될 수 있는 Collection을 또 다른 Observable로 만드는 역할을 한다고 생각하면 된다.

#### flatMap - maxConcurrent 설정하기
`flatMap`은 병렬 처리를 하기 때문에 순서보장이 되지 않는다. 병렬 처리로 이루어지기 떄문에 연산 속도가 빠른편이다. 하지만 사실 병렬처리도 병렬처리 오버헤드가 연산 오버헤드보다 더 커지는 특정 시점을 지나면 전체적인 성능이 감소될 수 있다.

이러한 부분을 컨트롤 하기 위해 `flatMap`은 동시성 개수를 설정할 수 있도록 되어있다. 디폴트 값은 Interger 최대 값이다.

```java
// flatMap 구현부 - maxConcurrency 설정이 가능하다.
public final <R> Observable<R> flatMap(Function< ? super T, ? extends ObservableSource< ? extends R > > mapper, int maxConcurrency) {
	return flatMap(mapper, false, maxConcurrency, bufferSize());
}
```

`concatMap`은 `faltMap(func, 1)`과 유사하다고 볼 수 있겠다.
> 사실은 미묘하게 다르다고 한다. 자세히는 책에서도 안알랴쥼.... 나중에 찾아보기로 하자.



### concatMap
![](http://reactivex.io/documentation/operators/images/concatMap.png)

flatMap은 순서가 보장되지 않는다. concatMap은 데이터를 싱글스레드에서 처리하기 때문에 순서가 보장된다.

### concatMapEager
[flatMap vs concatMap vs concatMapEager](http://www.nurkiewicz.com/2017/08/flatmap-vs-concatmap-vs-concatmapeager.html)

`concatMapEager`는 순서보장 + 동시성 보장까지 이루어진다. 싱글스레드로 처리되는 `concatMap`과는 달리, 멀티스레드에서 처리되다보니 부가 연산이 많아져 비용이 큰 편이다. 동시성 체크가 필요하지 않은 상황이라면 굳이 `concatMapEager`를 사용할 필요는 없어보인다.

### delay
![](http://reactivex.io/documentation/operators/images/delay.png)

이벤트를 미루는 연산자. 이벤트를 모두 받은 다음 모든 이벤트의 시간축을 옮긴다.

#### `timer`, `interval`과의 차이점
`timer`, `interval`은 Observable을 생성하는 Creator이고 `delay`는 Operator이다. 아예 Observable이 발행하는 것 자체를 지연시키는 것이 `timer`, Observable이 발행한 이벤트를 수신하는 것을 지연하는게 `delay` 이다.

책에서는 delay를 timer + flatMap로 바꿀 수 있다고 나오는데 바꿀 수는 있으나 엄밀히 따지면 **동일**하게 동작하는 것은 아니다.

```java
// Observable이 2초 뒤에 생성된다.
Observable.timer(2, TimeUnit.SECONDS)

// Observable의 발행값을 2초 미뤄서 받는다.
Observable.just(1, 2, 3)
		  .delay(2, TimeUnit.SECONDS)

// subscribe 이후, Observable 자체가 2초 뒤에 생성되고 flatmap이 콘솔에 찍힌다.
System.out.println("시작 : " + currentTime);
Observable.timer(2, TimeUnit.SECONDS)
.flatMap(item -> {
		System.out.println("flatmap : "  + currentTime);
    	return Observable.just(1, 2, 3);
    })
    .delay(5, TimeUnit.SECONDS)
	.subscribe(integer -> System.out.println(integer + " : " + currentTime));

/*** console ***/
// 시작 : 18:47:18
// flatmap : 18:47:20
// 1 : 18:47:25
// 2 : 18:47:25
// 3 : 18:47:25
```

## 합성 연산자
RxJava는 여러개의 `Observable`을 함께 사용할 수 있는 연산자를 제공한다. 전통적인 Java 프로그래밍 시에는 이 경우 복잡한 보일러플레이트 코드들이 추가되었겠지만 RxJava에서는 제공되는 연산자로 깔끔한 처리가 가능하다.

### [merge](http://reactivex.io/documentation/operators/merge.html)

![](http://reactivex.io/documentation/operators/images/merge.png)

Observable 여러개를 병합하는 연산자이다. **모든 Observable의 이벤트를 모두 모아 하나의 스트림에 담는 것**이다.

어느 상황에 `merge`를 사용할 수 있을지 생각해보자 :
* 비슷한 성향(이벤트)을 띄우는 여러개의 옵저버블을 하나의 스트림으로 묶기위해
* 그냥 각각의 결과값에 대한 후처리 없이 그저 옵저버블들을 묶고 싶을때

병합한 스트림들 중 하나의 스트림에서 에러가 발생하면 다운스트림으로 에러를 내려주고 스트림을 종료한다. 위의 그림에서 에러 발생이후 보라색 데이터를 내리지 않는 것이 바로 이 동작이다.

에러 발생시, 모든 스트림이 종료될 때 까지 미루고 싶다면 `mergeDelayError` 연산자를 사용하자. 이 연산자는 종료시까지 발생한 모든 에러를 모아 `CompositeException`으로 반환한다.

![](http://reactivex.io/documentation/operators/images/mergeDelayError.C.png)

### [zip](http://reactivex.io/documentation/operators/zip.html)
개인적으로 `zip`은 아래의 구버전 다이어그램보다 신버전이 더 이해하기 쉬운 듯 하다.

![](http://reactivex.io/documentation/operators/images/zip.png)

Observable 여러개를 병합하고 짝을 지어 결과 값을 방출한다. 그렇기 때문에 **모든 업스트림이 데이터를 방출해야 짝을 짓고 다운스트림으로 내려줄 수 있다.** 이러한 점을 활용하여 **모든 조건이 만족되었을 때에만 특정 행동을 해야하는 경우**에 `zip`연산을 사용할 수 있다.

또한, **서로 관련이 있는 여러 스트림을 하나로 묶거나 각 데이터를 묶어서 의미있는 데이터로 만들때** 유용하게 사용할 수 있다.

```java
// 여기서는 그냥 모든 Observable의 이벤트가 발행되었는지의 여부만 리턴하도록 설정했지만,
// 각 이벤트를 조합하여 리턴하면 더 다양하게 사용될 수 있을 것이다.
integerObservable.zipWith(integerObservable2, (integer, integer2) -> {
        System.out.println(integer + ", " + integer2);
    	return true;
	}).subscribe(System.out::println);
```

여러개의 API의 응답을 모두 받고나서 특정 동작이 수행되어야 한다면, `zip`을 사용할수도있지만 `merge`도 사용할 수 있다. 오히려 이 경우엔 API를 `Observable`대신 `Single`로 처리하고 `merge`로 처리하는 것이 더 어울려보인다.

### [combineLatest](http://reactivex.io/documentation/operators/combinelatest.html)
![](http://reactivex.io/documentation/operators/images/combineLatest.png)

`zip`은 합성하려는 모든 스트림에서 각 이벤트가 방출되어야만 합성이 이루어진다. 특정 스트림에서 방출이 지연되면 합성자체가 지연되어 메모리 누수가 생길 수 있다. 그러므로 `zip`을 사용할때에는 주의하도록 하자.
**스트림에서 이벤트가 방출되는 순간, 나머지 스트림의 가장 최근 이벤트와 합성**이 이루어지도록 하고싶을때에는 `combineLatest`를 이용하자.

### [withLatestFrom](http://reactivex.io/documentation/operators/combinelatest.html)

![](http://reactivex.io/documentation/operators/images/withLatestFrom.png)

첫번째 스트림의 이벤트가 발생할 때에만 두번째 스트림의 이벤트와 합성 한다. 네이밍이 `with~`인 것으로 알 수 있듯이, 두개의 스트림을 합성하는 연산자이다.

두번째 스트림에서 발생한 이벤트가 없으면 첫번재 스트림에서 이벤트가 발생해도 무시된다. 이게 싫다면 `startWith()`으로 첫번째 값을 설정하도록 하자.

```java
Observable<String> fast = Observable.interval(10, TimeUnit.MILLISECONDS)
		.map(x -> "F" + x)
        .delay(100, TimeUnit.MILLISECONDS)
        .startWith("FAST START VALUE");

Observable<String> slow = Observable.interval(17, TimeUnit.MILLISECONDS)
        .map(x -> "S" + x);
slow.withLatestFrom(fast, (s, f) -> s + ":" + f)
	.forEach(System.out::println);

// === output ===
// S0:FAST START VALUE
// S1:FAST START VALUE
// S2:FAST START VALUE
// S3:FAST START VALUE
// S4:FAST START VALUE
// S5:FAST START VALUE
// S6:F0
// S7:F2
// S8:F4
// S9:F6
```

### [amb](http://reactivex.io/documentation/operators/amb.html)

![](http://reactivex.io/documentation/operators/images/amb.png)

`amb`로 엮인 모든 스트림 중에서 **가장 먼저 이벤트를 방출한 스트림의 이벤트만을 다운스트림으로 내려준다.**

> `ambWith()`을 사용하는 것 보다는 `amb()`를 사용하는 것이 가독성에 더 좋다. 두 스트림이 동등한 관계라는 점을 명확하게 알려줄 수 있기 때문이다 :-)


## 고수준 연산자

### [scan](http://reactivex.io/documentation/operators/scan.html), [reduce](http://reactivex.io/documentation/operators/reduce.html)

누산기 :-)

![](http://reactivex.io/documentation/operators/images/scan.png)

![](http://reactivex.io/documentation/operators/images/reduceSeed.png)


### collect

![](http://reactivex.io/documentation/operators/images/collect.png)

전체 이벤트를 모아서 하나의 리스트를 반환하는 `Observable`을 만들고 싶은 경우가 있다. 은근히 잦다.

일단은 `reduce`를 이용해서 만들어보자.
```java
Observable<List<Integer>> observable = Observable.range(10, 20)
	.reduce(new ArrayList<>(), (list, item) -> {
    	list.add(item);
        return list;
    });
```
위와 같이 만들 수 는 있지만 뭔가 꺼림칙하다. 굳이 return을 해줘야하고 부자연스럽다.

그럴때 `collect`를 이용하면 편하다.
```java
Observable<List<Integer>> observable2 = Observable.range(10, 20)
	.collect(ArrayList::new, List::add);
```

> 사실 collect가 누산기 원리를 이용해서 만들어진 것이라는 건 이번에 처음 알았다.. 그냥 결과를 컬렉션 형태로 묶어서 바로 반환해주는 정도로만 알았는데... 올!

#### [toList](http://reactivex.io/documentation/operators/to.html)
`collect` 연산자로 `List` 외에도 여러가지 컬렉션으로의 변환이 가능하다. 그 중에서도 `List` 로의 변환은 굉장히 자주 쓰이기 때문에 아예 `toList()`연산자로 따로 나와있다.

![](http://reactivex.io/documentation/operators/images/toList.png)

```java
Observable<List<Integer>> observable3 = Observable.range(10, 20).toList();
```

### [single](http://reactivex.io/documentation/operators/first.html)
정확히 딱 하나의 값만 방출하는 Observable인 경우, `single()`를 사용하면 값을 가져올 수 있다.
만약 하나 이상의 값이 방출되면 Exception이 발생한다.
그게 싫다면 `singleOrDefault()`를 사용하면 된다.

![](http://reactivex.io/documentation/operators/images/single.png)

비슷한 부류(?)로, `first`가 있다.

### [distinct](http://reactivex.io/documentation/operators/distinct.html), distinctUntilChanged

![](http://reactivex.io/documentation/operators/images/distinct.png)

중복 값을 제거해준다. 각 값들은 `equals`와 `hashcode`를 기반으로 비교된다.

중복 값을 제거하기 위해서는 **이전에 방출한 데이터를 모두 캐싱**하고 있어야 한다. 그러므로 데이터가 늘어날 수록 메모리 점유율도 함께 높아질 것이다.

방출 직전 값과 다른 경우만 방출하고 싶은 경우에는 `distinctUntilChanged` 를 사용하자. 이 연산자는 변화가 생겼을 경우에만 이벤트를 방출하기 때문에 바로 직전 값만 메모리에 저장하면 되기때문에 효율적이다.

![](http://reactivex.io/documentation/operators/images/distinctUntilChanged.png)

날씨 이벤트와 같이 변화에 집중해야 하는 경우에 유용하게 쓸 수 있겠다.

Changed 여부를 특정 특성에 대해서만 체크하고 싶다면 아래와 같이 해당 특성을 넘겨주면 된다.

![](http://reactivex.io/documentation/operators/images/distinctUntilChanged.key.png)

### 이벤트 건너뛰기

Observable이 방출하는 데이터의 일부만 가져오고 싶은 경우 사용하는 연산자들이 있다.

#### [take](http://reactivex.io/documentation/operators/take.html)
처음 n개의 값만을 다운스트림으로 방출하고 종료한다.

![](http://reactivex.io/documentation/operators/images/take.png)

#### [skip](http://reactivex.io/documentation/operators/skip.html)
처음 n개의 값을 스킵하고 그 다음 값부터 방출한다.

![](http://reactivex.io/documentation/operators/images/skip.png)

> `take`, `skip` 연산자 모두 인덱스 예외처리가 되어있어 편하게 사용할 수 있다. 음수 값이 들어오면 0으로 처리되고, size를 초과해도 최대 size로 알아서 처리된다. 굿!

#### [takeLast](http://reactivex.io/documentation/operators/takelast.html)

마지막 n개의 값만을 다운스트림으로 방출하고 종료한다.
마지막 값임을 알기위해서는 종료시점을 알아야하므로, Observable이 종료되어야지만 다운스트림으로 이벤트를 방출할 수 있다.

![](http://reactivex.io/documentation/operators/images/takeLast.n.png)

#### [skipLast](http://reactivex.io/documentation/operators/skiplast.html)

처음부터 값을 방출하다가 마지막 n개의 값을 스킵하고 Observable을 종료한다. 마지막 n개의 값을 스킵해야 하므로 n개 이상의 값이 나타났을 때에 비로소 다운스트림으로 첫번째 값을 방출할 수 있게 된다.

![](http://reactivex.io/documentation/operators/images/skipLast.png)

#### first()
`take(1).single()` 와 동일하다. `single()`을 사용했으므로 값이 1개 초과이거나 없을 경우 `NoSuchElementException`이 발생한다.

`first(predicate)`로 특정 조건을 만족하는 첫번째 값을 가져올 수도 있다.

#### last()
`takeLast(1).single()` 와 동일하다. `single()`을 사용했으므로 값이 1개 초과이거나 없을 경우 `NoSuchElementException`이 발생한다.

`last(predicate)`로 특정 조건을 만족하는 마지막 값을 가져올 수도 있다.

#### [takeFirst(predicate)]()
특정 조건을 만족하는 첫번째 값을 취한다. `filter(predicate).take(1)` 와 동일하다. `fisrt(predicate)`와 달리, 일치하는 값이 없어도 `NoSuchElementException` 이 발생하지 않는다.

![](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/takeFirstN.png)

#### [takeUntil(predicate)](http://reactivex.io/documentation/operators/takeuntil.html)

특정 조건이 만족될 때 까지 값을 방출한다. 조건을 만족한 값이 방출되면 스트림을 종료한다.

![](http://reactivex.io/documentation/operators/images/takeUntil.p.png)

#### [takeWhile(predicate)](http://reactivex.io/documentation/operators/takewhile.html)

특정 조건을 만족하는 한 계속해서 값을 방출한다.

![](http://reactivex.io/documentation/operators/images/takeWhile.png)

#### elementAt(n)
특정 위치의 항목을 뽑아내고 싶은 경우 사용한다. `IndexOutOfBoundsException`이 방출될 수 있다.

#### count
방출된 이벤트의 개수를 센다.

```java
Observable<Integer> observable = Observable.just(1, 2, 3);
observable.count()
	.subscribe(System.out::println); // 3

// reduce 로 count 구현하기
observable.reduce(0, (size, item) -> size + 1);
```

#### [all(predicate)](http://reactivex.io/documentation/operators/all.html), exist(predicate), [contains(value)](http://reactivex.io/documentation/operators/contains.html)

![](http://reactivex.io/documentation/operators/images/all.png)

![](http://reactivex.io/documentation/operators/images/exists.png)

![](http://reactivex.io/documentation/operators/images/contains.png)

### 스트림 결합하기
#### [concat](http://reactivex.io/documentation/operators/concat.html)

여러개의 `Observable`을 하나의 스트림으로 이어붙인다.

![](http://reactivex.io/documentation/operators/images/concat.png)

##### 주의할 점
* Observable이 순차적으로 이어진다.
: **첫번째 Observable이 끝나야 두번째 Observable 을 구독할 수 있게 된다.** 큐가 하나이기 때문인데, 이는 flatMap의 순차버전이 왜 concatMap인지 알 수 있게 한다! :-)

 아래는 Cold Observable들을 하나로 묶을 때의 `concat`연산 이다.
![](./images/concat.png)

* **구독**을 순차적으로 하는 것이다.
: 구독을 순차적으로 옮겨가는 것 이므로 Hot Observable들을 이을때는 위의 다이어그램의 결과가 달라진다.

 위의 그림에서 첫번째 빨간색 2는 받아지지 않고 두번쨰 빨간색 2만 해당 타이밍에 받아질 것이다.


##### 사용처
그럼 `concat`은 언제 사용하는게 좋을까?

**캐시된 값을 확인해보고 캐싱되어있지 않은 경우만 값을 로드하는 경우** 적합하게 사용할 수 있다.

```java
Observable<String> getAccessToken = loadAccessToken();
Observable<String> requestAccessTokenToServer = requestAccessToken();

Observable.concat(getAccessToken, requestAccessTokenToServer).first();
```

첫번째 Stream 이 아무런 값을 방출하지 않고 종료되는 경우 두번째 Stream을 차선책으로 사용하여 값을 받아오는 코드이다.
첫번째 Stream이 종료되기 전까지는 두번째 Stream이 실행되지 않으므로 위와 같은 흐름에서 사용하면 매우 적합한 사용이 될 듯 하다!!!

##### merge, switchOnNext 와의 차이점
`merge`는 이벤트가 오는 대로 방출

`concat`은 하나의 스트림이 끝나야 다음 스트림의 이벤트를 방출하므로 순차적

`switchOnNext`는 여러개의 flat한 Observable을 스위칭 해가며 하나만 구독한다. 말로 설명하는 것 보다는 그림이 더 나을 듯.

![](http://reactivex.io/documentation/operators/images/switchDo.png)

### [groupBy](http://reactivex.io/documentation/operators/groupby.html)

특정 기준으로 스트림을 병렬적으로 나누는 연산자.

![](http://reactivex.io/documentation/operators/images/groupBy.c.png)

## 사용자 정의 연산자

### compose
여러개의 연산자를 모아 재구성하는 연산자.

```java
private <T> Observable.Transformer<T, T> setSchedulers() {
	return observable -> observable.subscribeOn(Schedulers.io)
    							 .observeOn(AndroidSchedulers.mainThread());
}

Observable.just(1, 2, 3)
		.compose(setSchedulers())
        .subscribe(System.out::println);
```

* Transformer 메서드는 적용되는 Observable을 subscribe하는 시점에 Lazy 하게 실행되는 것이 아니다. Observable이 생성되는 순간 바로 실행된다. (Eager Execution)

>책에 나와있는 예제보다 아래 링크의 예시 사례가 더 와닿는다.
실제로 저렇게 구현해야하나 고민했던 적이 많은데 `compose`를 이용하면 좀 더 깔끔하게 해결될 수 있겠다.
http://blog.danlew.net/2015/03/02/dont-break-the-chain/

### lift
완전히 새로운 연산자를 만들어 낼 수 있는 연산자.


## 뭐지뭐지 멘붕쓰 더 알아보기
### [join](http://reactivex.io/documentation/operators/join.html)
[참고링크](https://github.com/Froussios/Intro-To-RxJava/blob/master/Part%204%20-%20Concurrency/3.%20Sequences%20of%20coincidence.md)

Default Scheduler : ComputationScheduler ㅎㄸㄸ
