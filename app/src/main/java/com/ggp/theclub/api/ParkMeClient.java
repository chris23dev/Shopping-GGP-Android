package com.ggp.theclub.api;

import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParkMeClient extends BaseApiClient {

    private static ParkMeClient instance;
    @Getter private ParkMeApi parkMeApi;

    public static ParkMeClient getInstance() {
        if (instance == null) {
            instance = new ParkMeClient();
        }
        return instance;
    }

    public ParkMeClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(getCache())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ParkMeApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();

        parkMeApi = retrofit.create(ParkMeApi.class);
    }

    public static HashMap getLotParameters(LocalDateTime time) {
        String entryTime = ISODateTimeFormat.dateHourMinute().print(time);
        return new HashMap<String, String>() {
            {
                put("offset", ParkMeApi.OFFSET);
                put("limit", ParkMeApi.LIMIT);
                put("entry_time", entryTime);
                put("duration", ParkMeApi.DURATION);
            }
        };
    }

    public static String getLocation(double latitude, double longitude){
        return latitude + "|" + longitude;
    }

    public static int getRadius(){
        return ParkMeApi.RADIUS_METERS;
    }
}
