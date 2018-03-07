package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.model.User;

import butterknife.Bind;

public class AccountSocialRegistrationPreferencesActivity extends AccountRegistrationPreferencesActivity {
    @Bind(R.id.register_header) TextView registerHeader;

    public static Intent buildIntent(Context context, User user, String registrationType) {
        return buildIntent(context, AccountSocialRegistrationPreferencesActivity.class, user, registrationType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        user = getIntentExtra(User.class);
        registrationType = getIntentExtra(String.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void configureView() {
        super.configureView();
        registerHeader.setVisibility(View.VISIBLE);
    }

    @Override
    protected void save() {
        accountManager.register(user, responseListener);
    }
}