package com.ggp.theclub.manager.impl;

import android.app.Activity;
import android.util.Log;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.CrashReportingManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsManagerImpl implements AnalyticsManager {
    private final String LOG_TAG = getClass().getSimpleName();
    MallRepository mallRepository;
    MallManager mallManager;

    private static String previousScreen;

    public AnalyticsManagerImpl(MallRepository mallRepository, MallManager mallManager) {
        this.mallRepository = mallRepository;
        this.mallManager = mallManager;

        Config.setContext(MallApplication.getApp());
        Config.setDebugLogging(true);

        try {
            String configFilename = MallApplication.getApp().getString(R.string.adobe_config_filename);
            InputStream configInput = MallApplication.getApp().getAssets().open(configFilename);
            Config.overrideConfigStream(configInput);
        } catch (IOException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    // Track Lifecycle
    @Override
    public void startTrackingLifecycleData(Activity activity) {
        Config.collectLifecycleData(activity);
    }

    @Override
    public void stopTrackingLifecycleData() {
        Config.pauseCollectingLifecycleData();
    }

    // Track Screens
    @Override
    public void trackScreen(String screen) {
        trackScreen(screen, null);
    }

    @Override
    public void trackScreen(String screen, String tenant) {
        HashMap<String, Object> contextData = new HashMap<>();

        safePut(ContextDataKeys.ScreenName, screen, contextData);
        safePut(ContextDataKeys.TenantName, tenant, contextData);
        safePut(ContextDataKeys.MallName, mallManager.getMall().getName(), contextData);
        safePut(ContextDataKeys.AuthStatus, MallApplication.getApp().getAccountManager().isLoggedIn() ? ContextDataValues.AuthStatusAuthenticated : ContextDataValues.AuthStatusUnauthenticated, contextData);
        safePut(ContextDataKeys.UserId, StringUtils.isEmpty(PreferencesManager.getInstance().getUserId()) ? ContextDataValues.GuestUser : PreferencesManager.getInstance().getUserId(), contextData);
        safePut(ContextDataKeys.PreviousScreenName, previousScreen, contextData);

        previousScreen = screen;

        forceLowercase(contextData);
        Analytics.trackState(screen, contextData);

        CrashReportingManager.trackInteraction(screen, tenant);
    }

    // Track Actions
    @Override
    public void trackAction(String action) {
        trackAction(action, null, null);
    }

    @Override
    public void trackAction(String action, HashMap<String, Object> contextData) {
        trackAction(action, contextData, null);
    }

    @Override
    public void trackAction(String action, HashMap<String, Object> contextData, String tenant) {
        if (contextData == null) {
            contextData = new HashMap<>();
        }

        safePut(ContextDataKeys.AuthStatus, MallApplication.getApp().getAccountManager().isLoggedIn() ? ContextDataValues.AuthStatusAuthenticated : ContextDataValues.AuthStatusUnauthenticated, contextData);
        safePut(ContextDataKeys.UserId, StringUtils.isEmpty(PreferencesManager.getInstance().getUserId()) ? ContextDataValues.GuestUser : PreferencesManager.getInstance().getUserId(), contextData);
        safePut(ContextDataKeys.ScreenName, previousScreen, contextData);
        safePut(ContextDataKeys.TenantName, tenant, contextData);
        safePut(ContextDataKeys.MallName, mallManager.getMall().getName(), contextData);

        forceLowercase(contextData);
        Analytics.trackAction(action, contextData);
    }

    // Build Context Data
    public void safePut(String key, String value, HashMap<String, Object> contextData) {
        if (value != null) {
            contextData.put(key, value);
        }
    }

    private void forceLowercase(HashMap<String, Object> contextData) {
        for (Map.Entry<String, Object> entry : contextData.entrySet()) {
            if (entry.getValue() != null) {
                entry.setValue(entry.getValue().toString().toLowerCase());
            }
        }
    }
}