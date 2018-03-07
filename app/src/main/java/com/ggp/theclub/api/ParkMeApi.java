package com.ggp.theclub.api;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.model.ParkingLotsResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ParkMeApi {
// https://digitalservices-qa.ggp.com/mall-api/parkme/lots/
// 41.85028%7C-87.9533/radius/2000?
// offset=1&
// limit=50&
// entry_time=2017-09-05T12:57&
// duration=30&
// callback=jQuery1112043642547333342185_1502128663863

    String PUBLISHER_ID = "754e2686";
    String KEY = "068a!";

    String LIMIT = "50";
    int RADIUS_METERS = 2000;
    String DURATION = "30";
    String OFFSET = "1";
    String BASE_URL = MallApplication.getApp().getString(R.string.api_base_url);

    @GET("parkme/lots/{location}/radius/{radius}")
    Call<ParkingLotsResponse> getParkingLots(
            @Path("location") String location,
            @Path("radius") int radius,
            @QueryMap Map<String, String> parameters
    );
}