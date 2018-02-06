import com.yenarue.rxjava.Speech;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

public class Exception extends Throwable {

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.trampoline());
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

}
