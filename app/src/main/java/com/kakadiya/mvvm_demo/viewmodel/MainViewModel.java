package com.kakadiya.mvvm_demo.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.kakadiya.mvvm_demo.MVVMApplication;
import com.kakadiya.mvvm_demo.R;
import com.kakadiya.mvvm_demo.data.WeatherFactory;
import com.kakadiya.mvvm_demo.data.WeatherResponse;
import com.kakadiya.mvvm_demo.data.WeatherService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by krish on 2019-05-09.
 */

public class MainViewModel extends Observable {
    public ObservableInt progressbar;
    public ObservableInt mainLayout;
    public ObservableField<String> temperature;
    public ObservableField<String> maxTemperature;
    public ObservableField<String> minTemperature;
    public ObservableField<String> dateTime;
    public ObservableField<String> description;
    public ObservableField<Drawable> image;

    private WeatherResponse weatherResponse;
    private BindableFieldTarget bindableFieldTarget;

    String appId = "b6907d289e10d714a6e88b30761fae22";
    private Context context;


    private CompositeDisposable compositeDisposable  = new CompositeDisposable();

    public MainViewModel(@NonNull Context context){
        this.context = context;
        progressbar = new ObservableInt(View.GONE);
        mainLayout = new ObservableInt(View.VISIBLE);
        temperature = new ObservableField<>("Temp");
        maxTemperature = new ObservableField<>("Day Temp");
        minTemperature = new ObservableField<>("Night Temp");
        description = new ObservableField<>("Description");
        dateTime = new ObservableField<>("January 7, 14:55");
        image = new ObservableField<>();
        bindableFieldTarget = new BindableFieldTarget(image,context.getResources());
        //("http://openweathermap.org/img/w/04n.png")
    }

    public String getDateFormatted(){
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public WeatherResponse getWeatherResponse() {
        return weatherResponse;
    }

    public void setWeatherResponse(WeatherResponse weatherResponse) {
        this.weatherResponse = weatherResponse;
        temperature.set(String.valueOf(convertKelvinToCelcius(weatherResponse.getMain().getTemp())));
        maxTemperature.set("Day "+String.valueOf(convertKelvinToCelcius(weatherResponse.getMain().getTempMax())));
        minTemperature.set("Night "+String.valueOf(convertKelvinToCelcius(weatherResponse.getMain().getTempMin())));
        description.set(String.valueOf(weatherResponse.getWeather().get(0).getDescription()));
        String imageURL = "http://openweathermap.org/img/w/"+weatherResponse.getWeather().get(0).getIcon()+".png";
        if(bindableFieldTarget!=null) {
            Picasso.get()
                    .load(imageURL)
                    .placeholder(R.mipmap.weather_icon)
                    .into(bindableFieldTarget);
        }
    }
    private int convertKelvinToCelcius(double kelvin) {
        return (int) (kelvin - 273.15);
    }


    public void loadData(double longitude,double latitude){
        initializeViews();
        fetchWeatherData(longitude, latitude);
    }
    private void initializeViews(){
        progressbar.set(View.VISIBLE);
        mainLayout.set(View.GONE);
        dateTime.set(getDateFormatted());
    }
    private void fetchWeatherData(double longitude,double latitude){

        MVVMApplication mMVVMApplication = MVVMApplication.create(context);
        WeatherService weatherService = mMVVMApplication.getWeatherService();

        Disposable disposable = weatherService.fetchWeather(latitude,longitude,appId)
                .subscribeOn(mMVVMApplication.subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResponse>() {
                    @Override
                    public void accept(WeatherResponse weatherResponse) throws Exception {
                        Log.e("getHumidity", String.valueOf(weatherResponse.getMain().getHumidity()));
                        progressbar.set(View.GONE);
                        mainLayout.set(View.VISIBLE);
                        setWeatherResponse(weatherResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override public void accept(Throwable throwable) throws Exception {
                        progressbar.set(View.GONE);
                        mainLayout.set(View.VISIBLE);
                        description.set(context.getString(R.string.error_loading_data));
                    }
                });
        compositeDisposable.add(disposable);
    }

    public class BindableFieldTarget implements Target {

        private ObservableField<Drawable> observableField;
        private Resources resources;

        public BindableFieldTarget(ObservableField<Drawable> observableField, Resources resources) {
            this.observableField = observableField;
            this.resources = resources;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            observableField.set(new BitmapDrawable(resources, bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            observableField.set(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            observableField.set(placeHolderDrawable);
        }
    }

    public void reset(){
        unsubscribeFromDisposable();
        compositeDisposable = null;
        context =null;

    }

    private void unsubscribeFromDisposable(){
        if (compositeDisposable !=null && !compositeDisposable.isDisposed()){
            compositeDisposable.dispose();
        }
    }
}
