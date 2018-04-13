import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class TroubleShooting {


    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.trampoline()); // 즉시 실행
        RxJavaHooks.onComputationScheduler(Schedulers.trampoline());
        RxJavaHooks.onNewThreadScheduler(Schedulers.trampoline());
    }

    @Test
    public void create의_오류처리() throws Exception {
//        TestSubscriber<String> subscriber = new TestSubscriber<>();
//
//        Observable.create(emitter -> {
//            String str = null;
//            str.length();
//            emitter.onNext(str);
//        }).subscribe(subscriber);
//
//        subscriber.assertError(Exception.class);
    }

    @Test
    public void 업스트림에서_오류가_발생할때() throws Exception {
        Observable.just("test", null)
                .map(String::length)
                .subscribe(str -> log("onNext=" + str),
                        err -> {
                            log("====onError====");
                            err.printStackTrace();
                        });
    }

    @Test
    public void onErrorReturn으로_오류를_대체값으로_대체() throws Exception {
        Observable.just("test", null)
                .map(String::length)
                .onErrorReturn(err -> 123)
                .subscribe(str -> log("[onNext] " + str),
                        err -> log("[onError] " + err));
    }

    @Test
    public void onErrorResumeNext으로_오류를_보조스트림으로_대체() throws Exception {
        Observable.just("test", null)
                .map(String::length)
                .onErrorResumeNext(err -> Observable.just(123, 321))
                .subscribe(str -> log("[onNext] " + str),
                        err -> log("[onError] " + err));
    }

    @Test
    public void flatMap으로_onError_onCompleted시_스트림_대체() throws Exception {
        Observable.just(1, 2, 3)
//        Observable.just("test", null)
//                .map(String::length)
                .flatMap(Observable::just,
                        err -> Observable.empty(),                  // 오류발생시 빈 Observable로 대체 (오류무시)
                        () -> Observable.just(4, 5, 6)) // 완료시 다른스트림으로 대체
                .subscribe(str -> log("[onNext] " + str),
                        err -> log("[onError] " + err),
                        () -> log("[onCompleted]"));
    }


    public void log(String logString) {
        System.out.println(logString);
    }
}

