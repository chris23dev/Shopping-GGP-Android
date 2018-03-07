package com.ggp.theclub.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.DateListAdapter.DateViewHolder;
import com.ggp.theclub.event.DateSelectEvent;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class DateListAdapter extends RecyclerView.Adapter<DateViewHolder> {
    private final String LOG_TAG = DateListAdapter.class.getSimpleName();
    private ArrayList<LocalDate> dateList = new ArrayList<>();
    private int selectedItem = 0;

    public DateListAdapter(List<LocalDate> dates) {
        createDateList(dates);
    }
    
    @Override
    public int getItemCount() {
        return dateList.size();
    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {
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

    public class DateViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.day_text) TextView dayTextView;
        @Bind(R.id.month_text) TextView monthTextView;
        @Bind(R.id.date_text) TextView dateTextView;
        @BindString(R.string.today_text) String todayText;
        @BindColor(R.color.blue) int blue;
        @BindColor(R.color.black) int black;
        @BindColor(R.color.white) int white;
        @BindColor(R.color.dark_gray) int darkGray;

        public DateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(LocalDate date, boolean isActive) {
            String dayText = date.equals(new LocalDate()) ? todayText : date.dayOfWeek().getAsShortText();
            dayTextView.setText(dayText);
            monthTextView.setText(date.monthOfYear().getAsShortText());
            dateTextView.setText(date.dayOfMonth().getAsString());

            setActive(isActive);
        }

        @OnClick(R.id.date_container)
        public void onClick() {
            setSelectedItem(getAdapterPosition());
            EventBus.getDefault().post(new DateSelectEvent(dateList.get(selectedItem)));
        }

        public void setActive(boolean active) {
            if(active) {
                dayTextView.setBackgroundColor(blue);
                monthTextView.setBackgroundColor(white);
                monthTextView.setTextColor(black);
                dateTextView.setBackgroundColor(white);
                dateTextView.setTextColor(black);
                dateTextView.setTypeface(null, Typeface.BOLD);
            } else {
                dayTextView.setBackgroundColor(black);
                monthTextView.setBackgroundColor(darkGray);
                monthTextView.setTextColor(white);
                dateTextView.setBackgroundColor(darkGray);
                dateTextView.setTextColor(white);
                dateTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}
