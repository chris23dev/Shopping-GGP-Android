package com.ggp.theclub;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.ggp.theclub.controller.HomeController;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.AttributionManager;
import com.ggp.theclub.manager.CrashReportingManager;
import com.ggp.theclub.manager.DeepLinkingManager;
import com.ggp.theclub.manager.FeedbackManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.manager.MockAccountManagerImpl;
import com.ggp.theclub.manager.impl.AccountManagerImpl;
import com.ggp.theclub.manager.impl.AnalyticsManagerImpl;
import com.ggp.theclub.manager.impl.AttributionManagerImpl;
import com.ggp.theclub.manager.impl.DeepLinkingManagerImpl;
import com.ggp.theclub.manager.impl.FeedbackManagerImpl;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.repository.impl.MallRepositoryImpl;
import com.ggp.theclub.widget.CustomLocalisationResources;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

import lombok.Getter;

public class MallApplication extends MultiDexApplication {
    private final String LOG_TAG = getClass().getSimpleName();
    private final String DEV_FLAVOR = "dev";
    @Getter private static MallApplication app;

    //repositories
    @Getter private MallRepository mallRepository;

    //managers
    private ConnectivityManager connectivityManager;
    @Getter private MallManager mallManager;
    @Getter private AnalyticsManager analyticsManager;
    @Getter private AccountManager accountManager;
    @Getter private AttributionManager attributionManager;
    @Getter private DeepLinkingManager deepLinkingManager;
    @Getter private FeedbackManager feedbackManager;
    @Getter private HomeController homeController;
    private Resources mResources;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Fabric.with(this, new Crashlytics());
        configureRepositories();
        configureManagers();
//        configPicasso();
        configImageLoader();
        JodaTimeAndroid.init(this);
    }

    /**
     * This method is named as such because getColor is implemented
     * as final in Context.java, and therefore cannot be overridden
     */
    public final int getColorById(int id) {
        return getResources().getColor(id);
    }

    public String[] getStringArray(int id) {
        return getResources().getStringArray(id);
    }

    private void configureRepositories() {
        mallRepository = new MallRepositoryImpl();
        homeController = new HomeController();
    }

    private void configureManagers() {

        mallManager = new MallManager();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isDev = BuildConfig.FLAVOR.equals(DEV_FLAVOR);
        if (!isDev) {
            CrashReportingManager.start();
        }
        analyticsManager = new AnalyticsManagerImpl(mallRepository, mallManager);
//        accountManager = new AccountManagerImpl();
        accountManager = new MockAccountManagerImpl();
        attributionManager = new AttributionManagerImpl();
        deepLinkingManager = new DeepLinkingManagerImpl();
        feedbackManager = new FeedbackManagerImpl();

        MapManager.getInstance().start();
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public Resources getResources() {
        if (CustomLocalisationResources.shouldBeUsed()) {
            if (mResources == null) {
                mResources = new CustomLocalisationResources(super.getResources());
            }
        }
        return mResources == null ? super.getResources() : mResources;
    }

    public void invalidateLocale() {
        if(mResources instanceof CustomLocalisationResources){
            ((CustomLocalisationResources)mResources).invalidateLocale();
        }
    }

    @Deprecated
    private void configPicasso(){
        Picasso.Builder builder = new Picasso.Builder(this);
//        Picasso built = builder.downloader(new OkHttpDownloader(this,  Integer.MAX_VALUE)).build();
        Picasso built = builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE)).build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);

        Picasso.setSingletonInstance(built);
    }

    private void configImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
    }
}