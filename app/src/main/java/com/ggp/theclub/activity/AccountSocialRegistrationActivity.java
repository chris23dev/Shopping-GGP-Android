package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager.EmailAvailabilityListener;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.StringUtils;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class AccountSocialRegistrationActivity extends BaseActivity {
    @BindString(R.string.email_error) String emailError;
    @BindString(R.string.first_name_error) String firstNameError;
    @BindString(R.string.last_name_error) String lastNameError;
    @Bind(R.id.register_header) TextView registerHeaderView;
    @Bind(R.id.register_button) Button registerBtn;
    @Bind(R.id.account_description) TextView accountDescriptionView;
    @Bind(R.id.email_wrapper) TextInputLayout emailWrapper;
    @Bind(R.id.first_name_wrapper) TextInputLayout firstNameWrapper;
    @Bind(R.id.last_name_wrapper) TextInputLayout lastNameWrapper;
    @Bind(R.id.email) EditText emailText;
    @Bind(R.id.first_name) EditText firstNameText;
    @Bind(R.id.last_name) EditText lastNameText;
    @Bind(R.id.required_field) TextView requiredField;
    protected String registrationType;
    protected User user;
    private String defaultEmail;

    public static Intent buildIntent(Context context, String registrationType) {
        return buildIntent(context, AccountSocialRegistrationActivity.class, registrationType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registrationType = getIntentExtra(String.class);
        user = accountManager.getCurrentUser();
        defaultEmail = user.getEmail();
        setContentView(R.layout.account_social_registration_activity);
        registerHeaderView.setText(getString(R.string.register_header));
        accountDescriptionView.setText(getString(R.string.register_description));
        emailText.setHint(getString(R.string.email_register));
        firstNameText.setHint(getString(R.string.first_name));
        lastNameText.setHint(getString(R.string.last_name));
        requiredField.setHint(getString(R.string.required_field));
        registerBtn.setText(getString(R.string.register_button_text));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.account_title);
        setTextActionButton(R.string.cancel_text);
        if(user != null) {
            emailText.setText(user.getEmail());
            firstNameText.setText(user.getFirstName());
            lastNameText.setText(user.getLastName());
        }
    }

    private boolean validate() {
        boolean emailValid = validateEmail();
        boolean firstNameValid = validateFirstName();
        boolean lastNameValid = validateLastName();

        return emailValid && firstNameValid && lastNameValid;
    }

    private boolean validateEmail() {
        boolean valid = StringUtils.isValidEmail(emailText.getText().toString());
        emailWrapper.setErrorEnabled(!valid);
        emailWrapper.setError(valid ? null : emailError);
        return valid;
    }

    private boolean validateFirstName() {
        boolean valid = !StringUtils.isEmpty(firstNameText.getText());
        firstNameWrapper.setErrorEnabled(!valid);
        firstNameWrapper.setError(valid ? null : firstNameError);
        return valid;
    }

    private boolean validateLastName() {
        boolean valid = !StringUtils.isEmpty(lastNameText.getText());
        lastNameWrapper.setErrorEnabled(!valid);
        lastNameWrapper.setError(valid ? null : lastNameError);
        return valid;
    }

    private void proceedIfEmailAvailable() {
        EmailAvailabilityListener emailAvailabilityListener = new EmailAvailabilityListener() {
            @Override
            public void onEmailAvailable() {
                proceedToPreferences();
            }

            @Override
            public void onEmailUnavailable() {
                Snackbar.make(layoutView, R.string.register_email_exists_error, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onEmailInvalid() {
                Snackbar.make(layoutView, R.string.email_invalid_error, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                showErrorSnackbar();
            }
        };

        //If they used the default email from the provider, check for a conflicting account, since the checkEmailExists is guaranteed to fail
        String newEmail = emailText.getText().toString();
        if (!StringUtils.isEmpty(defaultEmail) && defaultEmail.equalsIgnoreCase(newEmail)) {
            accountManager.checkConflictingAccounts(user.getRegToken(), emailAvailabilityListener);
        } else {
            accountManager.checkEmailValid(newEmail, this, emailAvailabilityListener);
        }
    }

    protected void proceedToPreferences() {
        startActivityForResult(AccountSocialRegistrationPreferencesActivity.buildIntent(AccountSocialRegistrationActivity.this, user, registrationType), RequestCode.FINISH_REQUEST_CODE);
    }

    @Override
    public void onActionButtonClick() {
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.register_button)
    public void onRegisterButtonClick() {
        if(validate()) {
            proceedIfEmailAvailable();
        }
    }

    @OnFocusChange({R.id.register_layout})
    public void onFocusChange(boolean hasFocus) {
        if(hasFocus) {
            hideKeyboard();
        }
    }
}