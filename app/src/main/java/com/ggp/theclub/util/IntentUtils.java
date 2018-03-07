package com.ggp.theclub.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.ParkingGarage;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;

public class IntentUtils {
    private static final String LOG_TAG = IntentUtils.class.getSimpleName();
    private static final char PLUS = '+';
    private static final char SPACE = ' ';
    private static String APP_STORE_INTENT_TEMPLATE = "market://details?id=%s";
    private static String APP_STORE_WEBSITE_INTENT_TEMPLATE = "https://play.google.com/tenant/apps/details?id=%s";

    public static void startShareChooser(String subject, String body, Activity activity) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        shareIntent.setType("text/plain");
        String shareText = MallApplication.getApp().getString(R.string.share_text);
        activity.startActivity(Intent.createChooser(shareIntent, shareText));
    }

    public static boolean startIntentIfSupported(Intent intent, Activity activity) {
        if (intent.resolveActivity(MallApplication.getApp().getPackageManager()) != null) {
            activity.startActivity(intent);
            return true;
        }
        Log.i(LOG_TAG, "Intent is not supported: " + intent.toString());
        return false;
    }

    public static boolean showDirectionsForTenant(Tenant tenant, Activity activity) {
        String mallName = MallApplication.getApp().getMallManager().getMall().getName();
        String googleMapsBaseUrl = MallApplication.getApp().getString(R.string.google_maps_base_url);
        String googleMapsSearchFormat = MallApplication.getApp().getString(R.string.google_maps_search_format);
        String directionsUrl = googleMapsBaseUrl.concat(String.format(googleMapsSearchFormat, tenant.getName().replace(SPACE, PLUS), mallName.replace(SPACE, PLUS)));
        Uri directionsIntent = Uri.parse(directionsUrl);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, directionsIntent);
        return startIntentIfSupported(mapIntent, activity);
    }

    public static boolean showDirectionsForParkingGarage(ParkingGarage parkingGarage, Activity activity) {
        String googleMapsBaseUrl = MallApplication.getApp().getString(R.string.google_maps_base_url);
        String googleMapsPlaceFormat = MallApplication.getApp().getString(R.string.google_maps_place_format);
        String directionsUrl = googleMapsBaseUrl.concat(String.format(googleMapsPlaceFormat, parkingGarage.getLatitude(), parkingGarage.getLongitude()));
        Uri directionsIntent = Uri.parse(directionsUrl);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, directionsIntent);
        return startIntentIfSupported(mapIntent, activity);
    }

    public static void startPhoneNumberIntent(String phoneNumber, Activity activity) {
        startIntentIfSupported(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)), activity);
    }

    public static void showAppInStore(Activity activity) {
        String packageName = MallApplication.getApp().getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(APP_STORE_INTENT_TEMPLATE, packageName))));
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(APP_STORE_WEBSITE_INTENT_TEMPLATE, packageName))));
        }
    }

    public static void showAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", MallApplication.getApp().getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void showGpsSettings(Activity activity) {
        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public static void share(Activity activity, Sale sale, MallManager mallManager, AnalyticsManager analyticsManager) {
        AnalyticsUtils.trackShare(analyticsManager, sale);
        String storeName = sale.getTenant() != null ? sale.getTenant().getName() : null;
        String shareSubjectFormat = activity.getResources().getString(R.string.share_subject_format);
        String saleShareFormat = activity.getResources().getString(R.string.sale_share_format);
        String saleSubject = String.format(shareSubjectFormat, sale.getTitle(), storeName);
        String saleBody = String.format(saleShareFormat, sale.getTitle(), storeName, mallManager.getMall().getWebsiteUrl(), Integer.toString(sale.getId()));
        startShareChooser(saleSubject, saleBody, activity);
    }

    public static void share(Activity activity, MallEvent event, MallManager mallManager, AnalyticsManager analyticsManager) {
        AnalyticsUtils.trackShare(analyticsManager, event);
        String shareSubjectFormat = activity.getResources().getString(R.string.share_subject_format);
        String eventShareFormat = activity.getResources().getString(R.string.event_share_format);
        String eventSubject = String.format(shareSubjectFormat, event.getTitle(), event.getLocation());
        String eventBody = String.format(eventShareFormat, event.getTitle(), event.getLocation(), mallManager.getMall().getWebsiteUrl(), Integer.toString(event.getId()));
        startShareChooser(eventSubject, eventBody, activity);
    }

    public static void email(Activity activity, String emailAddress) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        Uri uri = Uri.parse("mailto:" + emailAddress);
        emailIntent.setData(uri);
        startIntentIfSupported(emailIntent, activity);
    }
}