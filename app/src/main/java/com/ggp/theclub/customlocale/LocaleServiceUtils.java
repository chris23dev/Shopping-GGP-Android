package com.ggp.theclub.customlocale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ggp.theclub.activity.ChooseLanguageActivity;

/**
 * Created by john.curtis on 4/18/17.
 */
public class LocaleServiceUtils {



    public static void checkLocale(Context context) {
        sendToService(context, LocaleService.CHECK_LOCALE_ACTION, null);
    }
    public static void checkMallLocale(Context context) {
        sendToService(context, LocaleService.CHECK_LOCALE_EXIST_ACTION, null);
    }

    public static void setUpDefault(Context context) {
        sendToService(context, LocaleService.SETUP_DEFAULT_LOCALE_ACTION, null);
    }

    private static void sendToService(Context context, String action, Bundle b) {
        Intent intent = new Intent(context, LocaleService.class);
        if (b != null)
            intent.putExtras(b);

        intent.setAction(action);
        context.startService(intent);
    }

    public static void checkLocale(ChooseLanguageActivity context, String languageCode) {
        Bundle b = new Bundle();
        b.putString(LocaleService.LANGUAGE_CODE, languageCode);
        sendToService(context, LocaleService.CHECK_LOCALE_ACTION, b);
    }
}
