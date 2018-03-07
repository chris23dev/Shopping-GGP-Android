package com.ggp.theclub.manager;

import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BriteVerifyManager {
    private static final String LOG_TAG = BriteVerifyManager.class.getSimpleName();
    private static BriteVerifyManager briteVerifyManager = new BriteVerifyManager();

    public static BriteVerifyManager getInstance() {
        return briteVerifyManager;
    }

    private static HttpUrl requestUrl;
    private static OkHttpClient okHttpClient;
    private static Gson gson;

    private static final String API_KEY_PARAM = "apikey";
    private static final String EMAIL_PARAM = "address";

    private int TIMEOUT_IN_SECONDS = 3;

    public BriteVerifyManager() {
        gson = new GsonBuilder().create();
        requestUrl = HttpUrl.parse(MallApplication.getApp().getString(R.string.briteverify_url)).newBuilder()
        .setQueryParameter(API_KEY_PARAM, MallApplication.getApp().getString(R.string.briteverify_key)).build();


        okHttpClient =
                new OkHttpClient.Builder().readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public static void checkEmailValid(String email, ValidEmailListener listener) {
        HttpUrl url = requestUrl.newBuilder().addQueryParameter(EMAIL_PARAM, email).build();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(LOG_TAG, e.toString());
                listener.onEmailValid();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    BriteVerifyResponse briteVerifyResponse = gson.fromJson(response.body().string(), BriteVerifyResponse.class);
                    if (briteVerifyResponse.getStatus() != BriteVerifyStatus.invalid) {
                        listener.onEmailValid();
                    } else {
                        listener.onEmailInvalid();
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                    listener.onEmailValid();
                }
            }
        });
    }

    public interface ValidEmailListener {
        public void onEmailValid();
        public void onEmailInvalid();
    }

    private enum BriteVerifyStatus {valid, invalid, unknown, accept_all}

    private class BriteVerifyResponse {
        @Getter @Setter private BriteVerifyStatus status;
    }
}