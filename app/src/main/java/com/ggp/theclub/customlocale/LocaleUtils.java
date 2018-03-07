package com.ggp.theclub.customlocale;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

import com.ggp.theclub.manager.PreferencesManager;

import java.util.Locale;

/**
 * Created by john.curtis on 4/20/17.
 */
public class LocaleUtils {

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getCurrentLocale(@NonNull Resources resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return resources.getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return resources.getConfiguration().locale;
        }
    }

    public static String getCurrentLanguageCode(){
        return PreferencesManager.getInstance().getCurrentLanguage();
    }

    public static String getDefaultLanguageCode(){
        return "en";
    }

    public static boolean isNowNextLanguageCode(String en) {
        return getCurrentLanguageCode().equals(en);
    }

    public static boolean isCurrentLanguageCode(String code) {
        return getCurrentLanguageCode().equals(code);
    }

    public static void setCurrentLanguage(String languageCode) {
        PreferencesManager.getInstance().setCurrentLanguage(languageCode);
    }
}
