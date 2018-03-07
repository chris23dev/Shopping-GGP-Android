package com.ggp.theclub.manager;

import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.model.Mall;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class MallManager {
    private final String LOG_TAG = getClass().getSimpleName();
    private final int MAX_RECENT_MALLS = 5;
    private final String PREFERENCES_RECENT_MALLS = "RECENT_MALLS";

    public Mall getMall() {
        List<Mall> recentMalls = getRecentMallsList();
        return recentMalls.size() > 0 ? recentMalls.get(0) : new Mall();
    }

    public void setMall(Mall mall) {
        boolean mallChanged = mall.getId() != getMall().getId();

        CrashReportingManager.trackMall(mall.getName());
        addRecentMall(mall);
        if(mallChanged) {
            // Don't refresh if user selects the current mall. Otherwise jibestream crashes.
            MapManager.getInstance().refresh();
        }
        MallApplication.getApp().getMallRepository().prefetchData();
    }

    public List<Mall> getRecentMallsList() {
        String jsonString = PreferencesManager.getInstance().getSharedPreferences().getString(PREFERENCES_RECENT_MALLS, null);
        Type listType = new TypeToken<ArrayList<Mall>>() {}.getType();
        List<Mall> recentMallsList = new ArrayList<>();
        try {
            recentMallsList = new Gson().fromJson(jsonString, listType);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return recentMallsList != null ? recentMallsList : new ArrayList<>();
    }

    public void clearRecentMalls() {
        PreferencesManager.getInstance().removeObject(PREFERENCES_RECENT_MALLS);
    }

    public boolean hasValidMall() {
        return getMall().isValid();
    }

    public boolean hasValidActiveMall() {
        Mall mall = getMall();
        return mall.isValid() && mall.getStatus() == Mall.MallStatus.ACTIVE;
    }

    public void addRecentMall(Mall mall) {
        List<Mall> recentMallsList = getRecentMallsList();

        //Remove duplicate if it already exists, so new one moves to top of list
        recentMallsList = StreamSupport.stream(recentMallsList).filter(m -> m.getId() != mall.getId()).collect(Collectors.toList());
        recentMallsList.add(0, mall);

        if (recentMallsList.size() > MAX_RECENT_MALLS) {
            recentMallsList.remove(recentMallsList.size() - 1);
        }

        PreferencesManager.getInstance().saveObject(PREFERENCES_RECENT_MALLS, recentMallsList);
    }
}
