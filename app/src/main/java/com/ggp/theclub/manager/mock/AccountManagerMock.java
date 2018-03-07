package com.ggp.theclub.manager.mock;

import android.app.Activity;

import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.User;

import lombok.Getter;
import lombok.Setter;
import rx.Observable;

public class AccountManagerMock implements AccountManager {
    private final String VALID_EMAIL = "valid@example.com";
    private final String INVALID_EMAIL = "invalid@example.com";
    private final String VALID_REG_TOKEN = "VALID_REG_TOKEN";
    private final String INVALID_REG_TOKEN = "INVALID_REG_TOKEN";
    private final String DUPLICATE_EMAIL = "duplicate@example.com";

    @Getter @Setter private User currentUser = new User();

    public boolean shouldHandlePermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return true;
    }

    public void loginWithProvider(String provider, Activity activity, AccountListener accountListener) {
        User mockProviderUser = new User();
        mockProviderUser.setLoginProvider(provider);
        mockProviderUser.setRegToken(VALID_REG_TOKEN);
        accountListener.onSuccess(null);
    }

    public void logout(GigyaResponseListener gigyaResponseListener) {
        setCurrentUser(new User());
        gigyaResponseListener.onSuccess(null);
    }

    public void checkEmailValid(String email, Activity activity, EmailAvailabilityListener emailAvailabilityListener) {
        if (email.equalsIgnoreCase(DUPLICATE_EMAIL)) {
            emailAvailabilityListener.onEmailAvailable();
        } else if (email.equalsIgnoreCase(INVALID_EMAIL)){
            emailAvailabilityListener.onEmailInvalid();
        } else {
            emailAvailabilityListener.onEmailAvailable();
        }
    }

    public void registerEmail(User user, String password, GigyaResponseListener responseListener) {
        User responseObject = new User();
        user.setRegToken(responseObject.getRegToken());
        fetchAccountInfo(() -> responseListener.onSuccess(null));
    }

    public void loginWithEmail(String email, String password, AccountLoginListener accountLoginListener) {
        fetchAccountInfo(() -> accountLoginListener.onSuccess(null));
    }
    
    public void resetPassword(String emailAddress, ResetPasswordListener resetPasswordListener) {
        resetPasswordListener.onSuccess(null);
    }

    public void register(User user, GigyaResponseListener gigyaResponseListener) {
        gigyaResponseListener.onSuccess(null);
    }

    public void savePassword(String currentPassword, String newPassword, AccountLoginListener accountLoginListener) {
        accountLoginListener.onSuccess(null);
    }

    public void saveAccountInfo(User user, GigyaResponseListener gigyaResponseListener) {
        gigyaResponseListener.onSuccess(null);
    }

    public boolean isLoggedIn() {
        return VALID_EMAIL.equals(currentUser.getEmail());
    }

    public void fetchAccountInfo(AccountInfoListener accountInfoListener) {
        User mockUser = new User();
        mockUser.setEmail(VALID_EMAIL);
        mockUser.setBirthYear(1988);
        mockUser.setBirthMonth(2);
        mockUser.setBirthDay(3);
        mockUser.setEmailSubscribed(true);
        mockUser.setSmsSubscribed(true);
        mockUser.setMobilePhone("5555555555");

        setCurrentUser(mockUser);
        accountInfoListener.onReturn();
    }

    @Override
    public Observable fetchAccountInfo() {
        return Observable.just(null);
    }

    public void checkConflictingAccounts(String regToken, EmailAvailabilityListener emailAvailabilityListener) {
        if (regToken.equals(INVALID_REG_TOKEN)) {
            emailAvailabilityListener.onEmailUnavailable();
        } else {
            emailAvailabilityListener.onEmailAvailable();
        }
    }
    @Override
    public void addFavoriteTenant(Tenant tenant) {
        currentUser.addFavoriteTenant(tenant);
    }

    @Override
    public void removeFavoriteTenant(Tenant tenant) {
        currentUser.removeFavoriteTenant(tenant);
    }
}
