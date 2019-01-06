package com.packtpub.apps.rxjava_essentials.chapter6;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import rx.android.schedulers.AndroidSchedulers;

public class CombineLatestExampleFragment extends Fragment {

  @BindView(R.id.fragment_first_example_list) RecyclerView mRecyclerView;

  @BindView(R.id.fragment_first_example_swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

  private ApplicationAdapter mAdapter;

  private ArrayList<AppInfo> mAddedApps = new ArrayList<>();

  public CombineLatestExampleFragment() {
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

    loadList(apps);
  }

  private void loadList(List<AppInfo> apps) {
    mRecyclerView.setVisibility(View.VISIBLE);

    Observable<AppInfo> appsSequence = Observable.interval(1000, TimeUnit.MILLISECONDS)
        .map(position -> apps.get(position.intValue()));

    Observable<Long> tictoc = Observable.interval(1500, TimeUnit.MILLISECONDS);

    Observable.combineLatest(appsSequence, tictoc, this::updateTitle)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<AppInfo>() {
          @Override public void onCompleted() {
            Toast.makeText(getActivity(), "Here is the list!", Toast.LENGTH_LONG).show();
          }

          @Override public void onError(Throwable e) {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
          }

          @Override public void onNext(AppInfo appInfo) {
            if (mSwipeRefreshLayout.isRefreshing()) {
              mSwipeRefreshLayout.setRefreshing(false);
            }
            mAddedApps.add(appInfo);
            int position = mAddedApps.size() - 1;
            mAdapter.addApplication(position, appInfo);
            mRecyclerView.smoothScrollToPosition(position);
          }
        });
  }

  private AppInfo updateTitle(AppInfo appInfo, Long time) {
    appInfo.setName(time + " " + appInfo.getName());
    return appInfo;
  }
}
