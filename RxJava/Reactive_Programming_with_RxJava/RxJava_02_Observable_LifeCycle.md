02_RxJava Observable의 LifeCycle과 Subscription 이벤트
=======

* 참고 링크 : https://brunch.co.kr/@lonnie/17


RxJava 학습 초반에, Observable의 Stream LifeCycle과 Subscription 이벤트에 따른 LifeCycle 이 은근히 혼용되고 헷갈리는 경향이 생겼었다. 그리고 지금도 그랬다. ~~젠장~~

위 링크를 정독하고 앞으론 제대로 사용하도록하자ㅠㅠ

## onErrorResumeNext(Observable newObservable)
업스트림에서 발생하는 오류를 낚아채 새로운 Observable로 대체한다.
특정 데이터 로딩실패시 대체 데이터를 로딩하고 싶을 때 사용하면 편하다.
```java
recommandBooksObs()
	.onErrorResumeNext(bestSellerObs)
    .subscribe(book -> book.getTitle());
```