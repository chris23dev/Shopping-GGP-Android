package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.BindColor;

public class OnboardingResetPasswordActivity extends AccountResetPasswordActivity {
    @Bind(R.id.email_wrapper) TextInputLayout emailLayout;
    @BindColor(R.color.extra_light_gray) int gray;
    @BindColor(R.color.white) int white;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, OnboardingResetPasswordActivity.class);
    }

    @Override
    protected void configureView() {
        super.configureView();
        emailLayout.setHintTextAppearance(R.style.OnboardingTextInputTheme);
        emailText.setHintTextColor(gray);
        emailText.setTextColor(white);
    }
}
