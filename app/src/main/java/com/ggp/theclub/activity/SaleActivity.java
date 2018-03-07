package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.FeedbackManager;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.StringUtils;

import butterknife.BindString;

public class SaleActivity extends PromotionActivity {
    @BindString(R.string.share_subject_format) String shareSubjectFormat;
    @BindString(R.string.sale_share_format) String saleShareFormat;
    private FeedbackManager feedbackManager = MallApplication.getApp().getFeedbackManager();
    private Sale sale;

    public static Intent buildIntent(Context context, Sale sale) {
        return buildIntent(context, SaleActivity.class, sale);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sale = getIntentExtra(Sale.class);
        setPromotion(sale);
        feedbackManager.incrementFeedbackEventCount(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (tenant != null) {
            analyticsManager.trackScreen(AnalyticsManager.Screens.SalesDetail, tenant.getName());
        }
    }

    @Override
    protected void configureView() {
        super.configureView();
        setTitle(getString(R.string.sale_detail_title));
        if(tenant == null || StringUtils.isEmpty(tenant.getLogoUrl()) && StringUtils.isEmpty(tenant.getName())) {
            logoView.setVisibility(View.GONE);
        } else {
            ImageUtils.setLogo(logoImageView, logoTextView, tenant.getLogoUrl(), tenant.getName());
            logoView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setupLocation() {
        locationView.setText(tenant == null ? "" : tenant.getName());
        locationView.setTextColor(blue);
    }

    @Override
    public void onActionButtonClick() {
        IntentUtils.share(this, sale, mallManager, analyticsManager);
    }
}