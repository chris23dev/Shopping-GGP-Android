 package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.BindColor;

 public class OnboardingEmailLoginActivity extends AccountEmailLoginActivity {

     @Bind(R.id.scroll_view) NestedScrollView scrollView;
     @Bind(R.id.email_wrapper) TextInputLayout emailLayout;
     @Bind(R.id.password_wrapper) TextInputLayout passwordLayout;
     @BindColor(R.color.extra_light_gray) int gray;
     @BindColor(R.color.white) int white;

     public static Intent buildIntent(Context context) {
         return buildIntent(context, OnboardingEmailLoginActivity.class);
     }

     @Override
     protected void configureView() {
         super.configureView();
         scrollView.setBackgroundResource(0);
         emailLayout.setHintTextAppearance(R.style.OnboardingTextInputTheme);
         emailText.setHintTextColor(gray);
         emailText.setTextColor(white);
         passwordLayout.setHintTextAppearance(R.style.OnboardingTextInputTheme);
         passwordInput.setHintTextColor(gray);
         passwordInput.setTextColor(white);
         accountIssuesMessageView.setTextColor(gray);
     }

     @Override
     public void onResetPasswordButtonClick() {
         startActivity(OnboardingResetPasswordActivity.buildIntent(this));
     }
 }