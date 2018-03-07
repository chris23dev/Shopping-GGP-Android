package com.ggp.theclub.customlocale;

import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;

/**
 * Created by john.curtis on 4/17/17.
 */
public interface LocaleMatcher {
    String getKeyById(@IdRes int resId);
    String getKeyArrayById(@ArrayRes int arrayResId);
}
