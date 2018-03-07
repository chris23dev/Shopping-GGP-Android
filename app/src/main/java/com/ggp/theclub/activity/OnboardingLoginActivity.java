 package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;

import com.ggp.theclub.R;

import butterknife.Bind;

public class OnboardingLoginActivity extends AccountLoginActivity {

    @Bind(R.id.scroll_view) NestedScrollView scrollView;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, OnboardingLoginActivity.class);
    }

    @Override
    protected void configureView() {
        super.configureView();
        scrollView.setBackgroundResource(0);
    }

    @Override
    public void onEmailButtonClick() {
        startActivityForResult(OnboardingEmailLoginActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }

    @Override
    protected void continueToPreferences(String provider) {
        startActivityForResult(OnboardingSocialRegistrationPreferencesActivity.buildIntent(this, accountManager.getCurrentUser(), provider), RequestCode.FINISH_REQUEST_CODE);
    }
}