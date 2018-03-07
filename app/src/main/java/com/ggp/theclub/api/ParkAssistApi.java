package com.ggp.theclub.api;

import com.ggp.theclub.model.CarLocation;
import com.ggp.theclub.model.ParkingZone;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ParkAssistApi {

    String BASE_URL = "https://insights.parkassist.com/find_your_car/";
    String DEVICE_ID = UUID.randomUUID().toString();
    String SEARCH_ENDPOINT = "search.json";
    String ZONES_ENDPOINT = "zones.json";
    String DEVICE_PARAM = "device";
    String PLATE_PARAM = "plate";
    String SIGNATURE_PARAM = "signature";
    String SITE_PARAM = "site";

    @GET(SEARCH_ENDPOINT)
    Call<ArrayList<CarLocation>> getCarLocations(@QueryMap Map<String, String> parameters);

    @GET(ZONES_ENDPOINT)
    Call<ArrayList<ParkingZone>> getZones(@QueryMap Map<String, String> parameters);
}
