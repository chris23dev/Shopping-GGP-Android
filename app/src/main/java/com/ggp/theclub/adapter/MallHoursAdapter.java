package com.ggp.theclub.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.model.OperatingHours;
import com.ggp.theclub.model.OperatingHoursException;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.StringUtils;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MallHoursAdapter extends RecyclerView.Adapter {
    private final String LOG_TAG = getClass().getSimpleName();
    private Set<OperatingHours> operatingHours = MallApplication.getApp().getMallManager().getMall().getOperatingHours();
    private Set<OperatingHoursException> operatingHoursExceptions = MallApplication.getApp().getMallManager().getMall().getOperatingHoursExceptions();
    private List<LocalDate> dateList = new ArrayList<>();
    private boolean showHoursLabels;
    private boolean showDayLabels;

    private enum ItemViewType {HOURS, MONTH_HEADER}

    public MallHoursAdapter(List<LocalDate> dateList, boolean showMonthLabels, boolean showHoursLabels, boolean showDayLabels) {
        this.showHoursLabels = showHoursLabels;
        this.showDayLabels = showDayLabels;
        //if month labels should be shown add a nulls to the list to mark where they should go.
        this.dateList = showMonthLabels ? addNullsForMonthChange(dateList) : dateList;
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //month headers can't go at the end of the list
        return dateList.get(position) == null && position < dateList.size() - 1 ? ItemViewType.MONTH_HEADER.ordinal() : ItemViewType.HOURS.ordinal();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemViewType.HOURS.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mall_hours_item, parent, false);
            return new MallHoursViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mall_hours_month_header, parent, false);
            return new MonthHeaderViewHolder(view);
        }
    }

    /**
     * Add nulls to mark where the month headers should go
     */
    private List<LocalDate> addNullsForMonthChange(List<LocalDate> dateList) {
        List<LocalDate> result = new ArrayList<>();
        Integer currentMonth = null;
        for (LocalDate date : dateList) {
            if (currentMonth == null || date.getMonthOfYear() != currentMonth) {
                result.add(null);
            }
            currentMonth = date.getMonthOfYear();
            result.add(date);
        }
        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == ItemViewType.HOURS.ordinal()) {
            ((MallHoursViewHolder) holder).onBind(dateList.get(position));
        } else {
            //month header should be month of next date
            String monthName;
            if (dateList.size() < position + 1) {
                monthName = dateList.get(position + 1).toString("MMMM", Locale.US);
            } else {
                monthName = null;
            }
            ((MonthHeaderViewHolder) holder).onBind(monthName);
        }
    }

    public class MallHoursViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.day_text) TextView dayView;
        @Bind(R.id.date_text) TextView dateView;
        @Bind(R.id.hours_text) TextView hoursView;
        @Bind(R.id.hours_label) TextView hoursLabel;

        public MallHoursViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(LocalDate date) {

            dayView.setText(date.dayOfWeek().getAsShortText());
            dateView.setText(date.monthOfYear().getAsShortText() + " " + date.dayOfMonth().getAsString());
            HoursUtils.FormattedHours formattedHours = HoursUtils.getMultiLineHoursByDate(operatingHours, operatingHoursExceptions, date);
            hoursView.setText(formattedHours.getMultiLineHoursString());
            if (LocalDate.now().equals(date)) {
                setHighlighted();
            }
            if (showHoursLabels && !StringUtils.isEmpty(formattedHours.getHoursLabelString())) {
                hoursLabel.setText(formattedHours.getHoursLabelString());
                hoursLabel.setVisibility(View.VISIBLE);
            } else {
                hoursLabel.setVisibility(View.GONE);
            }

            dayView.setVisibility(showDayLabels ? View.VISIBLE : View.INVISIBLE);
        }

        private void setHighlighted() {
            dayView.setTypeface(null, Typeface.BOLD);
            dateView.setTypeface(null, Typeface.BOLD);
            hoursView.setTypeface(null, Typeface.BOLD);
        }
    }

    public class MonthHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind (R.id.month_name) TextView monthNameView;
        public MonthHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(String monthName) {
            monthNameView.setText(monthName);
        }
    }
}