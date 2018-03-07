package com.ggp.theclub.util;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.model.User;
import com.gigya.socialize.GSObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;

public class GigyaUtils {
    public static String KEY_UID = "UID";
    public static String KEY_PROFILE = "profile";
    public static String KEY_ACCOUNT_DATA = "data";
    public static String KEY_ANDROID = "android";
    public static String KEY_EMAIL = "email";
    public static String KEY_LOGIN_PROVIDER = "loginProvider";
    public static String KEY_EMAIL_PROVIDER = "site";
    public static String KEY_FIRST_NAME = "firstName";
    public static String KEY_LAST_NAME = "lastName";
    public static String KEY_PHOTO_URL = "photoURL";
    public static String KEY_THUMBNAIL_URL = "thumbnailURL";
    public static String KEY_GENDER = "gender";
    public static String KEY_BIRTH_DAY = "birthDay";
    public static String KEY_BIRTH_MONTH = "birthMonth";
    public static String KEY_BIRTH_YEAR = "birthYear";
    public static String KEY_ZIP_CODE = "zip";
    public static String KEY_REG_TOKEN = "regToken";
    public static String KEY_REG_SOURCE = "regSource";
    public static String KEY_EMAIL_SUBSCRIBE = "emailSubscribed";
    public static String KEY_SMS_SUBSCRIBE = "smsSubscribed";
    public static String KEY_MOBILE_PHONE = "mobilePhone";
    public static String KEY_ORIGIN_MALL_ID = "originMallId";
    public static String KEY_ORIGIN_MALL_NAME = "originMallName";
    public static String KEY_MALL_ID_1 = "MallId1";
    public static String KEY_MALL_ID_2 = "MallId2";
    public static String KEY_MALL_ID_3 = "MallId3";
    public static String KEY_MALL_ID_4 = "MallId4";
    public static String KEY_MALL_ID_5 = "MallId5";
    public static String KEY_TERMS = "terms";
    public static String KEY_AVAILABLE = "isAvailable";
    public static String KEY_FAVORITE_TENANTS = "favorite_tenants";
    public static String KEY_PLACEWISE_RETAILER_ID = "placewise_retailer_id";
    public static String KEY_STORE_ID = "store_id";
    public static String KEY_DISPLAY_NAME = "displayname";
    public class LoginProvider {
        public static final String EMAIL = "site";
    }
    private static boolean TERMS_VALUE = true;
    @Getter private static List<String> accountGenderKeys = new ArrayList<>(Arrays.asList("m", "f", "u"));
    @Getter private static List<String> accountGenderValues = Arrays.asList(MallApplication.getApp().getStringArray(R.array.account_gender_values));

    public static GSObject buildProfileRequestObject(User account) {
        GSObject profile = new GSObject();
        profile.put(KEY_EMAIL, account.getEmail());
        profile.put(KEY_FIRST_NAME, account.getFirstName());
        profile.put(KEY_LAST_NAME, account.getLastName());
        if (!StringUtils.isEmpty(account.getGender())) {
            profile.put(KEY_GENDER, account.getGender());
        }
        profile.put(KEY_BIRTH_DAY, account.getBirthDay() > 0 ? account.getBirthDay() : null);
        profile.put(KEY_BIRTH_MONTH, account.getBirthMonth() > 0 ? account.getBirthMonth() : null);
        profile.put(KEY_BIRTH_YEAR, account.getBirthYear() > 0 ? account.getBirthYear() : null);
        profile.put(KEY_ZIP_CODE, account.getZip());
        return profile;
    }

    public static GSObject buildRegisterDataObject(User user) {
        GSObject data = new GSObject();
        data.put(KEY_ORIGIN_MALL_ID, Integer.toString(MallApplication.getApp().getMallManager().getMall().getId()));
        data.put(KEY_ORIGIN_MALL_NAME, MallApplication.getApp().getMallManager().getMall().getName());
        if (StringUtils.isEmpty(user.getMallId1())) {
            data.put(KEY_MALL_ID_1, Integer.toString(MallApplication.getApp().getMallManager().getMall().getId()));
        }
        data.put(KEY_TERMS, TERMS_VALUE);
        data.put(KEY_EMAIL_SUBSCRIBE, user.isEmailSubscribed());
        data.put(KEY_SMS_SUBSCRIBE, user.isSmsSubscribed());
        data.put(KEY_MOBILE_PHONE, user.getMobilePhone());
        return data;
    }

    public static GSObject buildSaveAccountObject(User user) {
        GSObject data = new GSObject();
        data.put(KEY_EMAIL_SUBSCRIBE, user.isEmailSubscribed());
        data.put(KEY_SMS_SUBSCRIBE, user.isSmsSubscribed());
        data.put(KEY_MOBILE_PHONE, user.getMobilePhone());
        data.put(KEY_MALL_ID_1, user.getMallId1());
        data.put(KEY_MALL_ID_2, user.getMallId2());
        data.put(KEY_MALL_ID_3, user.getMallId3());
        data.put(KEY_MALL_ID_4, user.getMallId4());
        data.put(KEY_MALL_ID_5, user.getMallId5());
        return data;
    }

    public static GSObject buildFavoriteTenantsObject(HashSet<Integer> favoriteList) {
        GSObject data = new GSObject();
        data.put(KEY_FAVORITE_TENANTS, favoriteList);
        return data;
    }

    public static String getGenderValueByKey(String genderKey) {
        return !StringUtils.isEmpty(genderKey) ? accountGenderValues.get(accountGenderKeys.indexOf(genderKey)) : null;
    }
}