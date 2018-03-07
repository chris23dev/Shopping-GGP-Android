package com.ggp.theclub.adapter;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.MapLevelAdapter.MapLevelViewHolder;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.MapAmenityFilter;
import com.ggp.theclub.model.MapLevel;
import com.ggp.theclub.util.AnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapLevelAdapter extends RecyclerView.Adapter<MapLevelViewHolder> {
    private final int TYPE_TOP_BUTTON = 0;
    private final int TYPE_MIDDLE_BUTTON = 1;
    private final int TYPE_BOTTOM_BUTTON = 2;
    private final int MAX_LEVEL_LABEL_LENGTH = 6;
    private MapManager mapManager = MapManager.getInstance();
    private List<MapLevel> mapLevels = new ArrayList<>();
    private HashMap<Integer, Integer> filterIndicators = new HashMap<>();
    private boolean parkingIndicatorsEnabled;
    private MapAmenityFilter mapAmenityFilter;
    private int currentLevel;

    public MapLevelAdapter() {
        updateLevel(mapManager.getCurrentLevel());
    }

    public void setMapLevels(List<MapLevel> mapLevels) {
        this.mapLevels.clear();
        this.mapLevels.addAll(mapLevels);
        notifyDataSetChanged();
    }

    public void setFilterIndicator(int levelIndex, int number) {
        filterIndicators.put(levelIndex, number);
        notifyDataSetChanged();
    }

    public void clearFilterIndicators() {
        filterIndicators.clear();
        notifyDataSetChanged();
    }

    public void setAmenityFilterIndicator(MapAmenityFilter amenityFilter) {
        mapAmenityFilter = amenityFilter;
        notifyDataSetChanged();
    }

    public void enableParkingIndicators() {
        parkingIndicatorsEnabled = true;
        setMapLevels(mapManager.getParkingLevels());
    }

    public void updateLevel(int level) {
        currentLevel = level;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_BOTTOM_BUTTON;
        } else if (position == mapLevels.size() - 1) {
            return TYPE_TOP_BUTTON;
        }
        return TYPE_MIDDLE_BUTTON;
    }

    @Override
    public int getItemCount() {
        return mapLevels.size();
    }

    @Override
    public MapLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_level_item, parent, false);
        View button = view.findViewById(R.id.level_description_view);
        //All view holders are initially created as middle buttons.
        //conditionally set rounded corners on top and bottom buttons, margin on bottom button
        switch (viewType) {
            case TYPE_TOP_BUTTON:
                button.setBackgroundResource(R.drawable.map_level_top_view);
                break;
            case TYPE_BOTTOM_BUTTON:
                button.setBackgroundResource(R.drawable.map_level_bottom_view);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                layoutParams.bottomMargin = 0;
        }
        return new MapLevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapLevelViewHolder holder, int position) {
        holder.onBind(mapLevels.get(position).getDescription());
    }

    public class MapLevelViewHolder extends RecyclerView.ViewHolder {
        @BindColor(R.color.black) int unselectedTextColor;
        @BindColor(R.color.white) int selectedTextColor;
        @BindColor(R.color.translucent_white) int unselectedBackgroundColor;
        @BindColor(R.color.blue) int selectedBackgroundColor;
        @BindColor(R.color.green) int wayfindStartColor;
        @BindColor(R.color.red) int wayfindEndColor;
        @BindString(R.string.restroom_icon) String restroomAmenityIcon;
        @BindString(R.string.atm_icon) String atmAmenityIcon;
        @BindString(R.string.kiosk_icon) String kioskAmenityIcon;
        @BindString(R.string.management_icon) String managementAmenityIcon;
        @BindString(R.string.parking_icon) String parkingIcon;
        @BindString(R.string.location_icon) String locationIcon;
        @Bind(R.id.level_description_view) TextView levelDescriptionView;
        @Bind(R.id.text_indicator_view) TextView textIndicatorView;
        @Bind(R.id.icon_indicator_view) TextView iconIndicatorView;

        public MapLevelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBind(String levelDescription) {
            updateDescriptionView(levelDescription);
            if (currentLevel != getLevel()) {
                if (!filterIndicators.isEmpty()) {
                    AnimationUtils.exitReveal(iconIndicatorView);
                    updateTextIndicatorView();
                } else {
                    AnimationUtils.exitReveal(textIndicatorView);
                    updateIconIndicatorView();
                }
            } else {
                AnimationUtils.exitReveal(textIndicatorView);
                AnimationUtils.exitReveal(iconIndicatorView);
            }
        }

        @OnClick(R.id.level_description_view)
        public void onClick() {
            currentLevel = getLevel();
            mapManager.setCurrentLevel(currentLevel);
            notifyDataSetChanged();
        }

        private void updateDescriptionView(String levelDescription) {
            boolean isSelectedLevel = currentLevel == getLevel();
            int backgroundColor = isSelectedLevel ? selectedBackgroundColor : unselectedBackgroundColor;
            setIndicatorColor(levelDescriptionView, backgroundColor);
            levelDescriptionView.setTextColor(isSelectedLevel ? selectedTextColor : unselectedTextColor);
            levelDescriptionView.setText(levelDescription.substring(0, Math.min(levelDescription.length(), MAX_LEVEL_LABEL_LENGTH)));
        }

        private void updateTextIndicatorView() {
            int level = getLevel();

            if (filterIndicators.containsKey(level)) {
                Integer filterIndicatorNumber = filterIndicators.get(level);
                if (filterIndicatorNumber != null && filterIndicatorNumber > 0) {
                    textIndicatorView.setText(filterIndicatorNumber.toString());
                    AnimationUtils.enterReveal(textIndicatorView);
                    AnimationUtils.exitReveal(iconIndicatorView);
                } else {
                    AnimationUtils.exitReveal(textIndicatorView);
                }
            } else {
                AnimationUtils.exitReveal(textIndicatorView);
            }
        }

        private void updateIconIndicatorView() {
            int level = getLevel();
            boolean isAmenityLevel = mapManager.hasAmenitiesOnLevel(level);
            boolean isParkingLevel = mapManager.hasParkingOnLevel(level);
            boolean isWayfindLevel = mapManager.getStartWaypointLevel() == level || mapManager.getEndWaypointLevel() == level;

            if (isAmenityLevel) {
                showAmenityIcon();
            } else if (isParkingLevel && parkingIndicatorsEnabled) {
                showParkingIcon();
            } else if (isWayfindLevel) {
                showWayfindIcon();
            } else {
                hideIndicatorIcon();
            }
        }

        private void setIndicatorIcon(String icon) {
            iconIndicatorView.setText(icon);
            AnimationUtils.enterReveal(iconIndicatorView);
        }

        private void hideIndicatorIcon() {
            iconIndicatorView.setText(null);
            AnimationUtils.exitReveal(iconIndicatorView);
        }

        private void setIndicatorColor(View indicatorView, int color) {
            GradientDrawable gradientDrawable = (GradientDrawable) indicatorView.getBackground();
            gradientDrawable.setColor(color);
        }

        private void showAmenityIcon() {
            switch (mapAmenityFilter) {
                case RESTROOM: setIndicatorIcon(restroomAmenityIcon);
                    break;
                case ATM: setIndicatorIcon(atmAmenityIcon);
                    break;
                case KIOSK: setIndicatorIcon(kioskAmenityIcon);
                    break;
                case MANAGEMENT: setIndicatorIcon(managementAmenityIcon);
                    break;
                case NONE: hideIndicatorIcon();
                    break;
            }
            setIndicatorColor(iconIndicatorView, selectedBackgroundColor);
        }

        private void showParkingIcon() {
            setIndicatorIcon(parkingIcon);
            setIndicatorColor(iconIndicatorView, selectedBackgroundColor);
        }

        private void showWayfindIcon() {
            setIndicatorIcon(locationIcon);
            int backgroundColor = mapManager.getStartWaypointLevel() == getLevel() ? wayfindStartColor : wayfindEndColor;
            setIndicatorColor(iconIndicatorView, backgroundColor);
        }

        private int getLevel() {
            return mapLevels.get(getAdapterPosition()).getLevel();
        }
    }
}