package com.ggp.theclub.widget;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;

import com.ggp.theclub.BuildConfig;
import com.ggp.theclub.customlocale.LocaleMatcherImpl;
import com.ggp.theclub.customlocale.LocaleStorageImpl;
import com.ggp.theclub.customlocale.LocaleUtils;

import java.util.Locale;

public class CustomLocalisationResources extends Resources {
    private final LocaleStorageImpl localeStorage;
    private LocaleMatcherImpl mMatcher;

    public static boolean shouldBeUsed() {
        return BuildConfig.CUSTOM_LOCALISATION;
    }

    public CustomLocalisationResources(@NonNull Resources res) {
        super(getAssets(res), res.getDisplayMetrics(), getConfiguration(res));
        mMatcher = new LocaleMatcherImpl();
        localeStorage = new LocaleStorageImpl(getLanguageCodeFromPreferences());
    }

    private static Configuration getConfiguration(@NonNull Resources res) {
        Configuration configuration = res.getConfiguration();
        Locale locale = new Locale(getLanguageCodeFromPreferences());
        Locale.setDefault(locale);
        return configuration;
    }

    private static AssetManager getAssets(@NonNull Resources res) {
        return res.getAssets();
    }

    @Override
    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        super.updateConfiguration(config, metrics);
        if(localeStorage != null)
            localeStorage.setLocale(getLanguageCodeFromPreferences());
    }

    @NonNull
    @Override
    public String getString(@StringRes int id) throws NotFoundException {
        String value = getStringInternal(id);

        if (value == null) {
            return super.getString(id);
        }

        return value;
    }

    @NonNull
    @Override
    public String getString(@StringRes int id, Object... formatArgs) throws NotFoundException {
        String value = getStringInternal(id);

        if (value == null) {
            return super.getString(id, formatArgs);
        }

        return value;
    }

    @NonNull
    @Override
    public String[] getStringArray(@ArrayRes int id) throws NotFoundException {
        String[] values = getStringArrayInternal(id);

        if (values == null) {
            return super.getStringArray(id);
        }

        return values;
    }

    @NonNull
    @Override
    public CharSequence getText(@StringRes int id) throws NotFoundException {
        String value = getStringInternal(id);

        if (value == null) {
            return super.getText(id);
        }

        return value;
    }

    @Override
    public CharSequence getText(@StringRes int id, CharSequence def) {
        String value = getStringInternal(id);

        if (value == null) {
            return super.getText(id, def);
        }

        return value;
    }

    @NonNull
    @Override
    public CharSequence[] getTextArray(@ArrayRes int id) throws NotFoundException {
        String[] values = getStringArrayInternal(id);

        if (values == null) {
            return super.getTextArray(id);
        }

        return values;
    }

    @Nullable
    private String[] getStringArrayInternal(@ArrayRes int id) {
        String key = mMatcher.getKeyArrayById(id);
        try {
            //TODO Implement. We don't support this method for now.
            return localeStorage.getValueArray(key);
        } catch (Exception e){
            return null;
        }
    }

    @Nullable
    private String getStringInternal(@StringRes int id) {
        String key = mMatcher.getKeyById(id);
        return localeStorage.getValue(key);
    }

    public void invalidateLocale() {
        Locale locale = new Locale(getLanguageCodeFromPreferences());

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        updateConfiguration(config, getDisplayMetrics());
    }

    @NonNull
    private static String getLanguageCodeFromPreferences() {
        return LocaleUtils.getCurrentLanguageCode();
    }
}