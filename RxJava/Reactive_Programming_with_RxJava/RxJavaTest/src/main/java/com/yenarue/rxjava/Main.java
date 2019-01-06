package com.yenarue.rxjava;

import rx.Observable;   // 1
//import io.reactivex.Observable;

public class Main {
    public static void main(String[] args) {
        // 자유롭게 테스트 하기
        System.out.println("111");
//        Observable.error(new Exception("error!!!"))
//                .doOnError(e -> System.out.println("error"))
//                .doOnTerminate(() -> System.out.println("terminated"))
//                .doOnCompleted(() -> System.out.println("completed"))
//                .doOnUnsubscribe(() -> System.out.println("unsubscribe"))
//                .toBlocking().first();
        int a = Observable.just(1, 2, 3).toBlocking().first();
        System.out.println("222 " + a);


        setValue(1);
        Observable<Integer> observable = Observable.defer(() -> Observable.just(getValue()));
//        observable.toBlocking().subscribe(System.out::println);

        setValue(2);
//        observable.toBlocking().subscribe(System.out::println);
    }

    static int value;
    public static void setValue(int value1) {
        value = value1;
    }

    public static int getValue() {
        return value;
    }
}
