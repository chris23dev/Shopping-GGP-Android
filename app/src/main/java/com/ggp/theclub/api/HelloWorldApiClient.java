package com.ggp.theclub.api;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.util.StringUtils;

import java.io.IOException;

import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HelloWorldApiClient extends BaseApiClient{
    private static final String AUTH_HEADER_FORMAT = "Basic %s";

    @Getter private static HelloWorldApiClient instance = new HelloWorldApiClient();
    @Getter private HelloWorldApi helloWorldApi;

    public HelloWorldApiClient() {

        String baseUrl = MallApplication.getApp().getString(R.string.hello_world_base_url);
        String authenticationString = MallApplication.getApp().getString(R.string.hello_world_auth_string);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().cache(getCache());
        if (!StringUtils.isEmpty(authenticationString)) {
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().addHeader(AUTH_HEADER_NAME, String.format(AUTH_HEADER_FORMAT, authenticationString)).build();
                    return chain.proceed(request);
                }
            });
        }
        OkHttpClient okHttpClient = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();

        helloWorldApi = retrofit.create(HelloWorldApi.class);
    }
}
