package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.TenantActivity;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.util.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TheaterListAdapter extends RecyclerView.Adapter<TheaterListAdapter.TheaterListViewHolder>{

    private Context context;
    private List<MovieTheater> movieTheaters = new ArrayList<>();

    public TheaterListAdapter(Context context, List<MovieTheater> movieTheaters) {
        this.context = context;
        this.movieTheaters = movieTheaters;
    }

    @Override
    public TheaterListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_theater_item, parent, false);
        return new TheaterListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return movieTheaters.size();
    }

    @Override
    public void onBindViewHolder(TheaterListViewHolder holder, int position) {
        holder.onBind(movieTheaters.get(position));
    }


    public class TheaterListViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.theater_name) TextView nameTextView;
        @Bind(R.id.image_logo) ImageView logoImageView;
        @Bind(R.id.text_logo) TextView logoNameView;
        @Bind(R.id.theater_location) TextView locationTextView;

        private MovieTheater movieTheater;

        public TheaterListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBind(MovieTheater movieTheater) {
            this.movieTheater = movieTheater;
            nameTextView.setText(movieTheater.getName());
            locationTextView.setText(MapManager.getInstance().getTenantLocationByLeaseId(movieTheater.getLeaseId()));
            ImageUtils.setLogo(logoImageView, logoNameView, movieTheater.getLogoUrl(), movieTheater.getName());
        }

        private void trackSelectedTheater(String theaterName) {
            if (getItemCount() > 1) {
                HashMap<String, Object> contextData = new HashMap<>();
                AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();
                analyticsManager.safePut(AnalyticsManager.ContextDataKeys.MultiTheaterMallTheater, theaterName, contextData);
                analyticsManager.trackAction(AnalyticsManager.Actions.MultiTheaterMallTheater, contextData);
            }
        }

        @OnClick(R.id.item_view)
        public void onClick() {
            context.startActivity(TenantActivity.buildIntent(context, movieTheater));
            trackSelectedTheater(movieTheater.getName());
        }
    }
}