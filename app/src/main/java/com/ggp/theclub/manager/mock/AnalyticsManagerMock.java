package com.ggp.theclub.manager.mock;

import android.app.Activity;

import com.ggp.theclub.manager.AnalyticsManager;

import java.util.HashMap;

public class AnalyticsManagerMock implements AnalyticsManager {
    @Override
    public void startTrackingLifecycleData(Activity activity) {}

    @Override
    public void stopTrackingLifecycleData() {}

    @Override
    public void trackScreen(String screen) {}

    @Override
    public void trackScreen(String screen, String tenant) {}

    @Override
    public void trackAction(String action) {}

    @Override
    public void trackAction(String action, HashMap<String, Object> contextData) {}

    @Override
    public void trackAction(String action, HashMap<String, Object> contextData, String tenant) {}

    @Override
    public void safePut(String key, String value, HashMap<String, Object> contextData) {
        if (value != null) {
            contextData.put(key, value);
        }
    }
}