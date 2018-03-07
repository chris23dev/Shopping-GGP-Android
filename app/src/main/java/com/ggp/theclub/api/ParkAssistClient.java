package com.ggp.theclub.api;

import com.ggp.theclub.util.StringUtils;

import java.util.HashMap;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParkAssistClient extends BaseApiClient {

    private static ParkAssistClient instance;
    @Getter private ParkAssistApi parkAssistApi;

    public static ParkAssistClient getInstance() {
        if (instance == null) {
            instance = new ParkAssistClient();
        }
        return instance;
    }

    public ParkAssistClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(getCache())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ParkAssistApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();

        parkAssistApi = retrofit.create(ParkAssistApi.class);
    }

    public static HashMap<String, String> getParameters(String plate, String siteName, String secret) {
        String signature = generateSignature(plate, siteName, secret);

        HashMap<String, String> params = new HashMap<>();
        params.put(ParkAssistApi.DEVICE_PARAM, ParkAssistApi.DEVICE_ID);
        params.put(ParkAssistApi.PLATE_PARAM, plate);
        params.put(ParkAssistApi.SIGNATURE_PARAM, signature);
        params.put(ParkAssistApi.SITE_PARAM, siteName);

        return params;
    }

    public static HashMap<String, String> getParameters(String siteName, String secret) {
        String signature = generateSignature(siteName, secret);

        HashMap<String, String> params = new HashMap<>();
        params.put(ParkAssistApi.DEVICE_PARAM, ParkAssistApi.DEVICE_ID);
        params.put(ParkAssistApi.SIGNATURE_PARAM, signature);
        params.put(ParkAssistApi.SITE_PARAM, siteName);

        return params;
    }

    public static String getCarLocationImageUrl(String uuid, String siteName, String secret) {
        String signature = generateSignature(siteName, secret);

        return ParkAssistApi.BASE_URL +
                "thumbnails/" + uuid + ".jpg?" +
                ParkAssistApi.DEVICE_PARAM + "=" + ParkAssistApi.DEVICE_ID + "&" +
                ParkAssistApi.SIGNATURE_PARAM + "=" + signature + "&" +
                ParkAssistApi.SITE_PARAM + "=" + siteName;
    }

    public static String getMapImageUrl(String mapName, String siteName, String secret) {
        String signature = generateSignature(siteName, secret);

        return ParkAssistApi.BASE_URL +
                "maps/" + mapName + ".png?" +
                ParkAssistApi.DEVICE_PARAM + "=" + ParkAssistApi.DEVICE_ID + "&" +
                ParkAssistApi.SIGNATURE_PARAM + "=" + signature + "&" +
                ParkAssistApi.SITE_PARAM + "=" + siteName;
    }

    private static String generateSignature(String plate, String siteName, String secret) {
        return StringUtils.getMd5Hash(secret +
                ParkAssistApi.DEVICE_PARAM + "=" + ParkAssistApi.DEVICE_ID + "," +
                ParkAssistApi.PLATE_PARAM + "=" + plate + "," +
                ParkAssistApi.SITE_PARAM + "=" + siteName);
    }

    private static String generateSignature(String siteName, String secret) {
        return StringUtils.getMd5Hash(secret +
                ParkAssistApi.DEVICE_PARAM + "=" + ParkAssistApi.DEVICE_ID + "," +
                ParkAssistApi.SITE_PARAM + "=" + siteName);
    }
}
