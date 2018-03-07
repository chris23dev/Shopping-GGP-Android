package com.ggp.theclub.customlocale;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ggp.theclub.MallApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by john.curtis on 4/17/17.
 */
public class LocaleStorageImpl implements LocaleStorage {
    private SharedPreferences mPreferences;

    public LocaleStorageImpl(@NonNull String languageCode) {
        init(languageCode);
    }

    private void init(@NonNull String languageCode) {
        mPreferences = createSharedPreferences("language_"+ languageCode);
    }

    @Override
    public String getValue(@NonNull String key) {
        return getValueInternal(key);
    }

    @Override
    public String[] getValueArray(@NonNull String key) {
        //TODO Implement
        throw new IllegalStateException("Is not implemented yet!");
    }

    @Nullable
    private String getValueInternal(String key) {
        return mPreferences.getString(key, null);
    }

    @Override
    public boolean setValue(@NonNull String key, String value) {
        return setValueInternal(mPreferences, key, value);
    }

    @Override
    public int setValues(@NonNull HashMap<String, String> keyValueMap) {
        return setValuesInternal(mPreferences, keyValueMap);
    }

    @Override
    public int setArrayValues(@NonNull HashMap<String, List<String>> keyValueMap) {
        //TODO Implement
        throw new IllegalStateException("Is not implemented yet!");
    }


    private boolean setValueInternal(SharedPreferences preferences, String key, String value) {
        preferences.edit().putString(key, value).apply();
        return true;
    }

    private int setValuesInternal(SharedPreferences preferences, @NonNull HashMap<String, String> keyValueMap) {
        SharedPreferences.Editor edit = preferences.edit();
        Set<String> strings = keyValueMap.keySet();

        int added = 0;

        for (String key : strings) {
            edit.putString(key, keyValueMap.get(key));
            added++;
        }

        edit.apply();

        return added;
    }

    @NonNull
    private SharedPreferences createSharedPreferences(String fileName) {
        MallApplication app = MallApplication.getApp();
        return app.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }


    public void setLocale(@NonNull String languageCode) {
        init(languageCode);
    }
}
