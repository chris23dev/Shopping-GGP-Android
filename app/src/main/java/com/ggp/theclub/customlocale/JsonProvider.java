package com.ggp.theclub.customlocale;

import java.io.InputStream;

/**
 * Created by john.curtis on 4/18/17.
 */
public interface JsonProvider {
    String getJson();
    InputStream getJsonAsInputStream();
}
