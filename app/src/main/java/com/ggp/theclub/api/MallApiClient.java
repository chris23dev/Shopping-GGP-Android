package com.ggp.theclub.api;

import android.util.Log;

import com.ggp.theclub.BuildConfig;
import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.customlocale.LocaleUtils;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.model.MobileConfig;
import com.ggp.theclub.util.StringUtils;

import java.io.IOException;
import java.util.Locale;

import lombok.Getter;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MallApiClient extends BaseApiClient {

    private final String AUTH_USERNAME = "android_read";
    private final String AUTH_PASSWORD = "8PzSRZM8VW4SWAbg";
    private final String PREFERENCES_AUTH_TOKEN = "AUTH_TOKEN";

    private static MallApiClient instance = new MallApiClient();
    @Getter private MallApi mallApi;

    public static MallApiClient getInstance() {
        return instance;
    }

    public MallApiClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(getCache())
                .addInterceptor(interceptor)
                .addInterceptor(localeInterceptor)
//                .addInterceptor(logging)
                .authenticator(authenticator)
                .build();

        String string = MallApplication.getApp().getString(R.string.api_base_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(string)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mallApi = retrofit.create(MallApi.class);
    }

    /**
     * Automatically intercepts all outgoing requests, adds the right headers,
     * executes the request, and returns the response from either network or cache.
     * All API requests must include an Authorization header with a valid authToken.
     * If the authToken is invalid, a new one is created via API_ENDPOINT_AUTH.
     * To avoid repeated attempts to create a valid authToken, if the previous attempt failed, it is not retried.
     */
    private Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (request.url().toString().contains(MallApi.API_ENDPOINT_AUTH)) {
                String credentials = Credentials.basic(AUTH_USERNAME, AUTH_PASSWORD);
                if (credentials.equals(request.header(AUTH_HEADER_NAME))) {
                    return null;
                }
                Log.d(getLogTag(), "INTERCEPTOR: Adding " + credentials + " to " + request.url().toString());
                request = request.newBuilder().addHeader(AUTH_HEADER_NAME, credentials).build();
            } else if (!StringUtils.isEmpty(getAuthToken())) {
                Log.d(getLogTag(), "INTERCEPTOR: Adding " + getAuthToken() + " to " + request.url().toString());
                request = request.newBuilder().addHeader(AUTH_HEADER_NAME, getAuthToken()).build();
            }
            Log.d(getLogTag(), String.format("REQUEST: %s %s", request.url(), request.headers().toString()));
            long requestTime = System.nanoTime();
            Response response = chain.proceed(request);
            long responseTime = System.nanoTime();
            Log.d(getLogTag(), String.format("RESPONSE: %s in %.1f ms from %s", request.url(), (responseTime - requestTime) / 1e6d, response.cacheResponse() == null ? "NETWORK" : "CACHE"));
            return response;
        }
    };

    /**
     * Automatically intercepts 401 Unauthorized responses, generates a new authToken,
     * updates the request with the right authToken, and returns the request to be retried.
     */
    private Authenticator authenticator = new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            Log.d(getLogTag(), "AUTHENTICATOR: Intercepted " + response.request().url().toString());
            setAuthToken(getMallApi().getAuthToken().execute().body().getAuthToken());
            Log.d(getLogTag(), "AUTHENTICATOR: Adding " + getAuthToken() + " to " + response.request().url().toString());
            return response.request().newBuilder().addHeader(AUTH_HEADER_NAME, getAuthToken()).build();
        }
    };

    /**
     * Locale interceptor. We do care about localisation selected by user in mall.
     */
    private Interceptor localeInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            String languageCode = LocaleUtils.getCurrentLanguageCode();
            String language = String.format("%s;q=1", languageCode);
            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept-Language", language);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    };

    public String getAuthToken() {
        return PreferencesManager.getInstance().getSharedPreferences().getString(PREFERENCES_AUTH_TOKEN, null);
    }

    public void setAuthToken(String authToken) {
        PreferencesManager.getInstance().getSharedPreferences().edit().putString(PREFERENCES_AUTH_TOKEN, authToken).commit();
    }

    public void checkVersion(VersionCheckCallback callback) {
        getMallApi().getMobileConfig().enqueue(new Callback<MobileConfig>() {
            @Override
            public void onResponse(Call<MobileConfig> call, retrofit2.Response<MobileConfig> response) {
                boolean acceptable = !response.isSuccessful() || response.body().getMinAndroidVersion() <=  BuildConfig.VERSION_CODE;
                if (acceptable) {
                    callback.onAcceptableVersion();
                } else {
                    callback.onUnacceptableVersion();
                }
            }

            @Override
            public void onFailure(Call<MobileConfig> call, Throwable t) {
                Log.w(getLogTag(), t);
                callback.onAcceptableVersion();
            }
        });
    }

    public interface VersionCheckCallback {
        void onAcceptableVersion();
        void onUnacceptableVersion();
    }
}