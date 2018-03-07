package com.ggp.theclub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.R;
import com.ggp.theclub.event.TimeRangeSelectEvent;
import com.ggp.theclub.model.ParkingTimeRange;
import com.ggp.theclub.util.ParkingUtils;
import com.ggp.theclub.view.ParkingSelectViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class ParkingTimeListAdapter extends RecyclerView.Adapter<ParkingTimeListAdapter.ParkingTimeViewHolder> {
    private final String LOG_TAG = ParkingTimeListAdapter.class.getSimpleName();
    private ArrayList<ParkingTimeRange> timeList = new ArrayList<>();
    private int selectedItem = 0;
    boolean firstElementHidden = false;

    public ParkingTimeListAdapter() {
        createTimeList();
    }
    
    @Override
    public int getItemCount() {
        return timeList.size();
    }

    @Override
    public ParkingTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_availability_select_item, parent, false);
        return new ParkingTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkingTimeViewHolder holder, int position) {
        boolean active = position == selectedItem;
        holder.onBind(active, ParkingUtils.getTimeRangeText(timeList.get(position)), getIconText(timeList.get(position)));
    }
    
    private void setSelectedItem(int position) {
        if(getItemCount() > position) {
            selectedItem = position;
            notifyDataSetChanged();
        }
    }

    private void createTimeList() {
        this.timeList.clear();
        this.timeList.addAll(getParkingTimeRanges());
        notifyDataSetChanged();
    }

    private List<ParkingTimeRange> getParkingTimeRanges() {
        List<ParkingTimeRange> availableTimes = Arrays.asList(ParkingTimeRange.values());
        if (firstElementHidden) {
            availableTimes = StreamSupport.stream(availableTimes)
                    .filter(timeRange -> !(firstElementHidden && timeRange.equals(ParkingTimeRange.NOW)))
                    .collect(Collectors.toList());
        }
        return availableTimes;
    }

    private int getIconText(ParkingTimeRange timeRange) {
        switch(timeRange) {
            case NOW: return R.string.clock_icon;
            case MORNING: return R.string.morning_icon;
            case AFTERNOON: return R.string.afternoon_icon;
            case EVENING: return R.string.evening_icon;
            default: return R.string.clock_icon;
        }
    }

    public void setFirstElementHidden(boolean hidden) {
        boolean hiddenToggled = firstElementHidden != hidden;
        int itemToSelect = selectedItem;
        if(hidden && hiddenToggled) {
            itemToSelect = selectedItem - 1;
        } else if(hiddenToggled) {
            itemToSelect = selectedItem + 1;
        }
        firstElementHidden = hidden;
        createTimeList();
        setSelectedItem(itemToSelect);
    }

    public class ParkingTimeViewHolder extends ParkingSelectViewHolder {
        @BindString(R.string.today_text) String todayText;

        public ParkingTimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(boolean isActive, String timeText, int iconText) {
            headerTextView.setText(timeText);
            mainTextView.setVisibility(View.GONE);
            iconView.setVisibility(View.VISIBLE);
            iconView.setText(iconText);

            setActive(isActive);
        }

        @OnClick(R.id.date_container)
        public void onClick() {
            setSelectedItem(getAdapterPosition());
            EventBus.getDefault().post(new TimeRangeSelectEvent(timeList.get(selectedItem)));
        }

    }
}
