package com.packtpub.apps.rxjava_essentials.chapter8.api.openweathermap;

import com.packtpub.apps.rxjava_essentials.chapter8.api.openweathermap.models.WeatherResponse;
import com.packtpub.apps.rxjava_essentials.network.RetrofitCreator;

import lombok.Getter;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OpenWeatherMapApiManager {

  @Getter private static OpenWeatherMapApiManager instance = new OpenWeatherMapApiManager();

  private final OpenWeatherMapService mOpenWeatherMapService;

  private OpenWeatherMapApiManager() {
    Retrofit retrofit = RetrofitCreator.create("http://api.openweathermap.org");
    mOpenWeatherMapService = retrofit.create(OpenWeatherMapService.class);
  }

  public Observable<WeatherResponse> getForecastByCity(String city) {
    return mOpenWeatherMapService.getForecastByCity(city)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
