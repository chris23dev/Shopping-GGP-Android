package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FeedbackActivity extends WebViewActivity {
    private final String FEEDBACK_MALL_NAME_PARAMETER = "&custom_var=";
    private final String FEEDBACK_URL = "https://secure.opinionlab.com/ccc01/o.asp?id=tFfThNWB";

    public static Intent buildIntent(Context context) {
        return buildIntent(context, FeedbackActivity.class);
    }

    @Override
    protected void configureView() {
        super.configureView();
    }

    @Override
    protected String getUrl() {
        try {
            String mallName = mallManager.getMall().getName();
            return StringUtils.isEmpty(mallName) ?
                    FEEDBACK_URL : FEEDBACK_URL + FEEDBACK_MALL_NAME_PARAMETER + URLEncoder.encode(mallName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return FEEDBACK_URL;
        }
    }

    @Override
    protected String getAnalyticsScreenName() {
        return AnalyticsManager.Screens.Feedback;
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.feedback_title);
    }
}