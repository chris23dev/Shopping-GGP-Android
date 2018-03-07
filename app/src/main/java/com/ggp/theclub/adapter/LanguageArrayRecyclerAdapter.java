package com.ggp.theclub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.ggp.theclub.customlocale.gateway.Language;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by john.curtis on 5/22/17.
 */
public class LanguageArrayRecyclerAdapter extends ArrayRecyclerAdapter<Language, LanguageArrayRecyclerAdapter.LanguageViewHolder> {

    private int mLastCheckedPosition = -1;

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_checked, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        holder.onBind(get(position), position);
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder {
        @Bind(android.R.id.text1)
        CheckedTextView mLanguageDisplay;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(Language mallEvent, int position) {
            mLanguageDisplay.setText(mallEvent.name);
            mLanguageDisplay.setChecked(mLastCheckedPosition == position);
        }
    }

    public void setChecked(int position){
        mLastCheckedPosition = position;
        notifyDataSetChanged();
    }
}
