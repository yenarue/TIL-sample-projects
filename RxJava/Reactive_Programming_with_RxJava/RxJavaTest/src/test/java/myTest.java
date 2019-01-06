import com.yenarue.rxjava.Speech;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import twitter4j.*;

import java.util.concurrent.TimeUnit;

public class myTest {

    Observable<String> yena;
    Observable<String> seungha;
    Observable<String> pinga;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.trampoline());
        RxJavaHooks.onComputationScheduler(Schedulers.trampoline());
        RxJavaHooks.onNewThreadScheduler(Schedulers.trampoline());

        yena = Speech.speak("my name is yena", 100).map(word -> "yena : " + word);
        seungha = Speech.speak("Hello, I'm SeungHa", 300).map(word -> "seungha : " + word);
        pinga = Speech.speak("Hi, Here is Pinga", 200).map(word -> "pinga : " + word);
    }

    @Test
    public void test_merge() throws Exception {
        Observable.merge(yena, seungha, pinga)
                .toBlocking()   // setUp에서 어떻게 해줘야 이걸 없앨 수 있을까...ㅠㅠ
                .subscribe(System.out::println);
    }

    @Test
    public void test_concat() throws Exception {
        Observable.concat(yena, seungha, pinga)
                .toBlocking()
                .subscribe(System.out::println);
    }

    @Test
    public void test_switchOnNext() throws Exception {
        Observable<Observable<String>> peopleSpeech = Observable.just(yena, seungha, pinga);

        Observable.switchOnNext(peopleSpeech)
                .toBlocking()
                .subscribe(System.out::println);
    }
}
