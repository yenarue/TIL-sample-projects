package com.packtpub.apps.rxjava_essentials.example3;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.packtpub.apps.rxjava_essentials.R;
import com.packtpub.apps.rxjava_essentials.apps.AppInfo;
import com.packtpub.apps.rxjava_essentials.apps.ApplicationAdapter;
import com.packtpub.apps.rxjava_essentials.apps.ApplicationsList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class ThirdExampleFragment extends Fragment {

    @BindView(R.id.fragment_first_example_list) RecyclerView mRecyclerView;

    @BindView(R.id.fragment_first_example_swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

    private ApplicationAdapter mAdapter;

    private ArrayList<AppInfo> mAddedApps = new ArrayList<>();

    private Subscription mTimeSubscription;

    public ThirdExampleFragment() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAdapter = new ApplicationAdapter(new ArrayList<>(), R.layout.applications_list_item);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));

        // Progress
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setVisibility(View.GONE);

        List<AppInfo> apps = ApplicationsList.getInstance().getList();

        AppInfo appOne = apps.get(0);

        AppInfo appTwo = apps.get(1);

        AppInfo appThree = apps.get(2);

        loadApps(appOne, appTwo, appThree);
    }

    private void loadApps(AppInfo appOne, AppInfo appTwo, AppInfo appThree) {
        mRecyclerView.setVisibility(View.VISIBLE);

        Observable.just(appOne, appTwo, appThree)
//                .repeat(3) // 세번 반복해서 발행
                .subscribe(new Observer<AppInfo>() {
                    @Override public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Here is the list!", Toast.LENGTH_LONG).show();
                    }

                    @Override public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override public void onNext(AppInfo appInfo) {
                        mAddedApps.add(appInfo);
                        mAdapter.addApplication(mAddedApps.size() - 1, appInfo);
                    }
                });

        Observable.just(appOne, appTwo, appThree)
//                .repeat(3) // 세번 반복해서 발행
                .subscribe(appInfo -> {
                            mAddedApps.add(appInfo);
                            mAdapter.addApplication(mAddedApps.size() - 1, appInfo);
                        },
                        e -> {
                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        },
                        () -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), "Here is the list!", Toast.LENGTH_LONG).show();
                        }
                );

        // 일정 시간 후에 발행하는 옵저버블! // use interval(long, long, TimeUnit, Scheduler) instead
        mTimeSubscription = Observable.timer(3, 3, TimeUnit.SECONDS)
                .subscribe(number -> Log.d("RXJAVA", "I say " + number));

        // 1.x 버전대에 추가된 것으로 보였으나 1.3.0에서 삭제되고 create로 대체됨
//        Observable.fromEmitter
//        Observable.create(emitter -> {
//            emitter.onNext("test");
//            emitter.onNext("test2");
//            emitter.onCompleted();
//        }, Emitter.BackpressureMode.BUFFER)


//        // range(X, N) : X ~ X + N - 1
//        Observable.range(10, 3)
//                .subscribe(integer -> Toast.makeText(getActivity(), "I say " + integer, Toast.LENGTH_LONG).show());

//        // polling 루틴
//        Observable.interval(3, TimeUnit.SECONDS)
//                .subscribe(longNumber -> Toast.makeText(getActivity(), "I say " + longNumber, Toast.LENGTH_LONG).show());
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (!mTimeSubscription.isUnsubscribed()) {
            mTimeSubscription.unsubscribe();
        }
    }
}
