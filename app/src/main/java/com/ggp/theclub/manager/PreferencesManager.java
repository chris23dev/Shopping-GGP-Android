package com.ggp.theclub.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import lombok.Getter;

public class PreferencesManager {
    private final String LOG_TAG = PreferencesManager.class.getSimpleName();

    public static final String PREFERENCES_TAG = "GGP_PREFERENCES";
    public static final String PREFERENCES_UID = "UID";
    public static final String FEEDBACK_EVENT_COUNT = "FEEDBACK_EVENT_COUNT";
    public static final String FEEDBACK_COUNT_RESET_VERSION_CODE = "FEEDBACK_COUNT_RESET_VERSION_CODE";
    public static final String PARKING_REMINDER = "PARKING_REMINDER";
    public static final String HAS_SHOWN_SETTINGS_DIALOG = "HAS_SHOWN_SETTINGS_DIALOG";
    public static final String HAS_PREVIEWED_SWIPE_DIRECTORY = "HAS_PREVIEWED_SWIPE_DIRECTORY";
    public static final String ONBOARDING_COMPLETE = "ONBOARDING_COMPLETE";
    public static final String LANGUAGE = "LANGUAGE";

    @Getter private SharedPreferences sharedPreferences;
    private Gson gson = new GsonBuilder().serializeNulls().create();
    private static PreferencesManager preferencesManager = new PreferencesManager();

    public static PreferencesManager getInstance() {
        return preferencesManager;
    }

    public PreferencesManager() {
        MallApplication app = MallApplication.getApp();
        sharedPreferences = app.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
    }

    public String getUserId() {
        return getSharedPreferences().getString(PREFERENCES_UID, "");
    }

    public void setUserId(String userId) {
        getSharedPreferences().edit().putString(PREFERENCES_UID, userId).commit();
    }

    public void saveObject(String key, Object obj) {
        String json = gson.toJson(obj);
        getSharedPreferences().edit().putString(key, json).commit();
    }

    public void removeObject(String key) {
        getSharedPreferences().edit().remove(key).commit();
    }

    public<T> T getObject(String key, Class<T> clazz) {
        String objectJson = getSharedPreferences().getString(key, null);
        return StringUtils.isEmpty(objectJson) ? null :gson.fromJson(objectJson, clazz);
    }

    public int getFeedbackEventCount() {
        return getSharedPreferences().getInt(FEEDBACK_EVENT_COUNT, 0);
    }

    public void setFeedbackEventCount(int count) {
        getSharedPreferences().edit().putInt(FEEDBACK_EVENT_COUNT, count).commit();
    }

    public int getFeedbackCountResetVersionCode() {
        return getSharedPreferences().getInt(FEEDBACK_COUNT_RESET_VERSION_CODE, 0);
    }

    public void setFeedbackCountResetVersionCode(int feedbackCountResetVersionCode) {
        getSharedPreferences().edit().putInt(FEEDBACK_COUNT_RESET_VERSION_CODE, feedbackCountResetVersionCode).commit();
    }

    public boolean getBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public void setBoolean(String key, Boolean flag) {
        getSharedPreferences().edit().putBoolean(key, flag).commit();
    }

    public boolean hasShownSettingsDialog() {
        return getSharedPreferences().getBoolean(HAS_SHOWN_SETTINGS_DIALOG, false);
    }

    public void setShownSettingsDialog(boolean directedUserToLocationSettings) {
        getSharedPreferences().edit().putBoolean(HAS_SHOWN_SETTINGS_DIALOG, directedUserToLocationSettings).commit();
    }

    public void setOnboardingComplete(boolean finished) {
        getSharedPreferences().edit().putBoolean(ONBOARDING_COMPLETE, finished).commit();
    }

    public boolean isOnboardingComplete() {
        return getSharedPreferences().getBoolean(ONBOARDING_COMPLETE, false);
    }

    public void setCurrentLanguage(String value){
        getSharedPreferences().edit().putString(LANGUAGE, value).commit();
    }

    public String getCurrentLanguage(){
        return getSharedPreferences().getString(LANGUAGE, "en");
    }
}