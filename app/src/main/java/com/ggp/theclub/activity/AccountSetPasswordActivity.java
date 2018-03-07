package com.ggp.theclub.activity;

import android.widget.EditText;
import android.widget.ToggleButton;

import com.ggp.theclub.R;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.ViewUtils;
import com.ggp.theclub.view.ValidationIndicator;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public abstract class AccountSetPasswordActivity extends BaseActivity {
    @BindString(R.string.password_length_error_message) String passwordLengthErrorMessage;
    @BindString(R.string.password_empty_error_message) String passwordEmptyErrorMessage;
    @BindString(R.string.retype_password_empty_error_message) String retypePasswordEmptyErrorMessage;
    @BindString(R.string.retype_password_match_error_message) String retypePasswordMatchErrorMessage;
    @Bind(R.id.password_input) EditText passwordInput;
    @Bind(R.id.show_password_toggle) ToggleButton showPasswordToggle;
    @Bind(R.id.password_validation_view) ValidationIndicator passwordValidationView;
    @Bind(R.id.retype_password_input) EditText retypePasswordInput;
    @Bind(R.id.retype_password_validation_view) ValidationIndicator retypePasswordValidationView;

    @Override
    protected void configureView() {
        setTitle(R.string.register_title);
        setTextActionButton(R.string.cancel_text);
        passwordValidationView.setDefaultColor(R.color.dark_gray);
        passwordValidationView.setDefaultMessage(passwordLengthErrorMessage);
    }

    protected boolean validatePasswordFields() {
        return validatePassword() & validateRetypePassword();
    }

    private boolean validatePassword() {
        String password = passwordInput.getText().toString();
        boolean valid = StringUtils.isValidPassword(password);
        String errorMessage = null;
        if(!valid) {
            errorMessage = StringUtils.isEmpty(password) ? passwordEmptyErrorMessage : passwordLengthErrorMessage;
        }
        passwordValidationView.setMessage(errorMessage, valid);
        return valid;
    }

    private boolean validateRetypePassword() {
        String newPassword = passwordInput.getText().toString();
        String retypePassword = retypePasswordInput.getText().toString();
        boolean valid = !StringUtils.isEmpty(retypePassword) && retypePassword.equals(newPassword);
        String errorMessage = null;
        if (!valid) {
            errorMessage = StringUtils.isEmpty(retypePassword) ? retypePasswordEmptyErrorMessage : retypePasswordMatchErrorMessage;
        }
        retypePasswordValidationView.setMessage(errorMessage, valid);
        return valid;
    }

    @Override
    public void onActionButtonClick() {
        setResult(RESULT_OK);
        finish();
    }

    @OnTextChanged(R.id.password_input)
    public void onEditPasswordField() {
        validatePassword();
        if(retypePasswordValidationView.isIconVisible()) {
            validateRetypePassword();
        }
    }

    @OnTextChanged(R.id.retype_password_input)
    public void onEditRetypePasswordField() {
        validateRetypePassword();
    }

    @OnClick(R.id.show_password_toggle)
    public void onClickShowPasswordToggle() {
        ViewUtils.setPasswordTextVisibility(passwordInput, showPasswordToggle.isChecked());
        ViewUtils.setPasswordTextVisibility(retypePasswordInput, showPasswordToggle.isChecked());
    }

    @OnFocusChange(R.id.account_password_layout)
    public void onFocusChange(boolean hasFocus) {
        if(hasFocus) {
            hideKeyboard();
        }
    }
}