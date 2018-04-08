GDG Korea DevFest 17
======

# 작성규칙
> 이것은 이 md파일의 작성자인 저의 의견을 나타냅니다.

# Android Architecture Components
###### 정현지 - GDG Korea Android Orgarnizer

##개요
이전에는 안드로이드에 공식적으로 가이드된(?) 아키텍처가 없어서 개발자가 MVC나 MVP나 MVVM 등의 구조를 직접 고민하고 작성했어야했음.

AAC가 등장했으니 걱정마셈

얼마전에 정식버전 업데이트됨 (Stable 1.0v)

## Android Life

cycle-aware compoenents codelab
[Google CodeLab Link](https://codelabs.developers.google.com/codelabs/android-lifecycles/index.html?index=..%2F..%2Fio2017#0)

* 일단, 쉽게 생각하면 **AppCompatActivity 가 LifecycleOwner라고 생각해도 됨**

* 실제로는 FragmentActivity가 fragment별 mLifecycleRegistry를 관리하고있음
* LifecycleRegistry가 LifecycleOwner를 WeakReference로 가지고 있음

* 옵저버 등록은 다음과 같이 한다.
```java
lifecycleOwner.getLifecycle().addObserver(this);
```


 * 자유도를 위해 `removeObserver()`를 퍼블릭으로 제공하지만, `AppcompatActivity`에서 Destory? Stop?(Stop때라면 유예기간 딜레이를 줄듯) 시에 알아서 Observer를 날려준다. **개편함**

* `remove`를 정확히 어디서 해주는지를 찾기위한 끝없는 여정을 시작해보았음...
`LifecycleRegistry`에서 `removeObserver`를 구현함
`ProcessLifecycleOwner`에서 `LifecycleRegistry`를 사용함
`ProcessLifecycleOwner` - activityStopped() -> dispatchStopIfNeeded() ->             mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
`LifecycleRegistry` handleLifecycleEvent ON_STOP일때 remove하는게 있겠다... 코드랩끝나고 찾아보기!!!

## ROOM Architecture
[Google CodeLab Link](https://codelabs.developers.google.com/codelabs/android-persistence/index.html?index=..%2F..%2Fio2017#0)
* 원시 SQL쿼리에 대한 컴파일 타임 검증.
* 개발자들이 실제로 SQL을 날려야함. -> 오히려 이게 장점일 수 있음. 개발자가 직접 쿼리를 날리지않고 래핑을 해서 사용하는 ORM 라이브러리들보다 더 효율적으로 사용가능하기 떄문 (Realm을 깠따!!)



# 1시간 안에 음성인식 인공지능 챗봇 개발하기
[Google Assistant](https://assistant.google.com/) 로 음성인식 붙임..그래서 영어다..
[DialogFlow](https://dialogflow.com/)
챗봇은 **어린아이**다!

아래의 세가지요소는 우리가 챗봇한테 알려줘야한다.
* 대화의 의도 (intent)
* 대화의 재료 (Entity)
* 대화의 분위기 (Context) - 눈치

=> 대화의 경험을 쌓고 응용 (Training)

* 사과라는 과일을 좋아한다
* 사과, 배, 복숭아 등 과일이름
* 과일이야기를 이어서하기

=> 아 과일 이야기를 하려는 거였구나~~

## 전처리
넘나 많은 유사어..... 멘붕쓰
[Google DataPrep](https://cloud.google.com/dataprep/)를 사용해봤다고 한다.
괜찮은듯?

## 대화의 의도(Intent) 등록
DialogFlow -> Intent 생성 -> 

## 대화의 분위기(Context)
특정 키워드들을 캐치하고 분위기를 파악한다?
DialogFlow Console에 키워드를 넣는데...흠........... 의도부분을 어떻게 넣는지 못봐서 헷갈림


> 네트워크 상태떄문에 세션진행에 문제가 생김 (디스플레이가 끊긴다, 준비한 영상이 재생되지 않는다) -> 다음 컨퍼런스부터는 로컬로 준비해오도록 하는게 좋지 않을지?

# UI Test 연동으로 배포 두려움 없애기
CI, Espresso, Dagger2, Mockito, Firebase Test Lab
김지혁 - Developer of Flitto
[GitHub - 샘플 저장소](http://github.com/wotomas/NewYorkTimesMVP)

아래 단위로 반복
> 기능개발 -> 유닛테스트 -> Mock UI Testing -> Prerformance Testing -> 배포

## Unit Test
* 악명높은 안드로이드 유닛테스트... : Context 분리. 스파게티 코드 제거 => 힘듦...
* Fast and Unfragile : 로직의 근간. Unit Test 코드는 절대 틀리지 않는다. 테스트가 깨지면 로직을 의심해야 함.

MVP로 짜면 유닛테스크하기 편함ㅎㅎㅎ 메소드 콜 벨리파이만 하면 되기 떄문임

### 시나리오
화면 노출 시 API 호출 이후 화면에 아래 내용 렌더링
- 에러시 토스트로 알림
- View의 역할은?
- Presenter의 역할은?

### Unit Test의 플로우
Arrange -> Act -> Assert
When -> Given -> Then

## CI
### Circle CI
공짜. 편함.
circle.yml 생성해서 설정해주면 됨. -> 근데 사실 이건 다른데선 단점이라고도 부름. CI설정파일을 repo에 올려야하니까...

## UI Testing
Espresso 공식 지원
무작정 UI 테스트 설계시, UI 테스트 수백개가 생긴다.
-> 네트워크 등의 문제로 인해 깨질 확률이 높다.

### Dagger2를 이용한 의존성 주입
Dagger2를 사용하여 UI테스트시 mock을 주입한다.
-> ? 근데 Dagger2공식 홈피에서 UnitTest할때는 쓰지말라고 하는데..
-> 아! UnitTest에서 쓰지말라는 얘기구나. UI Test할때는 괜찮은 거구나....

## Firebase Test Lab
로컬환경에서 남는 노트북으로 세팅 가능
안정적 테스팅 환경을 위해 Test Lab 이용
로그, 영상, CPU 밒 메모리 성능 확인 가능

![./gdgdevfest17/]()

## Debugging Tools
### Tiny Dancer
디버깅 툴로서, FPS Tracking Util이다. 
[GitHub](https://github.com/friendlyrobotnyc/TinyDancer)

### [그 외 디버깅 툴들](http://kimjihyok.info/tag/android-debugtools/)
> 나는 이전 프로젝트에서는 LeakCanary와 StrictMode를 사용했었다.

## Cold Start Time
KK이상에서만 사용가능함. UI테스트코드에서 분기태워라.

## QnA
소프트키
- 사실 UI Test를 깨트리는 가장 큰 요소
- 소프트키가 떠있는지 아닌지 체크하는 로직을 넣어야 한다.

웹뷰에 대한 테스팅은 어떻게...?
- 셀레니움 기반의 어떤 테스트 라이브러리에서 web 컨텐츠내의 id로 접근하여 ui테스트를 할 수 있다는 가이드를 본적이있다. 확실하지는 않다.

테스트 커버리지는 보통 어느정도?
- 95% 이상이 좋긴하지만...... 걍 팀단위로 합의를 해서 규정을 정하는게 낫다.

Dagger 관련 내용
- Dagger를 사용하지 않으면 Singletone 등의 상황에서 mocking처리가 애매해지는데, 이럴때는 사실 PowerMock을 사용하면 되긴하지만 비추천한다. Unit Test자체가 코드 품질을 개선시키는 용도인데 이걸 우회하는 용도인 PowerMock을 쓰는건... 가치가 충돌한다고 생각함.

그럼 Dagger를 꼭 사용해야 할까요? Dagger에 대한 의존성이 생기는 것 같아서 꺼려진다.
- 본인도 이에대한 의견이 있긴하지만 이 부분은 현재 발표주제와 맞지않으니 패스하겠다. 이메일로 답변주겠다고 하셨음.
> 이것에 대한 나의 의견 : Dagger에 대한 의존성이 두렵다면 결국 DI 역할을 하는 컨트롤러를 직접 짜서 사용해야 좋은 구조로 나아가게 될 것이다. 직접 구현할때 싱글톤 등을 대체하기 위해 인스턴스 라이프 사이클에 대한 컨트롤도 필요해질 것인데 이 부분을 정말 효율적으로 완벽하게 잘 짤수 있다면 직접짜는 것을 추천. 그 외 커스터마이징을 꼭 해야하는 이유가 있다면 이 경우에도 추천. 그 외의 경우엔..... 잘 만들어진 공신력(?)있는 DI 라이브러리인 Dagger2를 사용하는 것이 구조적으로나 성능적으로나 훨씬 좋을 것으로 예상됨. 이 부분에 대해서는 [DIP, DI에 대한 문서](https://trazy.gitbooks.io/oop/content/oop-dip.html)를 참고하면 참 좋을 듯 하다.


# 좋은 코드를 고민하는 주니어 개발자들을 위한 안드로이드 디자인패턴
###### 정원희 - 독서모임 트레바리 개발자
첫 질문이 안드로이드 개발 몇년차인가? 였음.
> 예전부터 궁금했던 부분은.... "경력은 언제부터 시작인 것인가!"이다. 학생때 경력도 포함해야 하는것인지... 입사 후의 경력만 경력인 것인지..
> 난 안드로이드를 2011년부터 개발해왔지만 입사후에 개발하게 된것은 2015년부터다. 그럼 난 안드로이드 몇년차인거지? 아하핳 모르겠따

## 좋은 코드가 아닐 때 겪는 문제점
* 기획 수정 시, 연달아 수정해야하는 코드가 너무 많아ㅠㅠ
* 코드 하나 고쳤는데 버그가 10000개 발생ㅎㅎㅎㅎㅎ
* 관련된 코드 찾기도 힘들어요ㅠㅠ
* 예전과 비슷한 기능을 추가하는데 또 수백줄을 짜야해요ㅠㅠ

=> 확장 / 유지보수가 힘듬 => 생산성 저하 => 야근.....

## 좋은 코드를 짜는 방법
* 우리와 같은 고민을 했을 예전 개발자들의 뇌를 빌린다. ~~갓GoF~~

## 디자인 패턴
일반적으로 발생하는 문제점들을 해결하기 위한 재사용 가능한 솔루션
=> 빠르게 문제를 해결할 수 있음
=> 생산성 증가
=> 칼퇴!

* 문제 상황을 빠르게 판단하고 해결
* 코드 재사용을 통해 개발속도 증가
* 개발자들끼리 공통의 해결방법(구조) 공유 : 다른 개발자들가 의사소통/코드파악에 유리

> 안드로이드 앱개발 맞춤형 디자인패턴 이야기를 할줄알았는데, 완전히 1~2년차 주니어 안드로이드 개발자들을 위한 발표인 것 같다...

## Android :-)
1. 데이터가 변경될 떄마다 여러 화면들을 업데이트 시키고 싶어요!
=> 리액티브 프로그래밍이 필요한 상황 => 옵저버 패턴을 사용한다. RxJava가 그러하지.
=> Command PAttern 을 사용해도 됨 => EventBus / Otto
2. 생성자에 넘겨야하는 파라미터가 넘나 많아여
=> 빌더 패턴을 사용한다.

