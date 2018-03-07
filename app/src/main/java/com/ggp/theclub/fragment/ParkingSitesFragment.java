package com.ggp.theclub.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ParkingGaragesAdapter;
import com.ggp.theclub.api.ParkAssistClient;
import com.ggp.theclub.model.ParkingSite;
import com.ggp.theclub.model.ParkingZone;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingSitesFragment extends BaseFragment {
    @Bind(R.id.parking_garage_list) CustomRecyclerView parkingGarageList;
    private ParkAssistClient parkAssistClient = new ParkAssistClient();

    public static ParkingSitesFragment newInstance() {
        return new ParkingSitesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(inflater, R.layout.parking_sites_fragment, container);
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        fetchParkingSite();
    }

    private void fetchParkingSite() {
        mallRepository.queryForParkingSite(this::fetchParkingZones);
    }

    private void fetchParkingZones(ParkingSite parkingSite) {
        if (parkingSite == null || parkingSite.getSiteName() == null || parkingSite.getSecret() == null) {
            Log.d(getFragmentTag(), "Invalid parking site");
            return;
        }

        HashMap params = ParkAssistClient.getParameters(parkingSite.getSiteName(), parkingSite.getSecret());

        parkAssistClient.getParkAssistApi().getZones(params).enqueue(new Callback<ArrayList<ParkingZone>>() {
            @Override
            public void onResponse(Call<ArrayList<ParkingZone>> call, Response<ArrayList<ParkingZone>> response) {
                ArrayList<ParkingZone> zones = new ArrayList();
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    zones = response.body();
                }
                parkingGarageList.setAdapter(new ParkingGaragesAdapter(getActivity(), parkingSite, zones));
            }

            @Override
            public void onFailure(Call<ArrayList<ParkingZone>> call, Throwable t) {
                Log.w(getFragmentTag(), t);
            }
        });
    }
}