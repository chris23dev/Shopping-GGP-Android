package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ggp.theclub.R;
import com.ggp.theclub.util.StringUtils;

import butterknife.Bind;

public class WebViewActivity extends BaseActivity {
    @Bind(R.id.web_view) WebView webView;

    private static final String URL = "URL";
    private static final String SCREEN_TITLE = "SCREEN_TITLE";
    private static final String ANALYTICS_SCREEN_NAME = "ANALYTICS_SCREEN_NAME";

    private String url;
    private String screenTitle;
    private String analyticsScreenName;

    public static Intent buildIntent(Context context, String url, String screenName, String screenTitle) {
        Intent intent = buildIntent(context, WebViewActivity.class);
        intent.putExtra(URL, gson.toJson(url));
        intent.putExtra(SCREEN_TITLE, gson.toJson(screenTitle));
        intent.putExtra(ANALYTICS_SCREEN_NAME, gson.toJson(screenName));
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntentExtra(URL, String.class);
        screenTitle = getIntentExtra(SCREEN_TITLE, String.class);
        analyticsScreenName = getIntentExtra(ANALYTICS_SCREEN_NAME, String.class);
        setContentView(R.layout.web_view_activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        trackAnalytics();
    }

    @Override
    public void onDestroy(){
        webView.removeAllViews();
        webView.destroy();
        super.onDestroy();
    }

    @Override
    protected void configureView() {
        enableBackButton();
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getUrl());
        setTitle(getScreenTitle());
    }

    protected String getUrl() {
        return url;
    }

    protected String getAnalyticsScreenName() {
        return analyticsScreenName;
    }

    protected String getScreenTitle() {
        return screenTitle;
    }

    private void trackAnalytics() {
        String screen = getAnalyticsScreenName();
        if(!StringUtils.isEmpty(screen)) {
            analyticsManager.trackScreen(screen);
        }
    }
}