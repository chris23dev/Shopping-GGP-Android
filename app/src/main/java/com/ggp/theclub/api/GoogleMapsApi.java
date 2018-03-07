package com.ggp.theclub.api;

import com.ggp.theclub.model.GoogleMapsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApi {

    String BASE_URL = "http://maps.google.com/maps/api/";
    String GEOCODE_ENDPOINT = "geocode/json";

    @GET(GEOCODE_ENDPOINT)
    Call<GoogleMapsResponse> getLocationByAddress(@Query("address") String address);
}
