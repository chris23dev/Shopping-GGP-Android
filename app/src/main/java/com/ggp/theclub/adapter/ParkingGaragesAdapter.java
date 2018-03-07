package com.ggp.theclub.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ParkingGaragesAdapter.ParkingGarageViewHolder;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.ParkingGarage;
import com.ggp.theclub.model.ParkingLevel;
import com.ggp.theclub.model.ParkingSite;
import com.ggp.theclub.model.ParkingZone;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.ParkingSiteUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParkingGaragesAdapter extends RecyclerView.Adapter<ParkingGarageViewHolder> {
    private Activity activity;
    private ParkingSite parkingSite;
    private List<ParkingZone> parkingZones;

    public ParkingGaragesAdapter(Activity activity, ParkingSite parkingSite, List<ParkingZone> parkingZones) {
        this.activity = activity;
        this.parkingSite = parkingSite;
        this.parkingZones = parkingZones;
    }

    @Override
    public ParkingGarageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_garage_item, parent, false);
        return new ParkingGarageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkingGarageViewHolder holder, int position) {
        holder.onBind(parkingSite.getGarages().get(position));
    }

    @Override
    public int getItemCount() {
        return parkingSite.getGarages().size();
    }

    public class ParkingGarageViewHolder extends RecyclerView.ViewHolder {
        @BindColor(R.color.green) int availableColor;
        @BindColor(R.color.red) int fullColor;
        @BindString(R.string.expand_icon) String expandIcon;
        @BindString(R.string.contract_icon) String contractIcon;
        @BindString(R.string.park_assist_spaces_full) String spacesFullText;
        @Bind(R.id.garage_view) TextView garageView;
        @Bind(R.id.description_view) TextView descriptionView;
        @Bind(R.id.directions_button) TextView directionsButton;
        @Bind(R.id.occupied_count_view) TextView occupiedCountView;
        @Bind(R.id.park_assist_spaces_occupied) TextView parkAssistSpacesOccupied;
        @Bind(R.id.available_count_view) TextView availableCountView;
        @Bind(R.id.available_label_view) TextView availableLabelView;
        @Bind(R.id.instructions_view) TextView instructionsView;
        @Bind(R.id.levels_button) TextView levelsButton;
        @Bind(R.id.availability_bar) ProgressBar availabilityBar;
        @Bind(R.id.parking_level_list) CustomRecyclerView parkingLevelList;

        public ParkingGarageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            instructionsView.setText(R.string.park_assist_levels_instruction);
            parkAssistSpacesOccupied.setText(R.string.park_assist_spaces_occupied);
            availableLabelView.setText(R.string.park_assist_spaces_available);
            directionsButton.setText(R.string.get_directions);
        }

        public void onBind(ParkingGarage parkingGarage) {
            List<ParkingLevel> parkingLevels = ParkingSiteUtils.findLevelsByGarageId(parkingGarage.getGarageId(), parkingSite.getLevels());
            HashMap<Integer, ParkingZone> levelToZoneLookup = ParkingSiteUtils.mapLevelsToZones(parkingLevels, parkingZones);
            int occupiedSpaces = ParkingSiteUtils.occupiedSpotsFromZones(levelToZoneLookup.values());
            int availableSpaces = ParkingSiteUtils.availableSpotsFromZones(levelToZoneLookup.values());

            garageView.setText(parkingGarage.getGarageName());
            if (StringUtils.isEmpty(parkingGarage.getGarageDescription())) {
                descriptionView.setVisibility(View.GONE);
            } else {
                descriptionView.setText(parkingGarage.getGarageDescription());
            }
            if (parkingGarage.getLatitude() == 0 && parkingGarage.getLongitude() == 0) {
                directionsButton.setVisibility(View.GONE);
            }
            occupiedCountView.setText(String.valueOf(occupiedSpaces));
            availableCountView.setText(availableSpaces == 0 ? spacesFullText : String.valueOf(availableSpaces));
            availableCountView.setTextColor(availableSpaces == 0 ? fullColor : availableColor);
            availableLabelView.setTextColor(availableSpaces == 0 ? fullColor : availableColor);
            availabilityBar.setMax(occupiedSpaces + availableSpaces);
            availabilityBar.setProgress(occupiedSpaces);
            parkingLevelList.setAdapter(new ParkingLevelsAdapter(parkingLevels, parkingZones));
        }

        private void trackLevelsClick() {
            MallApplication.getApp().getAnalyticsManager().trackAction(AnalyticsManager.Actions.ParkAssistLevels);
        }

        @OnClick(R.id.directions_button)
        public void onDirectionsButtonClick() {
            IntentUtils.showDirectionsForParkingGarage(parkingSite.getGarages().get(getAdapterPosition()), activity);
        }

        @OnClick(R.id.levels_button)
        public void onLevelsButtonClick() {
            if (parkingLevelList.isShown()) {
                parkingLevelList.setVisibility(View.GONE);
                instructionsView.setVisibility(View.VISIBLE);
                levelsButton.setText(expandIcon);
            } else {
                parkingLevelList.setVisibility(View.VISIBLE);
                instructionsView.setVisibility(View.INVISIBLE);
                levelsButton.setText(contractIcon);
                trackLevelsClick();
            }
        }
    }
}