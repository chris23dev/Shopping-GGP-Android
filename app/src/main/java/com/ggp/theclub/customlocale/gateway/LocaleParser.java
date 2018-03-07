package com.ggp.theclub.customlocale.gateway;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by john.curtis on 4/17/17.
 */

public interface LocaleParser<T> {
    /**
     * @param json
     * @return return Type that set in implementation
     */
    T parseValues(String json);
    boolean parseValues(InputStream inputStream, OutputStream outputStream);
}
