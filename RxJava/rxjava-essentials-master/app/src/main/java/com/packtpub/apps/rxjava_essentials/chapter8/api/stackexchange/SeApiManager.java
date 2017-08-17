package com.packtpub.apps.rxjava_essentials.chapter8.api.stackexchange;

import com.packtpub.apps.rxjava_essentials.chapter8.api.stackexchange.models.User;
import com.packtpub.apps.rxjava_essentials.chapter8.api.stackexchange.models.UsersResponse;
import com.packtpub.apps.rxjava_essentials.network.RetrofitCreator;

import java.util.List;
import lombok.experimental.Accessors;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Accessors(prefix = "m") public class SeApiManager {

  private final StackExchangeService mStackExchangeService;

  public SeApiManager() {
    Retrofit retrofit = RetrofitCreator.create("https://api.stackexchange.com");
    mStackExchangeService = retrofit.create(StackExchangeService.class);
  }

  public Observable<List<User>> getTenMostPopularSOusers() {
    return mStackExchangeService.getTenMostPopularSOusers()
        .map(UsersResponse::getUsers)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public Observable<List<User>> getMostPopularSOusers(int howmany) {
    return mStackExchangeService.getMostPopularSOusers(howmany)
        .map(UsersResponse::getUsers)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
