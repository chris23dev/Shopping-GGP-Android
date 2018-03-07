package com.ggp.theclub.customlocale;

/**
 * Created by john.curtis on 4/17/17.
 */
public interface LocaleInspector {
    boolean isNeedLoad(String localVersion, String localLocale);
    boolean isDefaultConfigured();
    void recordConfiguredDefault(boolean success);
    String getRecordedRevisionByLocale(String languageCode);
    void recordLoadedLocale(String languageCode, String serverVersion);
}
