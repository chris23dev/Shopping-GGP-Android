package com.ggp.theclub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ParkingLevelsAdapter.ParkingLevelViewHolder;
import com.ggp.theclub.model.ParkingLevel;
import com.ggp.theclub.model.ParkingZone;
import com.ggp.theclub.model.ParkingZone.ParkingZoneCounts;
import com.ggp.theclub.util.ParkingSiteUtils;
import com.ggp.theclub.util.StringUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;

public class ParkingLevelsAdapter extends RecyclerView.Adapter<ParkingLevelViewHolder> {
    private List<ParkingLevel> parkingLevels;
    private HashMap<Integer, ParkingZone> levelToZoneLookup = new HashMap<>();

    public ParkingLevelsAdapter(List<ParkingLevel> parkingLevels, List<ParkingZone> parkingZones) {
        this.parkingLevels = parkingLevels;
        levelToZoneLookup = ParkingSiteUtils.mapLevelsToZones(parkingLevels, parkingZones);
    }

    @Override
    public ParkingLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_level_item, parent, false);
        return new ParkingLevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkingLevelViewHolder holder, int position) {
        holder.onBind(parkingLevels.get(position));
    }

    @Override
    public int getItemCount() {
        return parkingLevels.size();
    }

    public class ParkingLevelViewHolder extends RecyclerView.ViewHolder {
        @BindColor(R.color.green) int availableColor;
        @BindColor(R.color.red) int fullColor;
        @BindString(R.string.park_assist_spaces_full) String spacesFullText;
        @Bind(R.id.level_view) TextView levelView;
        @Bind(R.id.description_view) TextView descriptionView;
        @Bind(R.id.available_view) TextView availableView;
        @Bind(R.id.availability_bar) ProgressBar availabilityBar;

        public ParkingLevelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(ParkingLevel parkingLevel) {
            ParkingZoneCounts parkingZoneCounts = levelToZoneLookup.get(parkingLevel.getLevelId()).getCounts();
            int occupiedSpaces = parkingZoneCounts.getOccupied();
            int availableSpaces = parkingZoneCounts.getAvailable();

            levelView.setText(parkingLevel.getLevelName());
            if (StringUtils.isEmpty(parkingLevel.getLevelDescription())) {
                descriptionView.setVisibility(View.GONE);
            } else {
                descriptionView.setText(parkingLevel.getLevelDescription());
            }
            availableView.setText(availableSpaces == 0 ? spacesFullText : String.valueOf(availableSpaces));
            availableView.setTextColor(availableSpaces == 0 ? fullColor : availableColor);
            availabilityBar.setMax(occupiedSpaces + availableSpaces);
            availabilityBar.setProgress(occupiedSpaces);
        }
    }
}