package com.ggp.theclub.manager;

import android.app.Activity;

import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.User;

import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by john.curtis on 8/11/17.
 */
public class MockAccountManagerImpl implements AccountManager {
    @Override
    public User getCurrentUser() {

        User user = new User();
//        user.setLoginProvider("");
//        user.setEmailSubscribed(false);
//        user.setSmsSubscribed(false);
//        user.setMobilePhone("");
//        user.setEmail("");
//        user.setFirstName("");
//        user.setLastName("");
//        user.setPhotoURL("");
//        user.setThumbnailURL("");
//        user.setGender("");
//        user.setBirthDay(-1);
//        user.setBirthMonth(-1);
//        user.setBirthYear(-1);
//        user.setZip("");
//        user.setRegToken("");
//        user.setOriginMallId("");
//        user.setMallId1("");
//        user.setMallId2("");
//        user.setMallId3("");
//        user.setMallId4("");
//        user.setMallId5("");
//        user.setFavorites(new HashSet<>());

        return user;
    }

    @Override
    public void setCurrentUser(User user) {

    }

    @Override
    public boolean shouldHandlePermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return true;
    }

    @Override
    public void loginWithProvider(String provider, Activity activity, AccountListener accountListener) {

    }

    @Override
    public void logout(GigyaResponseListener gigyaResponseListener) {

    }

    @Override
    public void checkEmailValid(String email, Activity activity, EmailAvailabilityListener emailAvailabilityListener) {

    }

    @Override
    public void registerEmail(User user, String password, GigyaResponseListener responseListener) {

    }

    @Override
    public void loginWithEmail(String email, String password, AccountLoginListener accountLoginListener) {

    }

    @Override
    public void resetPassword(String emailAddress, ResetPasswordListener resetPasswordListener) {

    }

    @Override
    public void register(User user, GigyaResponseListener gigyaResponseListener) {

    }

    @Override
    public void savePassword(String currentPassword, String newPassword, AccountLoginListener accountLoginListener) {

    }

    @Override
    public void saveAccountInfo(User user, GigyaResponseListener gigyaResponseListener) {

    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void fetchAccountInfo(AccountInfoListener accountInfoListener) {

    }

    @Override
    public Observable fetchAccountInfo() {
        return Observable.empty();
    }

    @Override
    public void checkConflictingAccounts(String regToken, EmailAvailabilityListener emailAvailabilityListener) {

    }

    @Override
    public void addFavoriteTenant(Tenant tenant) {

    }

    @Override
    public void removeFavoriteTenant(Tenant tenant) {

    }
}
