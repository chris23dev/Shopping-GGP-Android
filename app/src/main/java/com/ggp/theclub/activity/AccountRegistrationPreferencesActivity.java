package com.ggp.theclub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.api.HelloWorldApiClient;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.event.AccountRegistrationEvent;
import com.ggp.theclub.manager.AccountManager.GigyaResponseListener;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.HelloWorldProfileRequest;
import com.ggp.theclub.model.Sweepstakes;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.NetworkUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.SweepstakesUtils;
import com.ggp.theclub.util.ViewUtils;
import com.gigya.socialize.GSObject;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class AccountRegistrationPreferencesActivity extends BaseActivity {
    @BindString(R.string.phone_empty_error) String phoneEmptyErrorText;
    @BindString(R.string.phone_format_error) String phoneFormatErrorText;
    @BindString(R.string.sweepstakes_enroll) String nonClickableSweepsMessage;
    @BindString(R.string.terms_conditions_label) String clickableSweepsMessage;
    @BindString(R.string.privacy_description) String nonClickablePrivacyMessage;
    @BindString(R.string.privacy_link) String clickablePrivacyMessage;
    @BindString(R.string.account_issues_message) String accountIssuesMessage;
    @BindString(R.string.account_issues_email) String accountIssuesEmail;
    @Bind(R.id.loading_indicator) View loadingIndicator;
    @Bind(R.id.email_alert_label) TextView emailLabel;
    @Bind(R.id.sms_alert_label) TextView smsLabel;
    @Bind(R.id.sms_disclaimer_message) TextView smsDisclaimer;
    @Bind(R.id.send_email_switch) AppCompatCheckBox sendEmailSwitch;
    @Bind(R.id.send_sms_switch) AppCompatCheckBox sendSmsSwitch;
    @Bind(R.id.sms_message_info) LinearLayout smsMessageInfo;
    @Bind(R.id.phone_number_wrapper) TextInputLayout phoneNumberWrapper;
    @Bind(R.id.sms_phone_number) EditText phoneNumber;
    @Bind(R.id.terms_error) TextView termsError;
    @Bind(R.id.sweepstakes_layout) LinearLayout sweepstakesLayout;
    @Bind(R.id.sweepstakes_terms) TextView sweepstakesView;
    @Bind(R.id.sweepstakes_checkbox) CheckBox sweepstakesCheckbox;
    @Bind(R.id.privacy) TextView privacyView;
    @Bind(R.id.privacy_checkbox) CheckBox privacyCheckbox;
    @Bind(R.id.account_issues_message) TextView accountIssuesMessageView;
    @Bind(R.id.register_header) TextView registerHeader;
    @Bind(R.id.save_button) Button saveBtn;

    private HelloWorldApiClient helloWorldApiClient = HelloWorldApiClient.getInstance();

    protected GigyaResponseListener responseListener;
    protected String registrationType;
    protected User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_registration_preferences_activity);
        registerHeader.setText(getString(R.string.register_header));
        termsError.setText(getString(R.string.terms_error));
        emailLabel.setText(getString(R.string.email_communications));
        smsLabel.setText(getString(R.string.sms_alerts));
        phoneNumber.setHint(getString(R.string.mobile_phone));
        smsDisclaimer.setText(getString(R.string.text_alert_disclaimer));
        saveBtn.setText(getString(R.string.done_button));
    }

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Account);
    }

    @Override
    protected void configureView() {
        setTitle(R.string.account_title);
        setTextActionButton(R.string.cancel_text);

        setupPrivacyLink();
        checkSweepstakes();

        sendEmailSwitch.setChecked(true);
        sendSmsSwitch.setChecked(true);

        setSmsInfoVisibility();

        ViewUtils.setClickableSpan(accountIssuesMessageView, accountIssuesMessage,accountIssuesEmail, null, null);

        responseListener = new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                accountManager.setCurrentUser(user);
                trackAnalytics();
                if (sweepstakesCheckbox.isChecked()) {
                    makeSweepstakesCall();
                } else {
                    handleRegistrationSuccess();
                }
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                Log.d(LOG_TAG, errorMessage);
                showErrorSnackbar();
            }
        };
    }

    private void makeSweepstakesCall() {
        User user = accountManager.getCurrentUser();
        String ipAddress = NetworkUtils.getIPV4Address();
        HelloWorldProfileRequest profileRequest = new HelloWorldProfileRequest(user.getFirstName(), user.getLastName(), user.getEmail(), mallManager.getMall().getName());
        helloWorldApiClient.getHelloWorldApi().postProfile(profileRequest, ipAddress).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    handleSweepstakesSuccess();
                } else {
                    handleRegistrationSuccess();
                    Log.e(LOG_TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                handleRegistrationSuccess();
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    protected void handleRegistrationSuccess() {
        EventBus.getDefault().post(new AccountLoginEvent(accountManager.isLoggedIn()));
        EventBus.getDefault().post(new AccountRegistrationEvent(false));
        setResult(RESULT_OK);
        finish();
    }

    protected void handleSweepstakesSuccess() {
        EventBus.getDefault().post(new AccountLoginEvent(accountManager.isLoggedIn()));
        EventBus.getDefault().post(new AccountRegistrationEvent(true));
        setResult(RESULT_OK);
        finish();
    }

    protected abstract void save();

    protected void trackAnalytics() {
        HashMap<String, Object> registerContextData = new HashMap<String, Object>() {{
            put(AnalyticsManager.ContextDataKeys.AuthType, registrationType);
            put(AnalyticsManager.ContextDataKeys.FormName, AnalyticsManager.ContextDataValues.CustomerLeadType);
            put(AnalyticsManager.ContextDataKeys.CustomerLeadType, AnalyticsManager.ContextDataValues.CustomerLeadType);
            put(AnalyticsManager.ContextDataKeys.CustomerLeadLevel, AnalyticsManager.ContextDataValues.CustomerLeadLevel);
            put(AnalyticsManager.ContextDataKeys.PreferencesEmail, sendEmailSwitch.isChecked());
            put(AnalyticsManager.ContextDataKeys.PreferencesSMS, sendSmsSwitch.isChecked());
            put(AnalyticsManager.ContextDataKeys.PreferencesSweepstakes, sweepstakesCheckbox.isChecked());
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.RegisterUser, registerContextData);

        HashMap<String, Object> authenticateContextData = new HashMap<String, Object>(){{
            put(AnalyticsManager.ContextDataKeys.AuthType, registrationType);
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.AuthenticateUser, authenticateContextData);
    }

    private boolean validate() {
        return validatePrivacy() & validateSms();
    }

    private void checkSweepstakes() {
        MallApiClient.getInstance().getMallApi().getSweepstakes().enqueue(new Callback<List<Sweepstakes>>() {
            @Override
            public void onResponse(Call<List<Sweepstakes>> call, Response<List<Sweepstakes>> response) {
                if (response.isSuccessful()) {
                    List<Sweepstakes> sweepstakesList = response.body();
                    if(SweepstakesUtils.isSweepstakesActive(sweepstakesList)) {
                        sweepstakesLayout.setVisibility(View.VISIBLE);
                        setupSweepstakesLink();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Sweepstakes>> call, Throwable t) {
                Log.w(LOG_TAG, t);
            }
        });
    }

    private void setupPrivacyLink() {
        ViewUtils.setClickableSpan(privacyView, nonClickablePrivacyMessage, clickablePrivacyMessage, null,
                () -> startActivity(TermsActivity.buildIntent(this)));
    }

    private void setupSweepstakesLink() {
        ViewUtils.setClickableSpan(sweepstakesView, nonClickableSweepsMessage, clickableSweepsMessage, null,
                () -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.sweepstakes_terms_link)))));
    }

    private boolean validatePrivacy() {
        termsError.setVisibility(privacyCheckbox.isChecked() ? View.INVISIBLE : View.VISIBLE);
        return privacyCheckbox.isChecked();
    }

    private void updateUserFields() {
        user.setEmailSubscribed(sendEmailSwitch.isChecked());
        user.setSmsSubscribed(sendSmsSwitch.isChecked());
        user.setMobilePhone(sendSmsSwitch.isChecked() ? phoneNumber.getText().toString() : null);
    }

    private void setSmsInfoVisibility(){
        smsMessageInfo.setVisibility(sendSmsSwitch.isChecked() ? View.VISIBLE : View.GONE);
    }

    private boolean validateSms() {
        boolean valid = !sendSmsSwitch.isChecked() || StringUtils.isValidPhoneNumber(phoneNumber.getText().toString());
        phoneNumberWrapper.setErrorEnabled(!valid);
        String phoneError = StringUtils.isEmpty(phoneNumber.getText().toString()) ? phoneEmptyErrorText : phoneFormatErrorText;
        phoneNumberWrapper.setError(valid ? null : phoneError);
        return valid;
    }

    @Override
    public void onActionButtonClick() {
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.send_sms_switch)
    public void onSmsSwitchClick(){
        setSmsInfoVisibility();
    }

    @OnClick(R.id.save_button)
    public void onSaveButtonClick() {
        if(validate()) {
            updateUserFields();
            loadingIndicator.setVisibility(View.VISIBLE);
            save();
        }
    }

    @OnClick(R.id.account_issues_message)
    public void onAccountIssuesClick() {
        IntentUtils.email(this, accountIssuesEmail);
    }
}