package com.ggp.theclub.manager;

import android.app.Activity;

import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.User;
import com.gigya.socialize.GSObject;

import rx.Observable;

public interface AccountManager {
     String PROVIDER_FACEBOOK = "facebook";
     String PROVIDER_GOOGLE = "googleplus";

     User getCurrentUser();
     void setCurrentUser(User user);

     boolean shouldHandlePermissionsResult(int requestCode, String[] permissions, int[] grantResults);
     void loginWithProvider(String provider, Activity activity, AccountListener accountListener);
     void logout(GigyaResponseListener gigyaResponseListener);

    /**
     * This method checks if the email is a duplicate in gigya. It also checks if the email is valid through Briteverify
     */
    void checkEmailValid(String email, Activity activity, EmailAvailabilityListener emailAvailabilityListener);
    void registerEmail(User user, String password, GigyaResponseListener responseListener);
    void loginWithEmail(String email, String password, AccountLoginListener accountLoginListener);

    void resetPassword(String emailAddress, ResetPasswordListener resetPasswordListener);

    void register(User user, GigyaResponseListener gigyaResponseListener);
    void savePassword(String currentPassword, String newPassword, AccountLoginListener accountLoginListener);

    void saveAccountInfo(User user, GigyaResponseListener gigyaResponseListener);
    boolean isLoggedIn();
    void fetchAccountInfo(AccountInfoListener accountInfoListener);
    Observable fetchAccountInfo();
    void checkConflictingAccounts(String regToken, EmailAvailabilityListener emailAvailabilityListener);

    void addFavoriteTenant(Tenant tenant);
    void removeFavoriteTenant(Tenant tenant);

    interface AccountInfoListener {
        void onReturn();
    }

    interface GigyaResponseListener {
        void onSuccess(GSObject data);
        void onFailure(GSObject data, String errorMessage);
    }

    interface AccountListener extends GigyaResponseListener {
        void onRegistrationRequired(GSObject data);
    }

    interface AccountLoginListener extends GigyaResponseListener {
        void onInvalidCredentials();
    }

    interface ResetPasswordListener extends GigyaResponseListener {
        void onEmailNotFound();
    }

    interface EmailAvailabilityListener {
        void onEmailAvailable();
        void onEmailUnavailable();
        void onEmailInvalid();
        void onFailure();
    }
}
