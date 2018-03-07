package com.ggp.theclub.customlocale.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john.curtis on 4/17/17.
 */
public class LocaleParserImpl implements LocaleParser<HashMap<String, String>> {
    private Gson mGson;

    public LocaleParserImpl() {
        mGson = new Gson();
    }

    @Override
    public HashMap<String, String> parseValues(String json) throws JsonSyntaxException{

        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
//        LanguageResponse languageResponse = mGson.fromJson(json, LanguageResponse.class);
//        return languageResponse.mStrings;
        HashMap<String, String> languageResponse = mGson.fromJson(json, type);

        return languageResponse;
    }

    @Override
    public boolean parseValues(InputStream inputStream, OutputStream outputStream) {
        //TODO JSON can be big. Consider to implement via IO threads or remove at all.
        throw new IllegalStateException("Is not supported yet");
    }
}
