package com.ggp.theclub.util;

import android.location.Location;
import android.util.Log;

import com.ggp.theclub.api.GoogleMapsApiClient;
import com.ggp.theclub.event.MapSearchResultEvent;
import com.ggp.theclub.model.GoogleMapsResponse;
import com.ggp.theclub.model.MapsAddress;

import java.util.List;

import de.greenrobot.event.EventBus;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by avishek.das on 1/12/16.
 */
public class LocationUtils {

    private static final String LOG_TAG = LocationUtils.class.getSimpleName();

    @Getter @Setter private static Location currentLocation;

    /**
     *
     * @param address
     *  Makes a call to Google Maps to find the coordinates of {address}.
     *  Results are posted to a MapSearchResultEvent when a response is received.
     */
    public static void getMapsLocation(final String address) {
        GoogleMapsApiClient.getInstance().getGoogleMapsApi().getLocationByAddress(address).enqueue(new Callback<GoogleMapsResponse>() {
            @Override
            public void onResponse(Call<GoogleMapsResponse> call, Response<GoogleMapsResponse> response) {
                if (response.isSuccessful()) {
                    GoogleMapsResponse mapsResponse = response.body();
                    List<MapsAddress> responseList = mapsResponse.getResults();

                    if(responseList.size() > 0){
                        EventBus.getDefault().post(new MapSearchResultEvent(responseList.get(0), address));
                    } else {
                        EventBus.getDefault().post(new MapSearchResultEvent(null, address));
                    }
                } else {
                    Log.e(LOG_TAG, response.message());
                    EventBus.getDefault().post(new MapSearchResultEvent(null, address));
                }
            }

            @Override
            public void onFailure(Call<GoogleMapsResponse> call, Throwable t) {
                Log.w(LOG_TAG, t);
                EventBus.getDefault().post(new MapSearchResultEvent(null, address));
            }
        });
    }
}
