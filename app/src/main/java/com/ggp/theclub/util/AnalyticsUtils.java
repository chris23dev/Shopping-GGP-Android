package com.ggp.theclub.util;

import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;

import java.util.HashMap;

public class AnalyticsUtils {

    public static void trackShare(AnalyticsManager analyticsManager, Sale sale) {
        String storeName = sale.getTenant() != null ? sale.getTenant().getName() : null;
        HashMap<String, Object> contextData = new HashMap<String, Object>(){{
            put(AnalyticsManager.ContextDataKeys.TenantName, !StringUtils.isEmpty(storeName) ? storeName.toLowerCase() : "");
            put(AnalyticsManager.ContextDataKeys.EventSaleName, sale.getTitle().toLowerCase());
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.SocialShare, contextData);
    }

    public static void trackShare(AnalyticsManager analyticsManager, MallEvent event) {
        Tenant tenant = event.getTenant();
        HashMap<String, Object> contextData = new HashMap<String, Object>(){{
            put(AnalyticsManager.ContextDataKeys.TenantName, tenant != null ? tenant.getName().toLowerCase() : "");
            put(AnalyticsManager.ContextDataKeys.EventSaleName, event.getName().toLowerCase());
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.SocialShare, contextData);
    }
}
