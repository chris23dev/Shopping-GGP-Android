package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ggp.theclub.R;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.AlertUtils;

import butterknife.BindColor;

public class OnboardingSocialRegistrationPreferencesActivity extends AccountSocialRegistrationPreferencesActivity {
    @BindColor(R.color.extra_light_gray) int gray;
    @BindColor(R.color.white) int white;

    public static Intent buildIntent(Context context, User user, String registrationType) {
        return buildIntent(context, OnboardingSocialRegistrationPreferencesActivity.class, user, registrationType);
    }

    @Override
    protected void configureView() {
        super.configureView();
        emailLabel.setTextColor(gray);
        smsLabel.setTextColor(gray);
        smsDisclaimer.setTextColor(gray);
        privacyView.setTextColor(gray);
        registerHeader.setTextColor(white);
        sweepstakesView.setTextColor(gray);
        accountIssuesMessageView.setTextColor(gray);
    }

    @Override
    protected void handleSweepstakesSuccess() {
        loadingIndicator.setVisibility(View.GONE);
        AlertUtils.showSweepstakesSuccessDialog(this, () -> {
            //aside from this alert do the same thing as normal registration success;
            handleRegistrationSuccess();
        });
    }
}