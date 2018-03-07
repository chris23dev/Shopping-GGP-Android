package com.ggp.theclub.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListItemHeaderViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.header_view) TextView headerView;

    public ListItemHeaderViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void onBind(String headerText) {
        headerView.setText(headerText);
        itemView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        itemView.setVisibility(View.INVISIBLE);
    }
}