package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.BindString;

public class AccountLoginActivity extends AccountAuthenticationActivity {
    @BindString(R.string.facebook_login_text) String facebookLoginText;
    @BindString(R.string.google_login_text) String googleLoginText;
    @BindString(R.string.email_login_text) String emailLoginText;
    @Bind(R.id.facebook_button_label) TextView facebookBtnLabel;
    @Bind(R.id.google_button_label) TextView googleBtnLabel;
    @Bind(R.id.email_button_label) TextView emailBtnLabel;


    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountLoginActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_login_activity);
        facebookBtnLabel.setText(getString(R.string.facebook_login_text));
        googleBtnLabel.setText(getString(R.string.google_login_text));
        emailBtnLabel.setText(getString(R.string.email_login_text));


    }

    @Override
    protected void configureView() {
        super.configureView();
        setTitle(R.string.login_title);
    }

    @Override
    protected String getFacebookButtonLabel() {
        return facebookLoginText;
    }

    @Override
    protected String getGoogleButtonLabel() {
        return googleLoginText;
    }

    @Override
    protected String getEmailButtonLabel() {
        return emailLoginText;
    }

    @Override
    public void onEmailButtonClick() {
        startActivityForResult(AccountEmailLoginActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }
}