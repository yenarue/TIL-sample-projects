package com.yenarue.rxjava;

import com.yenarue.rxjava.structure.Pair;
import rx.Observable;

import java.util.concurrent.TimeUnit;


public class Speech {

    public static Observable<String> speak(String sentences, final long millsPerChar) {
        String[] tokens = sentences.replaceAll("[:,]", "").split(" ");

        Observable<String> words = Observable.from(tokens);
        Observable<Long> delays = words.map(String::length)
                .map(length -> length * millsPerChar)
                .scan((totalDelay, currentDelay) -> totalDelay + currentDelay);

        return words.zipWith(delays.startWith(0L), Pair::of)
                .flatMap(pair -> Observable.just(pair.first)
                        .delay(pair.second, TimeUnit.MILLISECONDS));
    }
}
