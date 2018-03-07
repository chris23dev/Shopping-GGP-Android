package com.ggp.theclub.customlocale;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

import java.util.Map;

/**
 * Created by john.curtis on 4/17/17.
 */
public class LocaleMatcherImpl implements LocaleMatcher {

    private Map<Integer, String> matchMap;

    public LocaleMatcherImpl() {
        ResourceMatcher matcher = new ResourceMatcher();
        matchMap = matcher.getMatchMap();
    }

    @Override
    public String getKeyById(@StringRes int resId) {
        return matchMap.get(resId);
    }

    @Override
    public String getKeyArrayById(@ArrayRes int arrayResId) {
        return matchMap.get(arrayResId);
    }
}
