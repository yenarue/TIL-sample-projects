02_`Observable`에 대하여 - 2
=======
※ 개인적으로 이해한 내용을 적은 것이므로 잘못되었거나 부족한 내용이 있을 수 있습니다.

# Observable의 특성

## 무한스트림
RxJava는 데이터를 발행하는 즉시 소비할 수 있기 때문에 무한 스트림이라는 개념이 가능하다. 특정 시점에 모든 값을 메모리에 보존할 필요가 없기 때문에 무한 큐가 가능한 것이다!

## 구독자들에게 동시 발행하면 안된다
Observable 내부에서 별도의 스레드를 돌리면 안된다! "구독자들은 동시에 이벤트 알림을 받으면 안된다"는 Rx 규약이 깨질 수 있기 때문이다. 한번에 하나의 이벤트가 각 구독자한테 순차적으로 전달되어야 하는데 내부적으로 스레드가 돌아가면 여러명이 동시에 이벤트를 받을 수 있게 된다. 불가피하게 스레드를 사용해야 하는 경우라면 꼭 `serialize()` 연산을 시키도록 하자.

## Hot Observable vs Cold Observable
### Cold Observable
Cold는 Lazy와 유사하다. 누군가가 구독하기 전까지는 데이터를 발행하지 않는다. 또한 기본적으로 캐시처리되지 않기때문에 각 구독자별로 별도의 복사본 스트림을 가지게 된다. Observable은 디폴트로 Cold하게 생성된다.

### Hot Observable
Hot은 구독자의 유무와 상관없이 데이터를 바로 발행한다. 때문에 데이터 유실이 발생할 수 있다. 이벤트를 브로드캐스트 할 때 유용하다. 마우스 동작과 같이 계속해서 발행하지만 계속해서 구독할 필요가 없는 이벤트들은 Hot하게 발행하면 유용하다.
> 그런데 Hot Observable은 어떻게 구현하는 걸까? 넘나 궁금.

# 특별한 `Observable`들
이전에 알아보았던 `Single`, `Completable`, `Observable` 외에도 좀 더 특별한 형태의 Observable 클래스들이 존재한다 :-D

`Subject`와 `ConnectableObservable`에 대해 알아보자.

## [Subject](http://reactivex.io/documentation/ko/subject.html)
`Subejct` 클래스는 `Observable`을 상속하면서 `Observer`도 구현하고 있다.
즉 `Obervable`이자 `Observer`라는 것인데, 이것은 즉, 무엇을 어떻게 발행할지도 정의할 수 있고 구독자의 행동도 정의 할 수 있다는 말이 된다.

`Subject`는 내부적으로 `Subscriber`의 생명 주기를 관리한다. 그러므로 현재 구독자가 있는지 없는지 체크할 필요 없이 그냥 발행을 진행하면 된다. Hot Observable 구현시 유용할 듯 하다.

`Subject`의 종류에는 `PublishSubject`, `AsyncSubject`, `BehaviorSubject`, `ReplaySubject`가 있다. [#](http://reactivex.io/documentation/ko/subject.html)

### [PublishSubject]()

![PublishSubject](http://reactivex.io/documentation/operators/images/S.PublishSubject.png)

가장 일반적인(?) Subject로서 구독자들은 구독을 시작한 시점 이후부터 발행된 이벤트들만 수신할 수 있다.

### [AsyncSubject](http://reactivex.io/RxJava/javadoc/rx/subjects/AsyncSubject.html)

![AsyncSubject](http://reactivex.io/documentation/operators/images/S.AsyncSubject.png)

`onComplete()`를 호출시 마지막 데이터를 발행한다. 그 전까지는 아무런 값도 발행하지 않는다.

![AsyncSubject-onError](http://reactivex.io/documentation/operators/images/S.AsyncSubject.e.png)

`onError()`가 호출되면 (에러가 발생하면) 아무런 값도 발행하지 않는다.

### [BehaviorSubject](http://reactivex.io/RxJava/javadoc/rx/subjects/BehaviorSubject.html)

![BehaviorSubject](http://reactivex.io/documentation/operators/images/S.BehaviorSubject.png)

구독 시작시 가장 최근에 발행된 데이터가 먼저 발행된다. 뭔가 말로쓰니 이상하지만... 그림으로 보면 이해하기 쉬을 것이다.
구독자가 추가 될 때 마다 초기값이나 시작값을 발행해야 할 때 사용하면 편할 듯 하다.
(예 : 몇 분 간격으로 온도를 알려주는 경우, 구독자가 구독 시작시 몇분을 기다렸다가 온도값을 받는게 아니라 바로 이전 값을 받는다! :-D)

![BehaviorSubject-onError](http://reactivex.io/documentation/operators/images/S.BehaviorSubject.e.png)

에러 발생 후 구독하면 발생했던 그 에러를 발행한다.

### [ReplaySubject](http://reactivex.io/RxJava/javadoc/io/reactivex/subjects/ReplaySubject.html)
![ReplaySubject](http://reactivex.io/documentation/operators/images/S.ReplaySubject.png)

새로운 구독자가 구독을 시작할 때, 이전에 발행되었던 모든 이벤트를 **재발행**한다. 엄밀히 말하자면 재발행 한다기보다는 캐싱해뒀다가 새로운 구독자한테 그 캐싱된 데이터를 전달해준다. 그 이후부터는 실시간으로 데이터를 받는다.

모든 데이터를 캐싱하기 때문에 **메모리 관리**에 신경을 써야한다. 특히 무한 스트림인 경우에는 Out Of Memory가 발생할 수 있으니 조심하자.

메모리 문제를 피하기 위해 아래와 같이 제약사항을 포함하여 생성하는 메서드도 제공한다.

```java
// 메모리에 캐싱할 수 있는 이벤트 개수 설정
ReplaySubject.createWithSize()
// 캐싱을 유지할 시간 설정
ReplaySubject.createWithTime()
// 둘 다 설정
ReplaySubject.createWithTimeAndSize()
```

### 주의사항
`Subject`는 조심스럽게 다뤄야 한다.
* 캐싱된 데이터 사용 시, `ConnectableObservable`를 사용하는 것이 보다 더 자연스러운 방법이라고 한다. (왜죠? 왠지는 안알랴쥼?)
* 동시성이 깨질 수 있다. `onNext`는 언제나 순서대로 호출되어야 하는데 (앞서 언급한 Rx 규약) Subject 메서드 호출을 여러 쓰레드에서 하게되면 충분히 겹치는 상황이 발생할 수 있다. 다행히 `Subject`도 `toSerialized()`라는 직렬화 메서드를 제공하니 꼭 사용하도록 하자. 이벤트가 다운스트림으로 순서대로 내려가게 해준다.
~~이럴거면 걍 Serialized를 디폴트로 부르게 하면 안됬던거냐!!!~~

## [ConnectableObservable](https://github.com/ReactiveX/RxJava/wiki/Connectable-Observable-Operators)

**여러 구독자가 하나의 구독을 공유**하는 형태의 Observable이다.

내부적으로는 최대 하나의 `Subscriber`만 유지하도록 되어있지만 실질적으로는 여러 `Subscriber`가 같은 기반의 리소스를 공유한다. -> 번역 오류인가? 뭔말인지는 알겠는데 이 문장 자체만 읽어보면 좀 이상해;;;

쉽게 생각하면 구독 채널을 하나만 열어두고 구독자들은 반드시 그 채널에만 달라붙어야(Connect) 해야 하는 구조인 듯 하다.
아래 그림을 보면 이 추측이 맞다는 게 확실해진다 :

![](https://github.com/ReactiveX/RxJava/wiki/images/rx-operators/publishConnect.png)

### `Observable`의 `publish().refCount()`와 `share()`
`ConnectableObservable`을 사용하지 않고 일반 `Observable` 로도 구독을 하나만 유지할 수 있는 방법이있다.
`publish().refCount()`와 같이 메서드를 조합해서 사용하면 일반 `Observable`도 하나의 구독만을 유지할 수 있게 된다.
구독자가 늘어나도 동일한 업스트림을 공유하게되며. 구독자가 0개가 되면 즉시 해지하는 상황으로 인지하여 알아서 해지시킨다.

```java
Observable.just(1, 2,3).publish().refCount();
```

`publish().refCount()`가 너무 자주쓰여서 `share()`라는 Alias가 추가되었다.

### 주의사항
이는 동일한 구독을 바라보는 **share**의 개념이지 캐싱하는 개념이 아니다. 처음에 잘 모르고 사용할때는 나도 캐시와의 차이가 뭔가 하고 꽤나 자주 헷갈렸었다. `cache()` 처리를 해야하는 상황에서 `share()`를 자주 사용하곤 했었는데 이렇게 차근차근 공부해보니 완전히 다른 개념이라는 것을 알게되었다. 정말 다행이다....

### ConnectableObservable 의 생명주기
`publish()`? `connect()`?? 으아 모르겠어이거.. 복습인데도 모르겠다니...엉엉.. 담주에 다시 읽어보고 이 항목 수정하자...

> ConnectableObservable 쓸까하다가 cache()로 처리했었던 적이 있는데 지금보니 cache()로 처리한게 더 적합한 선택이었지만 그때 ConnectableObservable 썼음 큰일날뻔했다 1도 모르고 사용할뻔했네...