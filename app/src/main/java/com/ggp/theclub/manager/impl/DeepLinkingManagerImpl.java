package com.ggp.theclub.manager.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.DeepLinkingManager;

import org.json.JSONException;

import io.branch.referral.Branch;
import io.branch.referral.InstallListener;

public class DeepLinkingManagerImpl implements DeepLinkingManager {
    public final String LOG_TAG = getClass().getSimpleName();

    public DeepLinkingManagerImpl() {
        Branch.getAutoInstance(MallApplication.getApp());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        new InstallListener().onReceive(context, intent);
    }

    @Override
    public void startSession(Activity activity) {
        Branch.getInstance().initSession((referringParams, error) -> {
            if (error == null) {
                Log.d(LOG_TAG, referringParams.toString());
            } else {
                Log.d(LOG_TAG, error.getMessage());
            }
        }, activity.getIntent().getData(), activity);
    }

    public boolean isDeepLinkLaunch(Activity activity) {
        return Branch.isAutoDeepLinkLaunch(activity);
    }

    public Integer getDeepLinkMallId() {
        try {
            return Branch.getInstance().getLatestReferringParams().getInt(MallApplication.getApp().getString(R.string.mall_deeplink_param));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }
}