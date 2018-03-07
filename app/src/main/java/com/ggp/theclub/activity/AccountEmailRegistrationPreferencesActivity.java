package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.User;

public class AccountEmailRegistrationPreferencesActivity extends AccountRegistrationPreferencesActivity {
    private String password;

    public static Intent buildIntent(Context context, User user, String password) {
        return buildIntent(context, AccountEmailRegistrationPreferencesActivity.class, user, password);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registrationType = AnalyticsManager.ContextDataValues.AuthTypeEmail;
        user = getIntentExtra(User.class);
        password = getIntentExtra(String.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void save() {
        accountManager.registerEmail(user, password, responseListener);
    }
}