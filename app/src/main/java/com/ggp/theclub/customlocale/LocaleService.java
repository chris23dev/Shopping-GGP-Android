package com.ggp.theclub.customlocale;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.customlocale.gateway.Language;
import com.ggp.theclub.customlocale.gateway.LocaleParserImpl;
import com.ggp.theclub.customlocale.gateway.MallLanguages;
import com.ggp.theclub.customlocale.gateway.ServerLocaleGateway;
import com.ggp.theclub.customlocale.gateway.ServerLocaleGatewayImpl;
import com.ggp.theclub.manager.NetworkManager;
import com.ggp.theclub.manager.PreferencesManager;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by john.curtis on 4/17/17.
 */
public class LocaleService extends IntentService {
    private static final String TAG = LocaleService.class.getSimpleName();
    public static final String LANGUAGE_CODE = "LANGUAGE_CODE";

    public static String HAS_LANGUAGE = "HAS_LANGUAGE";
    public static String CHECK_LOCALE_ACTION = "CHECK_LOCALE_ACTION";
    public static String SETUP_DEFAULT_LOCALE_ACTION = "SETUP_DEFAULT_LOCALE_ACTION";
    public static String CHECK_LOCALE_EXIST_ACTION = "CHECK_LOCALE_EXIST_ACTION";

    private final ServerLocaleGateway mGateway;
    private final LocaleInspectorImpl mLocaleInspector;
    private final LocaleParserImpl mLocaleParser;

    public LocaleService() {
        super("LocaleService");
        mGateway = new ServerLocaleGatewayImpl();
        mLocaleInspector = new LocaleInspectorImpl();
        mLocaleParser = new LocaleParserImpl();
    }

    @NonNull
    private LocaleStorageImpl createLocaleStorage(String languageCode) {
        return new LocaleStorageImpl(languageCode);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (CHECK_LOCALE_ACTION.equals(action)) {
            String languageCode = intent.getStringExtra(LANGUAGE_CODE);
            if(languageCode == null){
                languageCode = getCurrentLanguageCode();
            }

            if(!NetworkManager.getInstance().isNetworkAvailable()){
                EventBus.getDefault().post(new LocaleUpdatedSkipEvent(getString(R.string.no_internet_connection), languageCode));
                return;
            }

            try {
                String localRevision = mLocaleInspector.getRecordedRevisionByLocale(languageCode);

                List<Language> languages = mGateway.loadLanguages();
                String currentLocaleUrl = getCurrentLocaleUrl(languages, languageCode);

                if(currentLocaleUrl == null){
                    //There is no language on server side for current local.
                    EventBus.getDefault().post(new LocaleUpdatedSkipEvent(getString(R.string.there_is_no_language), languageCode));
                    return;
                }

                String serverRevision = mGateway.requestServerLanguageRevision(currentLocaleUrl);

                if (mLocaleInspector.isNeedLoad(localRevision, serverRevision)) {
                    String result = mGateway.loadLocalisationFile(currentLocaleUrl);
                    HashMap<String, String> values = mLocaleParser.parseValues(result);
                    int saved = createLocaleStorage(languageCode).setValues(values);

                    if(saved == values.size()){
                        mLocaleInspector.recordLoadedLocale(languageCode, serverRevision);
                    }

                    EventBus.getDefault().post(new LocaleUpdatedEvent(languageCode));

                } else {
                    //Do nothing. We already have appropriate currentLocale file on our device.
                    EventBus.getDefault().post(new LocaleUpdatedEvent(languageCode));
                }

            } catch (Exception e) {
                Log.d(TAG, "Error update localisation from server", e);
                EventBus.getDefault().post(new ErrorLocaleUpdatedEvent());
            }
        } else if(CHECK_LOCALE_EXIST_ACTION.equals(action)) {
            try {
                int id = MallApplication.getApp().getMallManager().getMall().getId();
                HashMap<Integer, MallLanguages> serverRevision = mGateway.loadMallsLanguagesSupportList();
                Log.d(TAG, "malls with languages" + serverRevision);
                boolean contains = serverRevision.keySet().contains(id);

                hasTranslation(contains);

                if(needResetToDefaultLanguage(id, serverRevision, contains)){
                    resetLanguageCode();
                }
            } catch (Exception e) {
                Log.d(TAG, "Error get languages for malls.", e);
                EventBus.getDefault().post(new ErrorLocaleUpdatedEvent());
            }
        }
    }

    @NonNull
    private String getDefaultLanguageCode() {
        return LocaleUtils.getDefaultLanguageCode();
    }

    private String getCurrentLocaleUrl(List<Language> languages, String languageCode) {
        for(Language language: languages){
            if(languageCode.equals(language.code)){
                return language.url;
            }
        }

        return null;
    }

    private void hasTranslation(boolean contains) {
        PreferencesManager.getInstance().setBoolean(HAS_LANGUAGE, contains);
    }

    public void resetLanguageCode() {
        LocaleUtils.setCurrentLanguage(getDefaultLanguageCode());
        BackStackUtils.fullyRestartApp(this);
    }

    private boolean needResetToDefaultLanguage(int id, HashMap<Integer, MallLanguages> serverRevision, boolean contains) {
        if(!contains){
            return !isDefaultLanguage();
        } else {
            MallLanguages mallLanguages = serverRevision.get(id);
            return !mallLanguages.languages.contains(getCurrentLanguageCode());
        }
    }

    public boolean isDefaultLanguage() {
        return LocaleUtils.isNowNextLanguageCode(LocaleUtils.getDefaultLanguageCode());
    }

    public String getCurrentLanguageCode() {
        return LocaleUtils.getCurrentLanguageCode();
    }

    public static class LocaleUpdatedEvent {
        public String mLanguageCode;

        public LocaleUpdatedEvent( String languageCode) {
            mLanguageCode = languageCode;
        }
    }

    public static class ErrorLocaleUpdatedEvent {
    }

    public static class LocaleUpdatedSkipEvent {
        public String mMessage;
        public String mLanguageCode;

        public LocaleUpdatedSkipEvent(String message, String languageCode) {
            mMessage = message;
            mLanguageCode = languageCode;
        }
    }
}
