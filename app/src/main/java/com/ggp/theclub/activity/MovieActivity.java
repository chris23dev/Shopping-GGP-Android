package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.DateListAdapter;
import com.ggp.theclub.adapter.TheaterShowtimesAdapter;
import com.ggp.theclub.event.DateSelectEvent;
import com.ggp.theclub.model.Movie;
import com.ggp.theclub.model.TheaterShowtime;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.MovieUtils;
import com.ggp.theclub.util.StringUtils;
import com.squareup.picasso.Callback;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;

public class MovieActivity extends BaseActivity {
    @BindColor(R.color.primary_color) int primaryColor;
    @BindColor(R.color.white) int white;
    @BindString(R.string.director_label) String directorLabel;
    @BindString(R.string.distributor_label) String distributorLabel;
    @BindString(R.string.hour_abbreviation) String hourAbbreviation;
    @BindString(R.string.minute_abbreviation) String minuteAbbreviation;
    @BindString(R.string.share_subject_format_movie) String shareSubjectFormat;
    @BindString(R.string.movie_share_format) String movieShareFormat;
    @Bind(R.id.date_picker_toolbar) Toolbar calendarRibbon;
    @Bind(R.id.appbar_layout) AppBarLayout appbarLayout;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.main_image) ImageView movieLogoView;
    @Bind(R.id.date_selector) RecyclerView dateSelector;
    @Bind(R.id.movie_showtimes) RecyclerView showtimesView;
    @Bind(R.id.no_showtimes_message) TextView noShowtimesView;
    @Bind(R.id.showtimes_header) TextView showtimeHeader;
    @Bind(R.id.rating_header) TextView ratingHeader;
    @Bind(R.id.rating_text) TextView ratingTextView;
    @Bind(R.id.runtime_header) TextView runtimeHeader;
    @Bind(R.id.runtime_text) TextView runtimeTextView;
    @Bind(R.id.genre_header) TextView genreHeader;
    @Bind(R.id.genre_text) TextView genreTextView;
    @Bind(R.id.plot_text) TextView plotTextView;
    @Bind(R.id.plot_header) TextView plotHeader;
    @Bind(R.id.cast_header) TextView castHeader;
    @Bind(R.id.cast_text) TextView castTextView;
    @Bind(R.id.more_info_header) TextView moreInfoView;
    @Bind(R.id.director_text) TextView directorTextView;
    @Bind(R.id.distributor_text) TextView distributorTextView;
    private Movie movie;
    private Boolean showTheaterHeaders;
    private LocalDate selectedDate = DateTime.now().toLocalDate();
    private final int NUM_CALENDAR_DATES = 7;

    public static Intent buildIntent(Context context, Movie movie, Boolean showTheaterHeaders) {
        return buildIntent(context, MovieActivity.class, movie, showTheaterHeaders);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = getIntentExtra(Movie.class);
        showTheaterHeaders = getIntentExtra(Boolean.class);
        setContentView(R.layout.movie_activity);
        noShowtimesView.setText(getString(R.string.no_showtimes_today));
        showtimeHeader.setText(getString(R.string.showtimes_header));
        plotHeader.setText(getString(R.string.plot_summary_header));
        ratingHeader.setText(getString(R.string.rating_header));
        runtimeHeader.setText(getString(R.string.runtime_header));
        genreHeader.setText(getString(R.string.genre_header));
        castHeader.setText(getString(R.string.cast_header));
        moreInfoView.setText(getString(R.string.more_info_header));
    }

    @Override
    protected void configureView() {
        enableBackButton();
        setIconActionButton(R.string.share_icon);

        ImageUtils.loadImage(movie.getLargePosterImageUrl(), movieLogoView, new Callback() {
            @Override
            public void onSuccess() {
                setToolbarColors();
            }

            @Override
            public void onError() {}
        });

        String runtimeText = StringUtils.minutesToPrettyTime(movie.getRunTimeInMinutes(), hourAbbreviation, minuteAbbreviation);
        runtimeTextView.setText(runtimeText);
        ratingTextView.setText(movie.getMpaaRating());
        genreTextView.setText(StringUtils.characterSeparatedString(movie.getGenres(), "/"));
        plotTextView.setText(movie.getSynopsis());
        castTextView.setText(StringUtils.characterSeparatedString(movie.getActors(), ", "));

        moreInfoView.setVisibility(StringUtils.isEmpty(movie.getDirector() + movie.getDistributor()) ? View.GONE : View.VISIBLE);
        directorTextView.setText(directorLabel + " " + movie.getDirector());
        directorTextView.setVisibility(StringUtils.isEmpty(movie.getDirector()) ? View.GONE : View.VISIBLE);
        distributorTextView.setText(distributorLabel + " " + movie.getDistributor());
        distributorTextView.setVisibility(StringUtils.isEmpty(movie.getDistributor()) ? View.GONE : View.VISIBLE);

        setDateList();
        setShowtimes();
        updateMovieShowtimes();
        setParallaxToolbar();
    }

    private void updateMovieShowtimes() {
        List<TheaterShowtime> filteredShowtimes = MovieUtils.getTheaterShowtimesForDate(movie, selectedDate);
        showtimesView.setAdapter(new TheaterShowtimesAdapter(this, movie, filteredShowtimes, selectedDate, showTheaterHeaders));
        noShowtimesView.setVisibility(filteredShowtimes.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void setDateList() {
        List<LocalDate> availableDates = new ArrayList<>();
        for(int i = 0; i < NUM_CALENDAR_DATES; i++) {
            LocalDate date = new LocalDate().plusDays(i);
            availableDates.add(date);
        }

        dateSelector.setLayoutManager(new LinearLayoutManager(dateSelector.getContext(), LinearLayoutManager.HORIZONTAL, false));
        dateSelector.setAdapter(new DateListAdapter(availableDates));
    }

    private void setParallaxToolbar() {
        collapsingToolbar.setTitle(movie.getTitle());
        appbarLayout.setExpanded(true, true);
        setToolbarColors();
    }

    private void setToolbarColors() {
        //TODO: add this back once we get approval
//        int matchColor = ImageUtils.findMatchingColor(movieLogoView, primaryColor);
        collapsingToolbar.setBackgroundColor(primaryColor);
        collapsingToolbar.setContentScrimColor(primaryColor);
        collapsingToolbar.setCollapsedTitleTextColor(white);
        collapsingToolbar.setExpandedTitleColor(white);
    }

    private void setShowtimes() {
        showtimesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        showtimesView.setNestedScrollingEnabled(false);
        updateMovieShowtimes();
    }

    public void onEvent(DateSelectEvent event) {
        selectedDate = event.getDate();
        updateMovieShowtimes();
    }

    public void onActionButtonClick() {
        String movieSubject = String.format(shareSubjectFormat, movie.getTitle());
        String movieBody = String.format(movieShareFormat, movie.getTitle(), mallManager.getMall().getWebsiteUrl(), movie.getId());
        IntentUtils.startShareChooser(movieSubject, movieBody, this);
    }
}