package com.ggp.theclub.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public interface DeepLinkingManager {
    void onReceive(Context context, Intent intent);
    void startSession(Activity activity);
    boolean isDeepLinkLaunch(Activity activity);
    Integer getDeepLinkMallId();
}