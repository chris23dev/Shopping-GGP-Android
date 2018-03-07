package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AccountManager.AccountLoginListener;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.ViewUtils;
import com.ggp.theclub.view.ValidationIndicator;
import com.gigya.socialize.GSObject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnCheckedChanged;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class AccountChangePasswordActivity extends BaseActivity {
    @BindString(R.string.password_length_error_message) String passwordLengthErrorMessage;
    @BindString(R.string.password_empty_error_message) String passwordEmptyErrorMessage;
    @BindString(R.string.retype_password_empty_error_message) String retypePasswordEmptyErrorMessage;
    @BindString(R.string.retype_password_match_error_message) String retypePasswordMatchErrorMessage;
    @BindString(R.string.invalid_password_error_label) String invalidPasswordErrorMessage;
    @Bind(R.id.password_input) EditText currentPasswordInput;
    @Bind(R.id.new_password_input) EditText newPasswordInput;
    @Bind(R.id.retype_password_input) EditText retypePasswordInput;
    @Bind(R.id.validation_indicator) ValidationIndicator currentValidationIndicator;
    @Bind(R.id.new_validation_indicator) ValidationIndicator newValidationIndicator;
    @Bind(R.id.retype_validation_indicator) ValidationIndicator retypeValidationIndicator;
    @Bind(R.id.current_show_password_toggle) ToggleButton currentShowPasswordToggle;
    @Bind(R.id.new_show_password_toggle) ToggleButton newShowPasswordToggle;
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();

    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountChangePasswordActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_change_password_activity);
        currentShowPasswordToggle.setText(getString(R.string.show_password_text));
        newShowPasswordToggle.setTextOff(getString(R.string.show_password_text));
        currentShowPasswordToggle.setTextOn(getString(R.string.hide_password_text));
        newShowPasswordToggle.setTextOn(getString(R.string.hide_password_text));
        retypePasswordInput.setHint(getString(R.string.retype_password_input_hint));
        currentPasswordInput.setHint(getString(R.string.current_password_label));
        newPasswordInput.setHint(getString(R.string.new_password_label));

    }

    @Override
    protected void configureView() {
        setTitle(R.string.change_password_title);
        enableBackButton();
        setTextActionButton(R.string.save_preferences);
        textActionButton.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (textActionButton.isShown()) {
            AlertUtils.showDiscardDialog(this);
        } else {
            super.onBackPressed();
        }
    }

    private boolean validatePasswordFields() {
        return validateCurrentPassword() & validateNewPassword() & validateRetypePassword();
    }

    private boolean validateCurrentPassword() {
        boolean valid = !StringUtils.isEmpty(currentPasswordInput.getText().toString());
        currentValidationIndicator.setMessage(valid ? null : passwordEmptyErrorMessage, valid);
        return valid;
    }

    private boolean validateNewPassword() {
        String password = newPasswordInput.getText().toString();
        boolean valid = StringUtils.isValidPassword(password);
        String errorMessage = null;
        if(!valid) {
            errorMessage = StringUtils.isEmpty(password) ? passwordEmptyErrorMessage : passwordLengthErrorMessage;
        }
        newValidationIndicator.setMessage(errorMessage, valid);
        return valid;
    }

    private boolean validateRetypePassword() {
        String newPassword = newPasswordInput.getText().toString();
        String retypePassword = retypePasswordInput.getText().toString();
        boolean valid = !StringUtils.isEmpty(retypePassword) && retypePassword.equals(newPassword);
        String errorMessage = null;
        if (!valid) {
            errorMessage = StringUtils.isEmpty(retypePassword) ? retypePasswordEmptyErrorMessage : retypePasswordMatchErrorMessage;
        }
        retypeValidationIndicator.setMessage(errorMessage, valid);
        return valid;
    }

    private void resetFields() {
        currentPasswordInput.setText(null);
        newPasswordInput.setText(null);
        retypePasswordInput.setText(null);
        currentValidationIndicator.reset();
        newValidationIndicator.reset();
        retypeValidationIndicator.reset();
        currentShowPasswordToggle.setChecked(false);
        newShowPasswordToggle.setChecked(false);
        textActionButton.setVisibility(View.GONE);
    }

    private void clearFocus() {
        currentPasswordInput.clearFocus();
        newPasswordInput.clearFocus();
        retypePasswordInput.clearFocus();
    }

    private void save() {
        String currentPassword = currentPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();
        accountManager.savePassword(currentPassword, newPassword, new AccountLoginListener() {
            @Override
            public void onSuccess(GSObject data) {
                resetFields();
                Snackbar.make(layoutView, R.string.user_save_success, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onInvalidCredentials() {
                currentPasswordInput.setText(null);
                AnimationUtils.enterReveal(textActionButton);
                currentValidationIndicator.setMessage(invalidPasswordErrorMessage, false);
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                AnimationUtils.enterReveal(textActionButton);
                Snackbar.make(layoutView, R.string.user_save_failure, Snackbar.LENGTH_LONG).show();
                Log.e(LOG_TAG, errorMessage);
            }
        });
    }

    private void trackAnalytics() {
        analyticsManager.trackAction(AnalyticsManager.Actions.ChangePassword);
    }

    @Override
    public void onActionButtonClick() {
        clearFocus();
        hideKeyboard();
        if(validatePasswordFields()) {
            AnimationUtils.exitReveal(textActionButton);
            save();
            trackAnalytics();
        }
    }

    @OnTextChanged(R.id.new_password_input)
    public void onEditNewPasswordField() {
        AnimationUtils.enterReveal(textActionButton);
        validateNewPassword();
        if(retypeValidationIndicator.isIconVisible()) {
            validateRetypePassword();
        }
    }

    @OnTextChanged(R.id.retype_password_input)
    public void onEditRetypePasswordField() {
        AnimationUtils.enterReveal(textActionButton);
        validateRetypePassword();
    }

    @OnTextChanged(R.id.password_input)
    public void onEditCurrentPasswordField() {
        AnimationUtils.enterReveal(textActionButton);
        validateCurrentPassword();
    }


    @OnFocusChange(R.id.account_password_layout)
    public void onFocusChange(boolean hasFocus) {
        if(hasFocus) {
            hideKeyboard();
        }
    }

    @OnCheckedChanged(R.id.current_show_password_toggle)
    public void onClickCurrentShowPasswordToggle() {
        ViewUtils.setPasswordTextVisibility(currentPasswordInput, currentShowPasswordToggle.isChecked());
    }

    @OnCheckedChanged(R.id.new_show_password_toggle)
    public void onClickNewShowPasswordToggle() {
        ViewUtils.setPasswordTextVisibility(newPasswordInput, newShowPasswordToggle.isChecked());
        ViewUtils.setPasswordTextVisibility(retypePasswordInput, newShowPasswordToggle.isChecked());
    }
}