package com.ggp.theclub.activity;

import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AccountManager.AccountListener;
import com.ggp.theclub.manager.AnalyticsManager;
import com.gigya.socialize.GSObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public abstract class AccountAuthenticationActivity extends BaseActivity {
    @BindColor(R.color.facebook) int facebookColor;
    @BindColor(R.color.google) int googleColor;
    @BindColor(R.color.white) int emailColor;
    @Bind(R.id.facebook_account_button) FrameLayout facebookAccountButton;
    @Bind(R.id.google_account_button) FrameLayout googleAccountButton;
    @Bind(R.id.email_account_button) FrameLayout emailAccountButton;
    @Bind(R.id.facebook_button_label) TextView facebookButtonLabel;
    @Bind(R.id.google_button_label) TextView googleButtonLabel;
    @Bind(R.id.email_button_label) TextView emailButtonLabel;

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Account);
    }

    @Override
    protected void configureView() {
        setTextActionButton(R.string.cancel_text);

        facebookAccountButton.getBackground().setColorFilter(facebookColor, PorterDuff.Mode.ADD);
        googleAccountButton.getBackground().setColorFilter(googleColor, PorterDuff.Mode.ADD);
        emailAccountButton.getBackground().setColorFilter(emailColor, PorterDuff.Mode.ADD);

        facebookButtonLabel.setText(getFacebookButtonLabel());
        googleButtonLabel.setText(getGoogleButtonLabel());
        emailButtonLabel.setText(getEmailButtonLabel());
    }

    protected abstract String getFacebookButtonLabel();
    protected abstract String getGoogleButtonLabel();
    protected abstract String getEmailButtonLabel();

    protected void continueToPreferences(String provider) {
        startActivityForResult(AccountSocialRegistrationActivity.buildIntent(AccountAuthenticationActivity.this, provider), RequestCode.FINISH_REQUEST_CODE);
    }

    protected void loginWithProvider(String provider) {
        accountManager.loginWithProvider(provider, this, new AccountListener() {
            @Override
            public void onSuccess(GSObject data) {
                Log.i(LOG_TAG, "Successful login with " + provider);
                trackAnalytics(provider);
                EventBus.getDefault().post(new AccountLoginEvent(accountManager.isLoggedIn()));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                Log.e(LOG_TAG,"Unable to login with " + provider + ": " + errorMessage);
            }

            @Override
            public void onRegistrationRequired(GSObject data) {
                Log.i(LOG_TAG, "Registration required");
                continueToPreferences(provider);
            }
        });
    }

    private void trackAnalytics(String provider) {
        HashMap<String, Object> contextData = new HashMap<String, Object>(){{
            put(AnalyticsManager.ContextDataKeys.AuthType, provider);
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.AuthenticateUser, contextData);
    }

    @Override
    public void onActionButtonClick() {
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.facebook_account_button)
    public void onFacebookButtonClick() {
        loginWithProvider(AccountManager.PROVIDER_FACEBOOK);
    }

    @OnClick(R.id.google_account_button)
    public void onGoogleButtonClick() {
        loginWithProvider(AccountManager.PROVIDER_GOOGLE);
    }

    @OnClick(R.id.email_account_button)
    public abstract void onEmailButtonClick();
}