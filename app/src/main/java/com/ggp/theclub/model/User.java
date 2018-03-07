package com.ggp.theclub.model;

import android.util.Log;

import com.ggp.theclub.util.GigyaUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantUtils;
import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import java8.util.Objects;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import lombok.Getter;
import lombok.Setter;

public class User {
    private static String LOG_TAG = User.class.getName();

    @Getter @Setter private String loginProvider;
    @Getter @Setter private boolean emailSubscribed;
    @Getter @Setter private boolean smsSubscribed;
    @Getter @Setter private String mobilePhone;
    @Getter @Setter private String email;
    @Getter @Setter private String firstName;
    @Getter @Setter private String lastName;
    @Getter @Setter private String photoURL;
    @Getter @Setter private String thumbnailURL;
    @Getter @Setter private String gender;
    @Getter @Setter private int birthDay;
    @Getter @Setter private int birthMonth;
    @Getter @Setter private int birthYear;
    @Getter @Setter private String zip;
    @Getter @Setter private String regToken;
    @Getter @Setter private String originMallId;
    @Getter @Setter private String mallId1;
    @Getter @Setter private String mallId2;
    @Getter @Setter private String mallId3;
    @Getter @Setter private String mallId4;
    @Getter @Setter private String mallId5;
    @Getter @Setter private HashSet<Integer> favorites = new HashSet<>();

    public User() {}

    public User(GSObject gsObject) {
        try {
            setRegToken(gsObject.getString(GigyaUtils.KEY_REG_TOKEN));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            setLoginProvider(gsObject.getString(GigyaUtils.KEY_LOGIN_PROVIDER));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        GSObject accountData = gsObject.getObject(GigyaUtils.KEY_ACCOUNT_DATA, new GSObject());
        setEmailSubscribed(accountData.getBool(GigyaUtils.KEY_EMAIL_SUBSCRIBE, false));
        setSmsSubscribed(accountData.getBool(GigyaUtils.KEY_SMS_SUBSCRIBE, false));
        setMobilePhone(accountData.getString(GigyaUtils.KEY_MOBILE_PHONE, null));
        setOriginMallId(accountData.getString(GigyaUtils.KEY_ORIGIN_MALL_ID, null));
        setMallId1(accountData.getString(GigyaUtils.KEY_MALL_ID_1, originMallId));
        setMallId2(accountData.getString(GigyaUtils.KEY_MALL_ID_2, null));
        setMallId3(accountData.getString(GigyaUtils.KEY_MALL_ID_3, null));
        setMallId4(accountData.getString(GigyaUtils.KEY_MALL_ID_4, null));
        setMallId5(accountData.getString(GigyaUtils.KEY_MALL_ID_5, null));

        try {
            HashSet<Integer> newFavorites = new HashSet<>();
            String favoritesString = accountData.getString(GigyaUtils.KEY_FAVORITE_TENANTS, null);
            if (!StringUtils.isEmpty(favoritesString)) {
                GSArray favoritesArray = new GSArray(favoritesString);
                for (int i = 0; i < favoritesArray.length(); i++) {
                    newFavorites.add(favoritesArray.getInt(i));
                }
                setFavorites(newFavorites);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        GSObject profileData = gsObject.getObject(GigyaUtils.KEY_PROFILE, new GSObject());
        setEmail(profileData.getString(GigyaUtils.KEY_EMAIL, null));
        setFirstName(profileData.getString(GigyaUtils.KEY_FIRST_NAME, null));
        setLastName(profileData.getString(GigyaUtils.KEY_LAST_NAME, null));
        setPhotoURL(profileData.getString(GigyaUtils.KEY_PHOTO_URL, null));
        setThumbnailURL(profileData.getString(GigyaUtils.KEY_THUMBNAIL_URL, null));
        setGender(profileData.getString(GigyaUtils.KEY_GENDER, null));
        setBirthDay(profileData.getInt(GigyaUtils.KEY_BIRTH_DAY, 0));
        setBirthMonth(profileData.getInt(GigyaUtils.KEY_BIRTH_MONTH, 0));
        setBirthYear(profileData.getInt(GigyaUtils.KEY_BIRTH_YEAR, 0));
        setZip(profileData.getString(GigyaUtils.KEY_ZIP_CODE, null));
    }

    public User clone() {
        User user = new User();
        user.setLoginProvider(loginProvider);
        user.setEmailSubscribed(emailSubscribed);
        user.setSmsSubscribed(smsSubscribed);
        user.setMobilePhone(mobilePhone);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhotoURL(photoURL);
        user.setThumbnailURL(thumbnailURL);
        user.setGender(gender);
        user.setBirthDay(birthDay);
        user.setBirthMonth(birthMonth);
        user.setBirthYear(birthYear);
        user.setZip(zip);
        user.setOriginMallId(originMallId);
        user.setMallId1(mallId1);
        user.setMallId2(mallId2);
        user.setMallId3(mallId3);
        user.setMallId4(mallId4);
        user.setMallId5(mallId5);
        return user;
    }

    public Calendar getCurrentBirthday() {
        Calendar birthday = Calendar.getInstance();
        birthday.set(birthYear, birthMonth, birthDay);
        return birthYear > 0 ? birthday : null;
    }

    public void addFavoriteTenant(Tenant tenant) {
        if(!TenantUtils.isFavoriteTenant(tenant, getFavorites())) {
            favorites.add(tenant.getPlaceWiseRetailerId());
        }
    }

    public void removeFavoriteTenant(Tenant tenant) {
        if(TenantUtils.isFavoriteTenant(tenant, getFavorites())) {
            favorites.remove(tenant.getPlaceWiseRetailerId());
        }
    }

    public boolean isSocialLogin() {
        return StringUtils.isEmpty(getLoginProvider()) || !getLoginProvider().equals(GigyaUtils.KEY_EMAIL_PROVIDER);
    }

    public boolean birthdayExists() {
        Calendar birthday = getCurrentBirthday();
        return birthday != null && getBirthDay() != 0 && getBirthMonth() != 0 && getBirthYear() != 0;
    }

    public List<String> getSubscribedMallIds() {
        List<String> subscriptions = new ArrayList<>(Arrays.asList(mallId1, mallId2, mallId3, mallId4, mallId5));
        return StreamSupport.stream(subscriptions).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void setSubscribedMallIds(List<String> subscribedMallIds) {
        if (subscribedMallIds.size() > 0) {
            setMallId1(subscribedMallIds.get(0));
        }
        if (subscribedMallIds.size() > 1) {
            setMallId2(subscribedMallIds.get(1));
        }
        if (subscribedMallIds.size() > 2) {
            setMallId3(subscribedMallIds.get(2));
        }
        if (subscribedMallIds.size() > 3) {
            setMallId4(subscribedMallIds.get(3));
        }
        if (subscribedMallIds.size() > 4) {
            setMallId5(subscribedMallIds.get(4));
        }
    }

    public boolean isSubscribedMall(int mallId) {
        return StreamSupport.stream(getSubscribedMallIds()).filter(subscribedMallId -> subscribedMallId.equals(String.valueOf(mallId))).count() == 1;
    }
}