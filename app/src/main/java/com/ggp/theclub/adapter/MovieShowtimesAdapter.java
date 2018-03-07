package com.ggp.theclub.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.adapter.MovieShowtimesAdapter.MovieShowtimeViewHolder;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.Movie;
import com.ggp.theclub.model.MovieShowtime;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.MovieUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieShowtimesAdapter extends RecyclerView.Adapter<MovieShowtimeViewHolder> {
    private Context context;
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();
    private Movie movie;
    private String theaterUrl;
    private String tmsId;
    protected List<MovieShowtime> movieShowtimes = new ArrayList<>();

    public MovieShowtimesAdapter(Context context, Movie movie, List<MovieShowtime> movieShowtimes, String theaterUrl, String tmsId) {
        this.context = context;
        this.movie = movie;
        this.movieShowtimes = movieShowtimes;
        this.theaterUrl = theaterUrl;
        this.tmsId = tmsId;
    }

    @Override
    public MovieShowtimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_showtime, parent, false);
        return new MovieShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieShowtimeViewHolder holder, int position) {
        holder.onBind(movieShowtimes.get(position));
    }

    @Override
    public int getItemCount() {
        return movieShowtimes.size();
    }

    public void setMovieShowtimes(List<MovieShowtime> showtimes) {
        movieShowtimes.clear();
        movieShowtimes.addAll(showtimes);
        notifyDataSetChanged();
    }

    public class MovieShowtimeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.showtime_button) AppCompatButton showtimeView;
        @BindColor(R.color.gray) int gray;
        @BindColor(R.color.blue) int blue;

        private boolean isDisabled;

        public MovieShowtimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(MovieShowtime showtime) {
            DateTime movieTime = new DateTime(showtime.getMovieShowtime());
            isDisabled = movieTime.isBeforeNow();
            setButtonColor();
            showtimeView.setText(DateTimeFormat.shortTime().print(movieTime));
        }

        private void trackBuyTickets(String movieTitle, MovieShowtime showtime) {
            LocalDate showDate = new LocalDate(showtime.getMovieShowtime());
            int daysAhead = Days.daysBetween(LocalDate.now(), showDate).getDays();
            
            HashMap<String, Object> contextData = new HashMap<String, Object>(){{
                put(AnalyticsManager.ContextDataKeys.BuyTicketsMovieName, movieTitle);
                put(AnalyticsManager.ContextDataKeys.BuyTicketsDaysInAdvance, daysAhead > 0 ? daysAhead : "");
            }};
            analyticsManager.trackAction(AnalyticsManager.Actions.BuyTickets, contextData);
        }

        private void setButtonColor() {
            int buttonColor = isDisabled ? gray : blue;
            ColorStateList colorStateList = new ColorStateList(new int[][] {{0}}, new int[] {buttonColor});
            showtimeView.setSupportBackgroundTintList(colorStateList);
        }

        @OnClick(R.id.showtime_button)
        protected void showtimeClicked() {
            if(!isDisabled) {
                MovieShowtime showtime = movieShowtimes.get(getAdapterPosition());
                String ticketsUrl = MovieUtils.buildPurchaseTicketsUrl(movie, showtime, theaterUrl, tmsId);
                trackBuyTickets(movie.getTitle(), showtime);
                IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(ticketsUrl)), (Activity) context);
            }
        }
    }
}