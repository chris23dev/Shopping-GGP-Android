package com.ggp.theclub.customlocale;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.ggp.theclub.manager.PreferencesManager;

/**
 * Created by john.curtis on 4/17/17.
 */
public class LocaleInspectorImpl implements LocaleInspector {
    private static String IS_DEFAULT_CONFIGURED ="IS_DEFAULT_CONFIGURED";
    private static String LOADED_LOCALE ="LOADED_LOCALE";

    @Override
    public boolean isNeedLoad(String localRevision, String serverRevision) {
        return localRevision == null || !localRevision.equals(serverRevision);
//        return true;
    }

    @Override
    public boolean isDefaultConfigured() {
        return getSharedPreferences().getBoolean(IS_DEFAULT_CONFIGURED, false);
    }

    @Override
    public void recordConfiguredDefault(boolean success) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(IS_DEFAULT_CONFIGURED, success).apply();
    }

    @Override
    public String getRecordedRevisionByLocale(String languageCode) {
        return getSharedPreferences().getString(LOADED_LOCALE + languageCode, null);
    }

    @Override
    public void recordLoadedLocale(String languageCode, String serverVersion) {
        if(serverVersion == null){
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(LOADED_LOCALE + languageCode, serverVersion).apply();
    }

    @NonNull
    private SharedPreferences getSharedPreferences() {
        return PreferencesManager.getInstance().getSharedPreferences();
    }
}
