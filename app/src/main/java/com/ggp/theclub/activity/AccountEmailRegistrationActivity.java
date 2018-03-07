package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager.EmailAvailabilityListener;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.StringUtils;
import com.gigya.socialize.GSObject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;

public class AccountEmailRegistrationActivity extends AccountSetPasswordActivity {
    @BindString(R.string.email_error) String emailEmptyError;
    @BindString(R.string.email_format_error) String emailFormatError;
    @Bind(R.id.email_error) TextView emailError;
    @Bind(R.id.first_name_error) TextView firstNameError;
    @Bind(R.id.last_name_error) TextView lastNameError;
    @Bind(R.id.email) EditText emailText;
    @Bind(R.id.first_name) EditText firstNameText;
    @Bind(R.id.last_name) EditText lastNameText;
    @Bind(R.id.required_field) TextView requiredField;
    @Bind(R.id.register_button) Button registerBtn;
    @Bind(R.id.password_input) EditText passwordInput;
    @Bind(R.id.show_password_toggle)ToggleButton showPasswordToggle;
    @Bind(R.id.retype_password_input)EditText retypePasswordInput;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountEmailRegistrationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_email_registration_activity);
        emailText.setHint(getString(R.string.email_register));
        firstNameText.setHint(getString(R.string.first_name));
        lastNameText.setHint(getString(R.string.last_name));
        requiredField.setHint(getString(R.string.required_field));
        registerBtn.setText(getString(R.string.register_button_text));
        emailError.setText(getString(R.string.email_error));
        firstNameError.setText(getString(R.string.first_name_error));
        lastNameError.setText(getString(R.string.last_name_error));
        showPasswordToggle.setTextOff(getString(R.string.show_password_text));
        showPasswordToggle.setTextOn(getString(R.string.hide_password_text));
        passwordInput.setHint(getString(R.string.password_create_hint));
        retypePasswordInput.setHint(getString(R.string.retype_password_input_hint));
    }

    @Override
    protected void configureView() {
        super.configureView();
        setTitle(R.string.register_title);
        setTextActionButton(R.string.cancel_text);
    }

    protected User createUserObject() {
        User user = new User(new GSObject());
        user.setEmail(emailText.getText().toString());
        user.setFirstName(firstNameText.getText().toString());
        user.setLastName(lastNameText.getText().toString());
        return user;
    }

    protected void continueToPreferences() {
        startActivityForResult(AccountEmailRegistrationPreferencesActivity.buildIntent(this, createUserObject(), passwordInput.getText().toString()), RequestCode.FINISH_REQUEST_CODE);
    }

    private boolean validate() {
        return validateEmail() & validateFirstName() & validateLastName();
    }

    private boolean validateEmail() {
        boolean valid = StringUtils.isValidEmail(emailText.getText().toString());
        String emailErrorText = StringUtils.isEmpty(emailText.getText().toString()) ? emailEmptyError : emailFormatError;
        emailError.setText(emailErrorText);
        emailError.setVisibility(valid ? View.INVISIBLE :View.VISIBLE);
        return valid;
    }

    private boolean validateFirstName() {
        boolean valid = !StringUtils.isEmpty(firstNameText.getText());
        firstNameError.setVisibility(valid ? View.INVISIBLE :View.VISIBLE);
        return valid;
    }

    private boolean validateLastName() {
        boolean valid = !StringUtils.isEmpty(lastNameText.getText());
        lastNameError.setVisibility(valid ? View.INVISIBLE :View.VISIBLE);;
        return valid;
    }

    @Override
    public void onActionButtonClick() {
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.register_button)
    public void onRegisterButtonClick() {
        if(validate()) {
            accountManager.checkEmailValid(emailText.getText().toString(), this, new EmailAvailabilityListener() {
                @Override
                public void onEmailAvailable() {
                    if(validatePasswordFields()) {
                        continueToPreferences();
                    }
                }

                @Override
                public void onEmailUnavailable() {
                    hideKeyboard();
                    showErrorSnackbar(R.string.register_email_exists_error);
                }

                @Override
                public void onEmailInvalid() {
                    hideKeyboard();
                    showErrorSnackbar(R.string.email_invalid_error);
                }

                @Override
                public void onFailure() {
                    hideKeyboard();
                    showErrorSnackbar();
                }
            });
        }
    }

    @OnFocusChange(R.id.register_layout)
    public void onFocusChange(boolean hasFocus) {
        if(hasFocus) {
            hideKeyboard();
        }
    }

    @OnEditorAction(R.id.retype_password_input)
    public boolean onEditorAction(int id) {
        if (id == EditorInfo.IME_ACTION_DONE) {
            retypePasswordInput.clearFocus();
            hideKeyboard();
            return true;
        }
        return false;
    }
}