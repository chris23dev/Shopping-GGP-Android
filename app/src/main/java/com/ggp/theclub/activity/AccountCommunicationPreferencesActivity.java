package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AccountManager.GigyaResponseListener;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.StringUtils;
import com.gigya.socialize.GSObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class AccountCommunicationPreferencesActivity extends BaseActivity {
    @BindString(R.string.phone_empty_error) String phoneEmptyErrorText;
    @BindString(R.string.phone_format_error) String phoneFormatErrorText;
    @BindString(R.string.subscription_message_format) String subscriptionMessageFormat;
    @BindString(R.string.subscription_message_option) String subscriptionMessageOptionText;
    @BindString(R.string.subscription_instruction_format) String subscriptionInstructionFormat;
    @Bind(R.id.send_email_switch) Checkable sendEmailSwitch;
    @Bind(R.id.send_sms_switch) Checkable sendSmsSwitch;
    @Bind(R.id.sms_message_info) LinearLayout smsMessageInfo;
    @Bind(R.id.phone_number_wrapper) TextInputLayout phoneNumberWrapper;
    @Bind(R.id.sms_phone_number) EditText phoneNumber;
    @Bind(R.id.divider_view) View dividerView;
    @Bind(R.id.subscription_message_view) TextView subscriptionMessageView;
    @Bind(R.id.subscription_choice_view) TextView subscriptionChoiceView;
    @Bind(R.id.subscription_instruction_view) TextView subscriptionInstructionView;
    @Bind(R.id.subscription_list) RadioGroup subscriptionList;
    @Bind(R.id.email_alert_label) TextView emailAlertLabel;
    @Bind(R.id.sms_alert_label) TextView smsAlertLabel;
    @Bind(R.id.sms_disclaimer_message) TextView smsDisclaimerMessage;

    private AccountManager accountManager = MallApplication.getApp().getAccountManager();
    private MallManager mallManager = MallApplication.getApp().getMallManager();
    private User user = accountManager.getCurrentUser().clone();
    private List<String> subscribedMallIds = user.getSubscribedMallIds();

    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountCommunicationPreferencesActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_communication_preferences_activity);
        emailAlertLabel.setText(getString(R.string.email_communications));
        smsAlertLabel.setText(getString(R.string.sms_alerts));
        phoneNumber.setHint(getString(R.string.mobile_phone));
        smsDisclaimerMessage.setText(getString(R.string.text_alert_disclaimer));
        subscriptionChoiceView.setText(getString(R.string.subscription_choice));

    }

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Account);
    }

    @Override
    protected void configureView() {
        setTitle(R.string.account_preferences_header);
        enableBackButton();
        setTextActionButton(R.string.save_preferences);
        textActionButton.setVisibility(View.GONE);
        setSmsInfoVisibility();
        setupAccountPreferences();
        setupAccountSubscriptions();
    }

    @Override
    public void onBackPressed() {
        if (textActionButton.isShown()) {
            AlertUtils.showDiscardDialog(this);
        } else {
            super.onBackPressed();
        }
    }

    private boolean validateSms() {
        boolean valid = !sendSmsSwitch.isChecked() || StringUtils.isValidPhoneNumber(phoneNumber.getText().toString());
        phoneNumberWrapper.setErrorEnabled(!valid);
        String phoneError = StringUtils.isEmpty(phoneNumber.getText().toString()) ? phoneEmptyErrorText : phoneFormatErrorText;
        phoneNumberWrapper.setError(valid ? null : phoneError);
        return valid;
    }

    private void save() {
        user.setEmailSubscribed(sendEmailSwitch.isChecked());
        user.setSmsSubscribed(sendSmsSwitch.isChecked());
        user.setMobilePhone(sendSmsSwitch.isChecked() ? phoneNumber.getText().toString() : null);

        if (subscriptionList.isShown()) {
            int selectedSubscription = subscriptionList.indexOfChild(ButterKnife.findById(subscriptionList, subscriptionList.getCheckedRadioButtonId()));
            String newMallId1 = subscribedMallIds.get(selectedSubscription);
            subscribedMallIds.remove(selectedSubscription);
            subscribedMallIds.add(0, newMallId1);
            user.setSubscribedMallIds(subscribedMallIds);
        }

        AnimationUtils.exitReveal(textActionButton);

        accountManager.saveAccountInfo(user, new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                Snackbar.make(layoutView, R.string.user_save_success, Snackbar.LENGTH_LONG).show();
                trackAnalytics();
                accountManager.setCurrentUser(user);
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                AnimationUtils.enterReveal(textActionButton);
                Snackbar.make(layoutView, R.string.user_save_failure, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupAccountPreferences() {
        if(user != null) {
            sendEmailSwitch.setChecked(user.isEmailSubscribed());
            sendSmsSwitch.setChecked(user.isSmsSubscribed());
            phoneNumber.setText(user.getMobilePhone());
        }
        setSmsInfoVisibility();
    }

    private void setSmsInfoVisibility(){
        smsMessageInfo.setVisibility(sendSmsSwitch.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void setupAccountSubscriptions() {
        if (subscribedMallIds.size() == 1 && !StringUtils.isEmpty(user.getMallId1()) && !user.isSubscribedMall(mallManager.getMall().getId())) {
            dividerView.setVisibility(View.VISIBLE);
            mallRepository.queryForMall(Integer.parseInt(user.getMallId1()), mall -> {
                subscriptionMessageView.setText(String.format(subscriptionMessageFormat, mall.getName()));
                subscriptionMessageView.setVisibility(View.VISIBLE);
            });
        } else if (subscribedMallIds.size() > 1) {
            mallRepository.queryForSimpleMalls(malls -> {
                if (!malls.isEmpty()) {
                    Map<Integer, String> mallNameMap = StreamSupport.stream(malls).collect(Collectors.toMap(Mall::getId, Mall::getName));
                    StreamSupport.stream(subscribedMallIds).forEach(subscribedMallId -> {
                        RadioButton subscriptionOption = new RadioButton(subscriptionList.getContext());
                        subscriptionOption.setText(mallNameMap.get(Integer.parseInt(subscribedMallId)));
                        subscriptionList.addView(subscriptionOption);
                        if (subscribedMallId.equals(user.getMallId1())) {
                            subscriptionList.check(subscriptionOption.getId());
                        }
                    });
                    subscriptionList.setOnCheckedChangeListener((group, checkedId) -> AnimationUtils.enterReveal(textActionButton));
                    subscriptionList.setVisibility(View.VISIBLE);
                    dividerView.setVisibility(View.VISIBLE);
                    subscriptionMessageView.setText(String.format(subscriptionMessageFormat, subscriptionMessageOptionText));
                    subscriptionMessageView.setVisibility(View.VISIBLE);
                    subscriptionChoiceView.setVisibility(View.VISIBLE);
                }
            });
        }
        if (!user.isSubscribedMall(mallManager.getMall().getId())) {
            subscriptionInstructionView.setText(String.format(subscriptionInstructionFormat, mallManager.getMall().getName()));
            subscriptionInstructionView.setVisibility(View.VISIBLE);
        }
    }

    private void trackAnalytics() {
        User currentUser = accountManager.getCurrentUser();
        HashMap<String, Object> contextData = new HashMap<String, Object>(){{
            put(AnalyticsManager.ContextDataKeys.PreferencesEmail, !currentUser.isEmailSubscribed() && user.isEmailSubscribed());
            put(AnalyticsManager.ContextDataKeys.PreferencesSMS, !currentUser.isSmsSubscribed() && user.isSmsSubscribed());
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.SavePreferences, contextData);
    }

    @Override
    public void onActionButtonClick() {
        if(validateSms()) {
            save();
        }
    }

    @OnCheckedChanged({R.id.send_email_switch, R.id.send_sms_switch})
    public void onToggleChange(CompoundButton view) {
        if (layoutView.isShown()) {
            AnimationUtils.enterReveal(textActionButton);
            if (view.getId() == R.id.send_sms_switch) {
                setSmsInfoVisibility();
            }
        }
    }

    @OnTextChanged(R.id.sms_phone_number)
    public void onNumberTextChange() {
        if (layoutView.isShown()) {
            AnimationUtils.enterReveal(textActionButton);
        }
    }
}