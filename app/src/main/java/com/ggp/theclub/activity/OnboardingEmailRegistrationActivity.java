package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;

import com.ggp.theclub.R;

import butterknife.BindColor;

public class OnboardingEmailRegistrationActivity extends AccountEmailRegistrationActivity {
    @BindColor(R.color.white) int white;
    @BindColor(R.color.extra_light_gray) int gray;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, OnboardingEmailRegistrationActivity.class);
    }

    @Override
    protected void configureView() {
        super.configureView();
        emailText.setTextColor(white);
        firstNameText.setTextColor(white);
        lastNameText.setTextColor(white);
        passwordInput.setTextColor(white);
        retypePasswordInput.setTextColor(white);
        passwordValidationView.setDefaultColor(gray);
    }

    @Override
    protected void continueToPreferences() {
        startActivityForResult(OnboardingRegistrationPreferencesActivity.buildIntent(this, createUserObject(), passwordInput.getText().toString()), RequestCode.FINISH_REQUEST_CODE);
    }
}
