package com.ggp.theclub.customlocale.gateway.rest;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by john.curtis on 5/17/17.
 */
public class LocaleApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url) {
        if (retrofit == null) {

//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            // set your desired log level
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .addInterceptor(logging)
//                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
//                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static ApiInterface getLocaleClient() {
        String url = MallApplication.getApp().getString(R.string.malls_languages_url);
        return LocaleApiClient.getClient(url).create(ApiInterface.class);
    }
}
