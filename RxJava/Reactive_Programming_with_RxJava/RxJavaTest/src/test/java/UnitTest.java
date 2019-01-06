import org.junit.Before;
import org.junit.Test;
import rx.Notification;
import rx.Observable;
import rx.exceptions.CompositeException;
import rx.observables.SyncOnSubscribe;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.plugins.RxJavaObservableExecutionHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UnitTest {
    private final TestScheduler testScheduler = new TestScheduler();

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(testScheduler);
        RxJavaHooks.onComputationScheduler(testScheduler);
        RxJavaHooks.onNewThreadScheduler(testScheduler);
    }

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

    public Observable<Integer> concatMapDelayErrorObservable() {
        return Observable.just(3, 0, 1, 0)
//                .map(x -> 100 / x)
                .concatMapDelayError(x -> Observable.fromCallable(() -> 100 / x));
    }

    @Test
    public void concatMapDelayError_subscribe() throws Exception {
        concatMapDelayErrorObservable()
                .subscribe(str -> log("[onNext] " + str),
                        err -> log("[onError] " + err),
                        () -> log("[onCompleted]"));
    }

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

    @Test
    public void concatMapDelayError_testSubscriber버전() throws Exception {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        concatMapDelayErrorObservable().subscribe(testSubscriber);

        testSubscriber.assertValues(33, 100);
//        testSubscriber.assertError(ArithmeticException.class); // Fail
        testSubscriber.assertError(CompositeException.class);
//        assertThat(((CompositeException) testSubscriber.getOnErrorEvents().get(0))).isInstanceOf(.class);
        testSubscriber.assertTerminalEvent();
    }


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

    @Test
    public void 제한시간이_설정된_임계값보다_커지지_않도록하기() throws Exception {
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = Schedulers.test();

        Observable.timer(2000, TimeUnit.MILLISECONDS, testScheduler)
                .map(x -> LocalDate.now())
                .subscribe(testSubscriber);

        testScheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        testSubscriber.assertNotCompleted();
        testScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertTerminalEvent();
    }

    /// BackPressure
    Observable<Long> createWithoutBackpressure() {
        return Observable.create(subscriber -> {
            long i = 0;
            while (!subscriber.isUnsubscribed()) {
                subscriber.onNext(i++);
            }
        });
    }

    Observable<Long> createWithBackpressure() {
        return Observable.create(
                SyncOnSubscribe.createStateful(
                        () -> 0L,
                        (cur, observer) -> {
                            observer.onNext(cur);
                            return cur + 1;
                        }
                ));
    }

    @Test
    public void 배압처리_테스트() throws Exception {
        TestSubscriber<Long> ts = new TestSubscriber<>(0);

//        createWithoutBackpressure()
        createWithBackpressure()
                .doOnRequest(x -> log(x.toString()))
                .take(10)
                .subscribe(ts);

        ts.assertNoValues();
        ts.requestMore(100);
        ts.assertValueCount(10);
        ts.assertCompleted();
    }

    public void log(String logString) {
        System.out.println(logString);
    }
}
