package com.ggp.theclub.view;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class ParkingSelectViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.view_container) protected FrameLayout viewContainer;
    @Bind(R.id.date_container) protected LinearLayout dateContainer;
    @Bind(R.id.header_text) protected TextView headerTextView;
    @Bind(R.id.main_text) protected TextView mainTextView;
    @Bind(R.id.icon) protected TextView iconView;
    @Bind(R.id.shadow) protected View shadowView;
    @BindColor(R.color.primary_blue) protected int blue;
    @BindColor(R.color.white) protected int white;
    @BindColor(R.color.dark_gray) protected int darkGray;
    @BindColor(R.color.light_gray) protected int lightGray;

    public ParkingSelectViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setActive(boolean active) {
        if (active) {
            shadowView.setVisibility(View.VISIBLE);
            dateContainer.setBackgroundColor(lightGray);
            headerTextView.setTextColor(darkGray);
            mainTextView.setTextColor(darkGray);
            iconView.setTextColor(darkGray);
            mainTextView.setTypeface(null, Typeface.BOLD);
        } else {
            shadowView.setVisibility(View.INVISIBLE);
            dateContainer.setBackgroundColor(white);
            headerTextView.setTextColor(blue);
            mainTextView.setTextColor(blue);
            iconView.setTextColor(blue);
            mainTextView.setTypeface(null, Typeface.NORMAL);
        }
    }
}