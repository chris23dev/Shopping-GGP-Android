package com.ggp.theclub.manager;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.event.LocationChangeEvent;
import com.ggp.theclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import java8.util.stream.StreamSupport;

public class LocationServicesManager {
    private final String LOG_TAG = LocationServicesManager.class.getSimpleName();
    private final long minUpdateTime = 200;
    private final float minUpdateDistance = 0;

    private LocationManager locationManager;
    private List<LocationListener> locationListeners = new ArrayList<>();
    private static LocationServicesManager locationServicesManager = new LocationServicesManager();

    public static LocationServicesManager getInstance() {
        return locationServicesManager;
    }

    public LocationServicesManager() {
        locationManager = (LocationManager) MallApplication.getApp().getSystemService(Context.LOCATION_SERVICE);
    }

    public void updateLocation(Activity activity) {
        PermissionsManager.getInstance().checkLocationPermission(activity, granted -> {
            if (granted) {
                String provider = locationManager.getBestProvider(new Criteria(), true);
                if (!StringUtils.isEmpty(provider)) {
                    try {
                        locationListeners.add(singleLocationListener);
                        locationManager.requestSingleUpdate(provider, singleLocationListener, null);
                        Location location = locationManager.getLastKnownLocation(provider);
                        if (location != null) {
                            EventBus.getDefault().post(new LocationChangeEvent(location));
                        }
                    } catch (SecurityException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }
        });
    }

    public void startLocationTracking(Activity activity) {
        PermissionsManager.getInstance().checkLocationPermission(activity, granted -> {
            if (granted) {
                String provider = locationManager.getBestProvider(new Criteria(), true);
                if (!StringUtils.isEmpty(provider)) {
                    try {
                        locationListeners.add(continuousLocationListener);
                        locationManager.requestLocationUpdates(provider, minUpdateTime, minUpdateDistance, continuousLocationListener);
                        Location location = locationManager.getLastKnownLocation(provider);
                        if (location != null) {
                            EventBus.getDefault().post(new LocationChangeEvent(location));
                        }
                    } catch (SecurityException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }
        });
    }

    public void stopLocationTracking() {
        try {
            if(locationListeners != null) {
                StreamSupport.stream(locationListeners).forEach(listener -> locationManager.removeUpdates(listener));
            }
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private LocationListener singleLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            EventBus.getDefault().post(new LocationChangeEvent(location));
            stopLocationTracking();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private LocationListener continuousLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            EventBus.getDefault().post(new LocationChangeEvent(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };
    
    public boolean isDeviceLocationEnabled() {
        boolean gps_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled;
    }
}