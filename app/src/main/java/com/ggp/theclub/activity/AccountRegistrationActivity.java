package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.model.Sweepstakes;
import com.ggp.theclub.util.SweepstakesUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRegistrationActivity extends AccountAuthenticationActivity {
    @BindString(R.string.facebook_register_text) String facebookRegisterText;
    @BindString(R.string.google_register_text) String googleRegisterText;
    @BindString(R.string.email_register_text) String emailRegisterText;
    @Bind(R.id.sweepstakes_layout) LinearLayout sweepstakesLayout;
    @Bind(R.id.have_account_label) TextView haveAccountLabel;
    @Bind(R.id.sweepstakes_description) TextView sweepstakesDescription;
    @Bind(R.id.terms_label) TextView termsLabel;
    @Bind(R.id.sweepstakes_terms) TextView sweepstakesTerms;
    @Bind(R.id.facebook_button_label) TextView facebookBtnLabel;
    @Bind(R.id.google_button_label) TextView googleBtnLabel;
    @Bind(R.id.email_button_label) TextView emailBtnLabel;


    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountRegistrationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_registration_activity);
        haveAccountLabel.setText(getString(R.string.login_link_prompt));
        sweepstakesDescription.setText(R.string.sweepstakes_register_text);
        termsLabel.setText(R.string.view_terms_label);
        sweepstakesTerms.setText(R.string.terms_conditions_label);
        facebookBtnLabel.setText(getString(R.string.facebook_login_text));
        googleBtnLabel.setText(getString(R.string.google_login_text));
        emailBtnLabel.setText(getString(R.string.email_login_text));

    }

    @Override
    protected void configureView() {
        super.configureView();
        setTitle(R.string.register_title);
        checkSweepstakes();
    }

    @Override
    protected String getFacebookButtonLabel() {
        return facebookRegisterText;
    }

    @Override
    protected String getGoogleButtonLabel() {
        return googleRegisterText;
    }

    @Override
    protected String getEmailButtonLabel() {
        return emailRegisterText;
    }

    @Override
    public void onEmailButtonClick() {
        startActivityForResult(AccountEmailRegistrationActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClick() {
        startActivityForResult(AccountLoginActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }

    @Override
    public void onBackButtonClick() {
        setResult(RESULT_OK);
        super.onBackButtonClick();
    }

    private void checkSweepstakes() {
        MallApiClient.getInstance().getMallApi().getSweepstakes().enqueue(new Callback<List<Sweepstakes>>() {
            @Override
            public void onResponse(Call<List<Sweepstakes>> call, Response<List<Sweepstakes>> response) {
                if (response.isSuccessful()) {
                    List<Sweepstakes> sweepstakesList = response.body();
                    if(SweepstakesUtils.isSweepstakesActive(sweepstakesList)) {
                        sweepstakesLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Sweepstakes>> call, Throwable t) {
                    Log.w(LOG_TAG, t);
            }
        });
    }

    @OnClick(R.id.sweepstakes_terms)
    public void onSweepstakesTermsClick() {
        //Need to open the link in a browser instead of the usual webview because it points to a PDF
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.sweepstakes_terms_link)));
        startActivity(browserIntent);
    }
}