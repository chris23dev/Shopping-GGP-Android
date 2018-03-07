package com.ggp.theclub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ggp.theclub.event.ConnectivityChangeEvent;

import de.greenrobot.event.EventBus;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new ConnectivityChangeEvent());
    }
}