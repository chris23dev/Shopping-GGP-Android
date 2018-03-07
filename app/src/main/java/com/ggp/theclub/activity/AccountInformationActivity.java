package com.ggp.theclub.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AccountManager.EmailAvailabilityListener;
import com.ggp.theclub.manager.AccountManager.GigyaResponseListener;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.User;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.GigyaUtils;
import com.ggp.theclub.util.StringUtils;
import com.gigya.socialize.GSObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class AccountInformationActivity extends BaseActivity {
    @BindString(R.string.first_name_error) String firstNameError;
    @BindString(R.string.last_name_error) String lastNameError;
    @BindString(R.string.zip_code_error) String zipCodeError;
    @BindString(R.string.delete_account_instructions) String deleteAccountInstructions;
    @BindString(R.string.email_error) String emailEmptyError;
    @BindString(R.string.email_format_error) String emailFormatError;
    @Bind(R.id.first_name_layout) TextInputLayout firstNameLayout;
    @Bind(R.id.first_name_view) EditText firstNameView;
    @Bind(R.id.last_name_layout) TextInputLayout lastNameLayout;
    @Bind(R.id.last_name_view) EditText lastNameView;
    @Bind(R.id.gender_view) EditText genderView;
    @Bind(R.id.birthday_view) EditText birthdayView;
    @Bind(R.id.birthday_clear_button) TextView birthdayClearButton;
    @Bind(R.id.zip_code_layout) TextInputLayout zipCodeLayout;
    @Bind(R.id.zip_code_view) EditText zipCodeView;
    @Bind(R.id.email_layout) TextInputLayout emailLayout;
    @Bind(R.id.email_view) EditText emailView;
    @Bind(R.id.email_info) TextView emailInfoView;
    @Bind(R.id.disclaimer_view) TextView disclaimerView;
    @Bind(R.id.instructions_view) TextView instructionsView;
    @Bind(R.id.delete_account_button_text) TextView deleteAccountBtn;
    private final int ZIP_CODE_LENGTH = 5;
    private final SimpleDateFormat BIRTHDAY_FORMAT = new SimpleDateFormat("MMMM d, yyyy");
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();
    private User user = accountManager.getCurrentUser().clone();
    private AlertDialog genderPicker;
    private DatePickerDialog birthdayPicker;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, AccountInformationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_information_activity);
        emailView.setHint(getString(R.string.email_input));
        deleteAccountBtn.setText(getString(R.string.delete_account_button_text));
        disclaimerView.setText(getString(R.string.account_information_disclaimer));
        firstNameView.setHint(getString(R.string.first_name_hint));
        lastNameView.setHint(getString(R.string.last_name_hint));
        genderView.setHint(getString(R.string.gender_hint));
        birthdayView.setHint(getString(R.string.birthday_hint));
        zipCodeView.setHint(getString(R.string.zip_code_hint));
        emailInfoView.setText(getString(R.string.email_additional_info));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.account_information_title);
        enableBackButton();
        setTextActionButton(R.string.save_preferences);
        textActionButton.setVisibility(View.GONE);

        firstNameView.setText(user.getFirstName());
        lastNameView.setText(user.getLastName());
        setupGenderPicker();
        setupBirthdayPicker();
        zipCodeView.setText(user.getZip());
        disclaimerView.setVisibility(user.isSocialLogin() ? View.VISIBLE : View.GONE);
        instructionsView.setText(String.format(deleteAccountInstructions, mallManager.getMall().getName()));
        emailView.setText(user.getEmail());
        boolean isEmailLogin = user.getLoginProvider().equals(GigyaUtils.LoginProvider.EMAIL);
        emailInfoView.setVisibility(isEmailLogin ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (textActionButton.isShown()) {
            AlertUtils.showDiscardDialog(this);
        } else {
            super.onBackPressed();
        }
    }

    private void saveInfoIfEmailAvailable() {
        accountManager.checkEmailValid(user.getEmail(), this, new EmailAvailabilityListener() {
            @Override
            public void onEmailAvailable() {
                saveInfo();
            }

            @Override
            public void onEmailUnavailable() {
                Snackbar.make(layoutView, R.string.account_info_email_exists_error, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onEmailInvalid() {
                Snackbar.make(layoutView, R.string.email_invalid_error, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                showErrorSnackbar();
            }
        });
    }

    private void saveInfo() {
        trackAnalytics();
        AnimationUtils.exitReveal(textActionButton);
        accountManager.saveAccountInfo(user, new GigyaResponseListener() {
            @Override
            public void onSuccess(GSObject data) {
                Snackbar.make(layoutView, R.string.user_save_success, Snackbar.LENGTH_LONG).show();
                accountManager.setCurrentUser(user);
            }

            @Override
            public void onFailure(GSObject data, String errorMessage) {
                AnimationUtils.enterReveal(textActionButton);
                Snackbar.make(layoutView, R.string.user_save_failure, Snackbar.LENGTH_LONG).show();
                Log.e(getClass().getSimpleName(), errorMessage);
            }
        });
    }

    private void setupGenderPicker() {
        genderView.setText(GigyaUtils.getGenderValueByKey(user.getGender()));
        genderPicker = new AlertDialog.Builder(this).setItems(R.array.account_gender_values, (dialog, which) -> {
            user.setGender(GigyaUtils.getAccountGenderKeys().get(which));
            genderView.setText(GigyaUtils.getAccountGenderValues().get(which));
            AnimationUtils.enterReveal(textActionButton);
        }).create();
    }

    private void setupBirthdayPicker() {
        Calendar birthday = Calendar.getInstance();
        if (user.birthdayExists()) {
            birthday = user.getCurrentBirthday();
            birthdayView.setText(BIRTHDAY_FORMAT.format(birthday.getTime()));
            birthdayClearButton.setVisibility(View.VISIBLE);
        }
        birthdayPicker = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            user.setBirthDay(dayOfMonth);
            user.setBirthMonth(monthOfYear);
            user.setBirthYear(year);
            birthdayView.setText(BIRTHDAY_FORMAT.format(user.getCurrentBirthday().getTime()));
            AnimationUtils.enterReveal(birthdayClearButton);
        }, birthday.get(Calendar.YEAR), birthday.get(Calendar.MONTH), birthday.get(Calendar.DAY_OF_MONTH));
    }

    private boolean validateFirstName() {
        boolean valid = !StringUtils.isEmpty(firstNameView.getText());
        if (valid) {
            user.setFirstName(firstNameView.getText().toString());
        }
        firstNameLayout.setErrorEnabled(!valid);
        firstNameLayout.setError(valid ? null : firstNameError);

        return valid;
    }

    private boolean validateLastName() {
        boolean valid = !StringUtils.isEmpty(lastNameView.getText());
        if (valid) {
            user.setLastName(lastNameView.getText().toString());
        }
        lastNameLayout.setErrorEnabled(!valid);
        lastNameLayout.setError(valid ? null : lastNameError);
        return valid;
    }

    private boolean validateZipCode() {
        boolean valid = StringUtils.isEmpty(zipCodeView.getText()) || zipCodeView.getText().length() == ZIP_CODE_LENGTH;
        if (valid) {
            user.setZip(zipCodeView.getText().toString());
        }
        zipCodeLayout.setErrorEnabled(!valid);
        zipCodeLayout.setError(valid ? null : zipCodeError);
        return valid;
    }

    private boolean validateEmail() {
        String email = emailView.getText().toString();
        boolean valid = StringUtils.isValidEmail(email);
        if (valid) {
            user.setEmail(email);
        }
        emailLayout.setErrorEnabled(!valid);
        String emailErrorText = StringUtils.isEmpty(email) ? emailEmptyError : emailFormatError;
        emailLayout.setError(valid ? null : emailErrorText);
        return valid;
    }

    private boolean validateFields() {
        boolean validFirstName = validateFirstName();
        boolean validLastName = validateLastName();
        boolean validZipCode = validateZipCode();
        boolean validEmail = validateEmail();
        return validFirstName && validLastName && validEmail && validZipCode;
    }

    private void trackAnalytics() {
        analyticsManager.trackAction(AnalyticsManager.Actions.MyInfoSave);
    }

    @Override
    public void onActionButtonClick() {
        hideKeyboard();
        if (validateFields()) {
            //don't check if email is available unless the user changed it, since it will be unavailable otherwise
            if(!user.getEmail().equalsIgnoreCase(accountManager.getCurrentUser().getEmail())) {
                saveInfoIfEmailAvailable();
            } else {
                saveInfo();
            }
        }
    }

    @OnClick(R.id.birthday_clear_button)
    public void onBirthdayClearButtonClick() {
        birthdayView.setText(null);
        user.setBirthDay(0);
        user.setBirthMonth(0);
        user.setBirthYear(0);
        AnimationUtils.exitReveal(birthdayClearButton);
    }

    @OnClick(R.id.gender_view)
    public void onGenderViewClick() {
        genderPicker.show();
    }

    @OnClick(R.id.birthday_view)
    public void onBirthdayViewClick() {
        birthdayPicker.show();
    }

    @OnTextChanged({R.id.first_name_view, R.id.last_name_view, R.id.birthday_view, R.id.zip_code_view, R.id.email_view})
    public void onTextChange() {
        if (layoutView.isShown()) {
            AnimationUtils.enterReveal(textActionButton);
        }
    }
}