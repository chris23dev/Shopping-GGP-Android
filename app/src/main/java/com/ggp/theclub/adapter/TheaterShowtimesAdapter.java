package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.model.Movie;
import com.ggp.theclub.model.MovieShowtime;
import com.ggp.theclub.model.TheaterShowtime;
import com.ggp.theclub.util.MovieUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TheaterShowtimesAdapter extends RecyclerView.Adapter<TheaterShowtimesAdapter.TheaterShowtimeViewHolder> {
    private Context context;
    private Movie movie;
    private LocalDate selectedDate = DateTime.now().toLocalDate();
    private boolean showTheaterNames;
    protected List<TheaterShowtime> theaterShowtimes = new ArrayList<>();

    public TheaterShowtimesAdapter(Context context, Movie movie, List<TheaterShowtime> theaterShowtimes, LocalDate selectedDate, boolean showTheaterNames) {
        this.context = context;
        this.movie = movie;
        this.theaterShowtimes = theaterShowtimes;
        this.selectedDate = selectedDate;
        this.showTheaterNames = showTheaterNames;
    }

    @Override
    public TheaterShowtimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theater_showtime, parent, false);
        return new TheaterShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TheaterShowtimeViewHolder holder, int position) {
        holder.onBind(theaterShowtimes.get(position));
    }

    @Override
    public int getItemCount() {
        return theaterShowtimes.size();
    }

    public class TheaterShowtimeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.theater_name) TextView theaterName;
        @Bind(R.id.movie_showtimes) RecyclerView showtimesView;
        private final int NUM_SHOWTIME_COLUMNS = 3;

        public TheaterShowtimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            showtimesView.setLayoutManager(new GridLayoutManager(itemView.getContext(), NUM_SHOWTIME_COLUMNS));
            showtimesView.setNestedScrollingEnabled(false);
        }

        public void onBind(TheaterShowtime theaterShowtime) {
            List<MovieShowtime> filteredShowtimes = MovieUtils.getShowtimesForDate(theaterShowtime, selectedDate);
            showtimesView.setAdapter(new MovieShowtimesAdapter(context, movie, filteredShowtimes, theaterShowtime.getTheaterUrl(), theaterShowtime.getTmsId()));
            theaterName.setText(theaterShowtime.getTheaterName());
            theaterName.setVisibility(filteredShowtimes.isEmpty() || !showTheaterNames ? View.GONE : View.VISIBLE);
        }
    }
}