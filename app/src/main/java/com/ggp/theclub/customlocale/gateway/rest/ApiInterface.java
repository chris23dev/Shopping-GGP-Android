package com.ggp.theclub.customlocale.gateway.rest;

import com.ggp.theclub.customlocale.gateway.Language;
import com.ggp.theclub.customlocale.gateway.MallLanguages;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Url;

/**
 * Created by john.curtis on 5/17/17.
 */
public interface ApiInterface {
    @GET("languages/android.json")
    Call<List<Language>> getLanguages();

    @GET
    Call<ResponseBody> getLanguage(@Url String url);

    @GET("malls.json")
    Call<HashMap<Integer, MallLanguages>> getMallsLanguages();


    @HEAD
    Call<Void> getLanguageHeaders(@Url String url);
}
