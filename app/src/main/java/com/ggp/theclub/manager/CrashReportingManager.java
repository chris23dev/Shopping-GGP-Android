package com.ggp.theclub.manager;

import com.ggp.theclub.MallApplication;
import com.newrelic.agent.android.NewRelic;

public class CrashReportingManager {

    private static final String APP_TOKEN = "AA7de705e1724a8ff6239cc8ff6034b328033fa128";
    private static final String TENANT_NAME = "tenant";
    private static final String MALL_NAME = "mall";

    public static void start() {
        NewRelic.withApplicationToken(APP_TOKEN).start(MallApplication.getApp());
    }

    public static void trackMall(String mallName) {
        NewRelic.setAttribute(MALL_NAME, mallName);
    }

    public static void trackInteraction(String screen, String tenant) {
        NewRelic.startInteraction(screen);
        NewRelic.setAttribute(TENANT_NAME, tenant);
    }
}