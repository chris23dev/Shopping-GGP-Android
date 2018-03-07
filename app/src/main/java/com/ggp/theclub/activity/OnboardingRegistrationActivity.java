package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.BindColor;

public class OnboardingRegistrationActivity extends AccountRegistrationActivity {

    @Bind(R.id.scroll_view) NestedScrollView scrollView;
    @Bind(R.id.have_account_label) TextView haveAccountTextView;
    @Bind(R.id.sweepstakes_description) TextView sweepstakesDescription;
    @Bind(R.id.terms_label) TextView termsView;
    @BindColor(R.color.extra_light_gray) int gray;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, OnboardingRegistrationActivity.class);
    }

    @Override
    protected void configureView() {
        super.configureView();
        scrollView.setBackgroundResource(0);
        sweepstakesDescription.setTextColor(gray);
        termsView.setTextColor(gray);
        haveAccountTextView.setTextColor(gray);
    }

    @Override
    public void onLoginButtonClick() {
        startActivityForResult(OnboardingLoginActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }

    @Override
    public void onEmailButtonClick() {
        startActivityForResult(OnboardingEmailRegistrationActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }

    @Override
    protected void continueToPreferences(String provider) {
        startActivityForResult(OnboardingSocialRegistrationActivity.buildIntent(this, provider), RequestCode.FINISH_REQUEST_CODE);
    }
}