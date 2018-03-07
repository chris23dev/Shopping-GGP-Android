package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ggp.theclub.R;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.ViewUtils;
import com.gigya.socialize.GSObject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import de.greenrobot.event.EventBus;

public class AccountEmailLoginActivity extends BaseActivity {
    @BindString(R.string.email_error) String emailEmptyError;
    @BindString(R.string.email_format_error) String emailFormatError;
    @BindString(R.string.account_issues_message) String accountIssuesMessage;
    @BindString(R.string.account_issues_email) String accountIssuesEmail;
    @Bind(R.id.email_error) TextView emailError;
    @Bind(R.id.email) EditText emailText;
    @Bind(R.id.password_input) EditText passwordInput;
    @Bind(R.id.password_error_view) TextView passwordErrorView;
    @Bind(R.id.show_password_toggle) ToggleButton showPasswordToggle;
    @Bind(R.id.account_issues_message) TextView accountIssuesMessageView;
    @Bind(R.id.login_button) Button loginBtn;
    @Bind(R.id.reset_password_button) Button resetPasswordBtn;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountEmailLoginActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_email_login_activity);
        emailText.setHint(getString(R.string.email_input));
        emailError.setText(getString(R.string.email_error));
        showPasswordToggle.setTextOff(getString(R.string.show_password_text));
        showPasswordToggle.setTextOn(getString(R.string.hide_password_text));
        passwordInput.setHint(getString(R.string.password_input_hint));
        passwordErrorView.setText(getString(R.string.password_empty_error_message));
        loginBtn.setText(getString(R.string.login_button));
        resetPasswordBtn.setText(getString(R.string.forgot_password));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.login_title);
        setTextActionButton(R.string.cancel_text);
        ViewUtils.setClickableSpan(accountIssuesMessageView, accountIssuesMessage,accountIssuesEmail, null, null);
    }

    private boolean validate() {
        return validateEmail() & validatePassword();
    }

    private boolean validateEmail() {
        boolean valid = StringUtils.isValidEmail(emailText.getText().toString());
        String emailErrorText = StringUtils.isEmpty(emailText.getText().toString()) ? emailEmptyError : emailFormatError;
        emailError.setText(emailErrorText);
        emailError.setVisibility(valid ? View.INVISIBLE :View.VISIBLE);
        return valid;
    }

    private boolean validatePassword() {
        boolean valid = !StringUtils.isEmpty(passwordInput.getText().toString());
        passwordErrorView.setVisibility(valid ? View.INVISIBLE : View.VISIBLE);
        return valid;
    }

    private void login() {
        accountManager.loginWithEmail(emailText.getText().toString(), passwordInput.getText().toString(), new AccountManager.AccountLoginListener() {
            @Override
            public void onInvalidCredentials() {
                showErrorSnackbar(R.string.login_invalid_credentials_message);
            }

            @Override
            public void onSuccess(GSObject data) {
                EventBus.getDefault().post(new AccountLoginEvent(accountManager.isLoggedIn()));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                showErrorSnackbar();
            }
        });
    }

    @Override
    public void onActionButtonClick() {
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked() {
        hideKeyboard();
        if(validate()) {
            login();
        }
    }

    @OnFocusChange({R.id.login_layout})
    public void onFocusChange(boolean hasFocus) {
        if(hasFocus) {
            hideKeyboard();
        }
    }

    @OnClick(R.id.show_password_toggle)
    public void onClickShowPasswordToggle() {
        ViewUtils.setPasswordTextVisibility(passwordInput, showPasswordToggle.isChecked());
    }

    @OnClick(R.id.reset_password_button)
    public void onResetPasswordButtonClick() {
        startActivity(AccountResetPasswordActivity.buildIntent(this));
    }

    @OnClick(R.id.account_issues_message)
    public void onAccountIssuesClick() {
        IntentUtils.email(this, accountIssuesEmail);
    }
}