package com.kakadiya.mvvm_demo;

import android.app.Application;
import android.content.Context;

import com.kakadiya.mvvm_demo.data.WeatherFactory;
import com.kakadiya.mvvm_demo.data.WeatherService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by krish on 2019-05-09.
 */

public class MVVMApplication extends Application {

    private WeatherService weatherService;
    private Scheduler scheduler;

    private static MVVMApplication get(Context context) {
        return (MVVMApplication) context.getApplicationContext();
    }

    public static MVVMApplication create(Context context) {
        return MVVMApplication.get(context);
    }

    public WeatherService getWeatherService() {
        if (weatherService == null) {
            weatherService = WeatherFactory.create();
        }
        return weatherService;
    }
    public Scheduler subscribeScheduler() {
        if (scheduler == null) {
            scheduler = Schedulers.io();
        }

        return scheduler;
    }

    public void setWeatherService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
