package com.ggp.theclub.customlocale;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

/**
 * Created by john.curtis on 4/17/17.
 */

public interface LocaleStorage {

    /**
     * @param key Can't be null
     * @return value be key provided as argument. In case no value found return null.
     */
    String getValue(@NonNull String key);

    /**
     * @param key Can't be null
     * @return value be key provided as argument. In case no value found return null.
     */
    String[] getValueArray(@NonNull String key);

    /**
     * @param key Can't be null
     * @param value Can be null
     * @return true in case value was saved or updated.
     */
    boolean setValue(@NonNull String key, String value);

    /**
     * @param keyValueMap
     * @return number of added or updated values
     */
    int setValues(@NonNull HashMap<String, String> keyValueMap);

    /**
     * @param keyValueMap
     * @return number of added or updated values
     */
    int setArrayValues(@NonNull HashMap<String, List<String>> keyValueMap);
}
