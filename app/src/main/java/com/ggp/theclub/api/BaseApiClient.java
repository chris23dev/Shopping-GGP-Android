package com.ggp.theclub.api;

import com.ggp.theclub.MallApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Cache;

public abstract class BaseApiClient {

    protected final String AUTH_HEADER_NAME = "Authorization";
    public static final String CONTENT_TYPE_JSON_HEADER = "Content-Type: application/json";
    public static final String ACCEPT_JSON_HEADER = "Accept: application/json";
    private static final int CACHE_SIZE = 2000000; // 2 MB
    private final String CACHE_DIR = "ggp";
    private final Cache cache = new Cache(new File(MallApplication.getApp().getCacheDir(), CACHE_DIR), CACHE_SIZE);
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

    protected Cache getCache() { return cache; }

    protected Gson getGson() { return gson; }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    public class DateDeserializer implements JsonDeserializer<Date> {
        private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String j = json.getAsJsonPrimitive().getAsString();
                return parseDate(j);
            } catch (ParseException e) {
                throw new JsonParseException(e.getMessage(), e);
            }
        }

        private Date parseDate(String dateString) throws ParseException {
            if (dateString != null && dateString.trim().length() > 0) {
                try {
                    return format1.parse(dateString);
                } catch (ParseException pe) {
                    return format2.parse(dateString);
                }
            } else {
                return null;
            }
        }
    }
}
