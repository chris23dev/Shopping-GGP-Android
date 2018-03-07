package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.MovieActivity;
import com.ggp.theclub.adapter.MovieListAdapter.MovieViewHolder;
import com.ggp.theclub.model.Movie;
import com.ggp.theclub.model.TheaterShowtime;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.MovieUtils;
import com.ggp.theclub.util.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    @Bind(R.id.no_showtimes_message) TextView noShowtimeMessage;
    private Context context;
    private LocalDate selectedDate = DateTime.now().toLocalDate();
    private boolean showTheaterNames;
    protected List<Movie> movieList = new ArrayList<>();
    protected List<Movie> filteredMovieList = new ArrayList<>();

    public MovieListAdapter(Context context, List<Movie> movies, boolean showTheaterNames) {
        this.context = context;
        this.showTheaterNames = showTheaterNames;
        movieList = movies;
        updateFilteredMovies();
    }

    @Override
    public int getItemCount() {
        return filteredMovieList.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        ButterKnife.bind(this,view);
        noShowtimeMessage.setText(context.getString(R.string.no_showtimes_today));
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.onBind(filteredMovieList.get(position));
    }

    public void setSelectedDate(LocalDate date) {
        selectedDate = date;
        updateFilteredMovies();
    }

    private void updateFilteredMovies() {
        filteredMovieList = MovieUtils.sortMovies(MovieUtils.getMoviesWithTheaterShowtimes(movieList, selectedDate));
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_title) TextView titleTextView;
        @Bind(R.id.movie_rating) TextView ratingTextView;
        @Bind(R.id.movie_runtime) TextView runtimeTextView;
        @Bind(R.id.theater_showtimes) RecyclerView showtimesView;
        @Bind(R.id.movie_image) ImageView movieImageView;
        @Bind(R.id.no_showtimes_message) TextView noShowtimesView;
        @BindString(R.string.hour_abbreviation) String hourAbbreviation;
        @BindString(R.string.minute_abbreviation) String minuteAbbreviation;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            showtimesView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            showtimesView.setNestedScrollingEnabled(false);
        }

        public void onBind(Movie movie) {
            ImageUtils.loadImage(movie.getLargePosterImageUrl(), movieImageView);
            titleTextView.setText(movie.getTitle());
            ratingTextView.setText(movie.getMpaaRating());
            runtimeTextView.setText(StringUtils.minutesToPrettyTime(movie.getRunTimeInMinutes(), hourAbbreviation, minuteAbbreviation));

            List<TheaterShowtime> filteredShowtimes = MovieUtils.getTheaterShowtimesForDate(movie, selectedDate);
            showtimesView.setAdapter(new TheaterShowtimesAdapter(context, movie, filteredShowtimes, selectedDate, showTheaterNames));
            noShowtimesView.setVisibility(filteredShowtimes.size() > 0 ? View.GONE : View.VISIBLE);
        }

        @OnClick(R.id.movie_card)
        public void onMovieClick() {
            context.startActivity(MovieActivity.buildIntent(context, filteredMovieList.get(getAdapterPosition()), showTheaterNames));
        }
    }
}