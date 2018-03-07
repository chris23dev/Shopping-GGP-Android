package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.model.Tenant;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TenantMapActivity extends MapActivity {
    private Tenant tenant;

    public static Intent buildIntent(Context context, Tenant tenant) {
        return buildIntent(context, TenantMapActivity.class, tenant);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tenant = getIntentExtra(Tenant.class);
        if (tenant == null) {
            tenant = getIntentExtra(MovieTheater.class);
        }
        setContentView(R.layout.tenant_map_activity);
        mapStatusView.setText(getString(R.string.map_loading));
    }

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.TenantDetailMap, tenant.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapManager.setSelection(tenant.getLeaseId());
    }

    @Override
    protected void configureView() {
        setTitle(R.string.tenant_map_title);
        enableBackButton();
        layoutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mapManager.frameDestination(tenant.getLeaseId());
            }
        });
    }
}