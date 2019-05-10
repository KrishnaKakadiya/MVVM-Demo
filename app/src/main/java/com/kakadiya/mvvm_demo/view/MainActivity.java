package com.kakadiya.mvvm_demo.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.kakadiya.mvvm_demo.R;
import com.kakadiya.mvvm_demo.databinding.ActivityMainBinding;
import com.kakadiya.mvvm_demo.viewmodel.MainViewModel;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setupObserver(mainViewModel);
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        assert lm != null;
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        if(location!=null){
            mainViewModel.loadData(location.getLongitude(),location.getLatitude());
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if(location!=null){
                mainViewModel.loadData(location.getLongitude(),location.getLatitude());
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(),"No location found",Toast.LENGTH_LONG);
        }
    };

    private void initDataBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new MainViewModel(getApplicationContext());
        binding.setMainViewModel(mainViewModel);
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof MainViewModel){
            MainViewModel mainViewModel = (MainViewModel) observable;
        }
    }

    private void setupObserver(Observable observable){
        observable.addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainViewModel.reset();
    }
}
