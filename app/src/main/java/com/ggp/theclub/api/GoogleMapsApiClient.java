package com.ggp.theclub.api;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleMapsApiClient extends BaseApiClient {

    private static GoogleMapsApiClient instance;
    @Getter private GoogleMapsApi googleMapsApi;

    public static GoogleMapsApiClient getInstance() {
        if (instance == null) {
            instance = new GoogleMapsApiClient();
        }
        return instance;
    }

    public GoogleMapsApiClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(getCache())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GoogleMapsApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();

        googleMapsApi = retrofit.create(GoogleMapsApi.class);
    }
}
