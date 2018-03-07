package com.ggp.theclub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.R;
import com.ggp.theclub.event.DateSelectEvent;
import com.ggp.theclub.view.ParkingSelectViewHolder;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ParkingDateListAdapter extends RecyclerView.Adapter<ParkingDateListAdapter.ParkingDateViewHolder> {
    private final String LOG_TAG = ParkingDateListAdapter.class.getSimpleName();
    private ArrayList<LocalDate> dateList = new ArrayList<>();
    private int selectedItem = 0;

    public ParkingDateListAdapter(List<LocalDate> dates) {
        createDateList(dates);
    }
    
    @Override
    public int getItemCount() {
        return dateList.size();
    }

    @Override
    public ParkingDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_availability_select_item, parent, false);
        return new ParkingDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkingDateViewHolder holder, int position) {
        boolean active = position == selectedItem;
        holder.onBind(dateList.get(position), active);
    }
    
    private void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    public void createDateList(List<LocalDate> dateList) {
        this.dateList.clear();
        this.dateList.addAll(dateList);
        notifyDataSetChanged();
    }

    public class ParkingDateViewHolder extends ParkingSelectViewHolder {
        @BindString(R.string.today_text) String todayText;

        public ParkingDateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(LocalDate date, boolean isActive) {
            String dayText = date.equals(new LocalDate()) ? todayText : date.dayOfWeek().getAsShortText();
            headerTextView.setText(dayText);
            mainTextView.setText(date.dayOfMonth().getAsString());

            setActive(isActive);
        }

        @OnClick(R.id.date_container)
        public void onClick() {
            setSelectedItem(getAdapterPosition());
            EventBus.getDefault().post(new DateSelectEvent(dateList.get(selectedItem)));
        }

    }
}
