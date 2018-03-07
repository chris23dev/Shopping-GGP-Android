package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParkingMapActivity extends MapActivity {
    public static Intent buildIntent(Context context) {
        return buildIntent(context, ParkingMapActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_map_activity);
        mapStatusView.setText(R.string.map_loading);
    }

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.ParkingMap);
    }

    @Override
    protected void configureView() {
        setTitle(R.string.parking_map_title);
        enableBackButton();
    }
}