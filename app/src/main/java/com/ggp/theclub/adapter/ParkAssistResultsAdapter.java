package com.ggp.theclub.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.ParkAssistMapActivity;
import com.ggp.theclub.adapter.ParkAssistResultsAdapter.ParkAssistResultViewHolder;
import com.ggp.theclub.api.ParkAssistClient;
import com.ggp.theclub.model.CarLocation;
import com.ggp.theclub.model.ParkingGarage;
import com.ggp.theclub.model.ParkingLevel;
import com.ggp.theclub.model.ParkingSite;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.ParkingSiteUtils;
import com.ggp.theclub.util.StringUtils;
import com.squareup.picasso.Callback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParkAssistResultsAdapter extends RecyclerView.Adapter<ParkAssistResultViewHolder> {
    @Bind(R.id.map_view_button) Button mapViewBtn;
    private Activity activity;
    private ParkingSite parkingSite;
    private List<CarLocation> carLocations;

    public ParkAssistResultsAdapter(Activity activity, ParkingSite parkingSite, List<CarLocation> carLocations) {
        this.activity = activity;
        this.parkingSite = parkingSite;
        this.carLocations = carLocations;
    }

    @Override
    public ParkAssistResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.park_assist_result_item, parent, false);
        ButterKnife.bind(this,view);
        mapViewBtn.setText(parent.getContext().getString(R.string.park_assist_map_title));
        return new ParkAssistResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkAssistResultViewHolder holder, int position) {
        holder.onBind(carLocations.get(position));
    }

    @Override
    public int getItemCount() {
        return carLocations.size();
    }

    public class ParkAssistResultViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_view) ImageView imageView;
        @Bind(R.id.garage_view) TextView garageView;
        @Bind(R.id.location_view) TextView locationView;
        @Bind(R.id.map_view_button) Button mapViewButton;
        private CarLocation carLocation;

        public ParkAssistResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(CarLocation carLocation) {
            this.carLocation = carLocation;
            String imageUrl = ParkAssistClient.getCarLocationImageUrl(carLocation.getUuid(), parkingSite.getSiteName(), parkingSite.getSecret());
            if (!StringUtils.isEmpty(imageUrl)) {
                ImageUtils.loadImage(imageUrl, imageView, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError() {
                        imageView.setBackgroundResource(R.drawable.park_assist_placeholder);
                    }
                });
            } else {
                imageView.setBackgroundResource(R.drawable.park_assist_placeholder);
            }

            ParkingLevel parkingLevel = ParkingSiteUtils.findParkingLevelByZoneName(carLocation.getZoneName(), parkingSite.getLevels());
            ParkingGarage parkingGarage = ParkingSiteUtils.findGarage(parkingLevel.getGarageId(), parkingSite.getGarages());
            String parkingLocation = parkingLevel.getLevelName();

            garageView.setText(parkingGarage.getGarageName());
            locationView.setText(parkingLocation);
            mapViewButton.setVisibility(StringUtils.isEmpty(carLocation.getMap()) ? View.GONE : View.VISIBLE);
        }

        @OnClick(R.id.map_view_button)
        public void onMapViewButtonClick() {
            String mapUrl = ParkAssistClient.getMapImageUrl(carLocation.getMap(), parkingSite.getSiteName(), parkingSite.getSecret());
            activity.startActivity(ParkAssistMapActivity.buildIntent(activity, mapUrl, carLocation.getPosition()));
        }
    }
}