package com.ggp.theclub.manager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.ggp.theclub.MallApplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class PermissionsManager {
    private final String LOG_TAG = PermissionsManager.class.getSimpleName();

    private int requestCode = 0;
    private HashMap<Integer, PermissionsResultCallback> requestCodeToCallbackLookup = new HashMap<>();
    private static PermissionsManager permissionsManager = new PermissionsManager();

    public static PermissionsManager getInstance() {
        return permissionsManager;
    }

    public void checkLocationPermission(Activity activity, PermissionsResultCallback callback) {
        String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        checkPermissions(permissions, activity, callback);
    }

    public boolean hasLocationPermission() {
        return hasPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION});
    }

    private void checkPermissions(String[] permissions, Activity activity, PermissionsResultCallback callback) {
        List<String> ungrantedPermissions = StreamSupport.stream(Arrays.asList(permissions))
                .filter(permission -> ContextCompat.checkSelfPermission(MallApplication.getApp(), permission) != PackageManager.PERMISSION_GRANTED)
                .collect(Collectors.toList());

        if (ungrantedPermissions.size() > 0) {
            requestCode++;
            requestCodeToCallbackLookup.put(requestCode, callback);
            ActivityCompat.requestPermissions(activity, ungrantedPermissions.toArray(new String[ungrantedPermissions.size()]), requestCode);
        } else {
            callback.onPermissionsResult(true);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        List<String> ungrantedPermissions = StreamSupport.stream(Arrays.asList(permissions))
                .filter(permission -> ContextCompat.checkSelfPermission(MallApplication.getApp(), permission) != PackageManager.PERMISSION_GRANTED)
                .collect(Collectors.toList());

        return ungrantedPermissions.size() == 0;
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        boolean granted = true;
        PermissionsResultCallback callback = requestCodeToCallbackLookup.get(requestCode);
        if (callback != null) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                }
            }
            callback.onPermissionsResult(granted);
            requestCodeToCallbackLookup.remove(requestCode);
        }
    }

    public interface PermissionsResultCallback {
        void onPermissionsResult(boolean granted);
    }
}