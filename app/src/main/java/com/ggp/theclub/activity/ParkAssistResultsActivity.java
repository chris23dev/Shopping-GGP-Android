package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ParkAssistResultsAdapter;
import com.ggp.theclub.model.CarLocation;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;

public class ParkAssistResultsActivity extends BaseActivity {
    @BindString(R.string.park_assist_results_format) String resultsFormat;
    @Bind(R.id.description_view) TextView descriptionView;
    @Bind(R.id.car_locations_list) CustomRecyclerView carLocationsList;

    private static final String CAR_LOCATIONS_EXTRA = "CAR_LOCATIONS";
    private String licensePlateNumber;
    private List<CarLocation> carLocations;

    public static Intent buildIntent(Context context, String licensePlateNumber, List<CarLocation> carLocations) {
        Intent intent = buildIntent(context, ParkAssistResultsActivity.class, licensePlateNumber);
        intent.putExtra(CAR_LOCATIONS_EXTRA, gson.toJson(carLocations));
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        licensePlateNumber = getIntentExtra(String.class);
        carLocations = Arrays.asList(getIntentExtra(CAR_LOCATIONS_EXTRA, CarLocation[].class));
        setContentView(R.layout.park_assist_results_activity);
    }

    @Override
    protected void configureView() {
        enableBackButton();
        setTitle(R.string.park_assist_results_title);
        descriptionView.setText(String.format(resultsFormat, carLocations.size(), licensePlateNumber));
        mallRepository.queryForParkingSite(parkingSite -> carLocationsList.setAdapter(new ParkAssistResultsAdapter(this, parkingSite, carLocations)));
    }
}