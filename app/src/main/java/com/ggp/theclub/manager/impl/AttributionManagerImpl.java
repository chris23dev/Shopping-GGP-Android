package com.ggp.theclub.manager.impl;

import android.content.Context;
import android.content.Intent;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AttributionManager;
import com.kochava.android.tracker.Feature;
import com.kochava.android.tracker.ReferralCapture;

import java.util.HashMap;

public class AttributionManagerImpl implements AttributionManager {

    private Feature kochavaTracker;

    public AttributionManagerImpl() {
        start();
    }

    private void start() {
        String appId = MallApplication.getApp().getString(R.string.kochava_key);

        HashMap<String, Object> params = new HashMap<String, Object>(){{
            put(Feature.INPUTITEMS.KOCHAVA_APP_ID,appId);
        }};

        kochavaTracker = new Feature(MallApplication.getApp(), params);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        new ReferralCapture().onReceive(context, intent);
    }
}
