package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;

public class TermsActivity extends WebViewActivity {
    private final String TERMS_URL = "http://assets.digitalservices.ggp.com/content/ggp/malls/global-mall-pages/en/terms-of-use.basic.html";

    public static Intent buildIntent(Context context) {
        return buildIntent(context, TermsActivity.class);
    }

    @Override
    protected String getUrl() {
        return TERMS_URL;
    }

    @Override
    protected String getAnalyticsScreenName() {
        return AnalyticsManager.Screens.TermsConditions;
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.terms_title);
    }
}