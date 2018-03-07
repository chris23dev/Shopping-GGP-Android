package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;

import com.ggp.theclub.R;

import butterknife.BindColor;

public class OnboardingSocialRegistrationActivity extends AccountSocialRegistrationActivity{
    @BindColor(R.color.white) int white;

    public static Intent buildIntent(Context context, String registrationType) {
        return buildIntent(context, OnboardingSocialRegistrationActivity.class, registrationType);
    }

    @Override
    protected void configureView() {
        super.configureView();
        registerHeaderView.setTextColor(white);
        accountDescriptionView.setTextColor(white);
        emailText.setTextColor(white);
        firstNameText.setTextColor(white);
        lastNameText.setTextColor(white);
    }

    @Override
    protected void proceedToPreferences() {
        startActivityForResult(OnboardingSocialRegistrationPreferencesActivity.buildIntent(OnboardingSocialRegistrationActivity.this, user, registrationType), RequestCode.FINISH_REQUEST_CODE);
    }
}
