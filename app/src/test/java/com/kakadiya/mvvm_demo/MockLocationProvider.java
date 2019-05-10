package com.kakadiya.mvvm_demo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.kakadiya.mvvm_demo.viewmodel.MainViewModel;

import org.junit.Test;
import org.mockito.Mock;

/**
 * Created by krish on 2019-05-10.
 */

public class MockLocationProvider {
        String providerName;
        Context ctx;

        @Mock
        LocationListener locationListener;

        @Mock
        MainViewModel mainViewModel;

        public MockLocationProvider(String name, Context ctx) {
            this.providerName = name;
            this.ctx = ctx;

            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);
            lm.addTestProvider(providerName, false, false, false, false, false,
                    true, true, 0, 5);
            lm.setTestProviderEnabled(providerName, true);
        }

        @Test
        public void getCurrentLocation() {
            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);
            assert lm != null;
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null){
                mainViewModel.loadData(-12.34, 23.45);
            }
        }

        public void shutdown() {
            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);
            lm.removeTestProvider(providerName);
        }
}
