package com.ggp.theclub.api;

import com.ggp.theclub.model.HelloWorldProfileRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface HelloWorldApi {
    String TRUE_CLIENT_IP_HEADER = "True-Client-IP";
    String PROFILES_ENDPOINT = "/v1/mallappsweeps/profiles";

    @Headers( {BaseApiClient.ACCEPT_JSON_HEADER, BaseApiClient.CONTENT_TYPE_JSON_HEADER} )
    @POST(PROFILES_ENDPOINT)
    Call<Object> postProfile(@Body HelloWorldProfileRequest requestBody, @Header(TRUE_CLIENT_IP_HEADER) String clientIp);
}
