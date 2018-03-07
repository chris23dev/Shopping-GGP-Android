package com.ggp.theclub.model;

import android.util.Log;

import com.ggp.theclub.util.GigyaUtils;
import com.gigya.socialize.GSObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by avishek.das on 3/31/16.
 */
public class GigyaEmailResponse {
    private static String LOG_TAG = GigyaEmailResponse.class.getName();

    @Getter @Setter private Boolean isAvailable;

    public GigyaEmailResponse(GSObject gsObject) {
        try {
            Boolean available = gsObject.getBool(GigyaUtils.KEY_AVAILABLE);
            setIsAvailable(available);

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

}
