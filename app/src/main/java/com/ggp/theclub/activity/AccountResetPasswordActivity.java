package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager.ResetPasswordListener;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.StringUtils;
import com.gigya.socialize.GSObject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class AccountResetPasswordActivity extends BaseActivity {
    @BindString(R.string.reset_password_success_dialog_title) String successDialogTitle;
    @BindString(R.string.reset_password_success_dialog_message) String successDialogMessage;
    @BindString(R.string.reset_password_no_email_found_dialog_message) String noEmailFoundDialogMessage;
    @BindString(R.string.reset_password_error_dialog_message) String errorDialogMessage;
    @BindString(R.string.reset_password_success_dialog_dismiss_button_text) String successDialogDismissButtonText;
    @BindString(R.string.reset_password_failure_dialog_dismiss_button_text) String failureDialogDismissButtonText;
    @Bind(R.id.email) EditText emailText;
    @Bind(R.id.email_error) TextView emailError;
    @Bind(R.id.reset_password_button) Button resetPasswordButton;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountResetPasswordActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_reset_password_activity);
        emailText.setHint(getString(R.string.email_input));
        emailError.setText(getString(R.string.email_error));
        resetPasswordButton.setText(getString(R.string.submit_button_text));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.reset_password_title);
        enableBackButton();
    }

    private boolean validateEmail() {
        boolean valid = StringUtils.isValidEmail(emailText.getText().toString());
        emailError.setText(StringUtils.isEmpty(emailText.getText().toString()) ? R.string.email_error : R.string.email_format_error);
        emailError.setVisibility(valid ? View.INVISIBLE : View.VISIBLE);
        return valid;
    }

    @OnClick(R.id.reset_password_button)
    public void onResetPasswordButtonClick() {
        if(validateEmail()) {
            resetPasswordButton.setEnabled(false);
            accountManager.resetPassword(emailText.getText().toString(), new ResetPasswordListener() {
                @Override
                public void onEmailNotFound() {
                    AlertUtils.showGenericDialog(null, noEmailFoundDialogMessage, failureDialogDismissButtonText, AccountResetPasswordActivity.this);
                    resetPasswordButton.setEnabled(true);
                }

                @Override
                public void onSuccess(GSObject data) {
                    AlertUtils.showGenericDialog(successDialogTitle, successDialogMessage, successDialogDismissButtonText, AccountResetPasswordActivity.this,
                            () -> finish());
                }

                @Override
                public void onFailure(GSObject data, String errorMessage) {
                    AlertUtils.showGenericDialog(null, errorDialogMessage, failureDialogDismissButtonText, AccountResetPasswordActivity.this);
                    resetPasswordButton.setEnabled(true);
                    Log.e(LOG_TAG, errorMessage);
                }
            });
        }
    }

    @OnFocusChange({R.id.reset_password_layout})
    public void onFocusChange(boolean hasFocus) {
        if(hasFocus) {
            hideKeyboard();
        }
    }
}