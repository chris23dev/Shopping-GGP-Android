package com.ggp.theclub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GGPMallsInstallListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MallApplication.getApp().getAttributionManager().onReceive(context, intent);
        MallApplication.getApp().getDeepLinkingManager().onReceive(context, intent);
    }
}
