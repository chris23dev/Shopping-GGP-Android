package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AccountManager.GigyaResponseListener;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.StringUtils;
import com.gigya.socialize.GSObject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

public class AccountLandingActivity extends BaseActivity {
    @BindString(R.string.name_greeting_format) String nameGreetingFormat;
    @Bind(R.id.logo_layout) RelativeLayout logoLayout;
    @Bind(R.id.logo_text_view) TextView logoTextView;
    @Bind(R.id.logo_image_view) ImageView logoImageView;
    @Bind(R.id.greeting_view) TextView greetingView;
    @Bind(R.id.change_password_button) LinearLayout changePasswordButton;
    @Bind(R.id.manage_favorites) Button manageFavorites;
    @Bind(R.id.my_information_button) TextView myInformation;
    @Bind(R.id.communication_preferences_button_text) TextView communicationPreferencesBtn;
    @Bind(R.id.change_password_button_text) TextView changePasswordBtn;
    @Bind(R.id.logout_button) TextView logoutBtn;
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();

    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountLandingActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_landing_activity);
        manageFavorites.setText(getString(R.string.manage_favorites_button_text));
        myInformation.setText(getString(R.string.my_information_button_text));
        communicationPreferencesBtn.setText(getString(R.string.communication_preferences_button_text));
        changePasswordBtn.setText(getString(R.string.change_password_button_text));
        logoutBtn.setText(getString(R.string.logout_button_text));
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayAccountInfo();
    }

    @Override
    protected void configureView() {
        setTitle(R.string.account_title);
        enableBackButton();
    }

    private void displayAccountInfo() {
        User user = accountManager.getCurrentUser();
        if(user != null) {
            AnimationUtils.enterReveal(logoLayout, true);
            ImageUtils.setCircularLogo(logoImageView, logoTextView, user.getPhotoURL(), user.getFirstName());
            if (!StringUtils.isEmpty(user.getFirstName())) {
                greetingView.setText(String.format(nameGreetingFormat, user.getFirstName()));
            }
            if (user.isSocialLogin()) {
                changePasswordButton.setVisibility(View.GONE);
            }
        } else {
            logoLayout.setVisibility(View.GONE);
            greetingView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.manage_favorites)
    public void onManageFavoritesButtonClick() {
        startActivity(ManageFavoritesActivity.buildIntent(this));
    }

    @OnClick(R.id.my_information_button)
    public void onMyInformationButtonClick() {
        startActivity(AccountInformationActivity.buildIntent(this));
    }

    @OnClick(R.id.communication_preferences_button)
    public void onCommunicationPreferencesButtonClick() {
        startActivity(AccountCommunicationPreferencesActivity.buildIntent(this));
    }

    @OnClick(R.id.change_password_button)
    public void onChangePasswordButtonClick() {
        startActivity(AccountChangePasswordActivity.buildIntent(this));
    }

    @OnClick(R.id.logout_button)
    public void onLogoutButtonClick() {
        accountManager.logout(new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                // TODO: Properly refresh app after logout
                finish();
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                Log.e(getClass().getSimpleName(), "Logout failed");
            }
        });
    }
}