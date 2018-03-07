package com.ggp.theclub.activity;

import android.app.Activity;
import android.content.res.Resources;

import com.ggp.theclub.widget.CustomLocalisationResources;

public abstract class CustomResourcesActivity extends Activity {
    private CustomLocalisationResources mResources;

    @Override
    public Resources getResources() {
        if (CustomLocalisationResources.shouldBeUsed()) {
            if (mResources == null) {
                mResources = new CustomLocalisationResources(super.getResources());
            }
        }
        return mResources == null ? super.getResources() : mResources;
    }
}