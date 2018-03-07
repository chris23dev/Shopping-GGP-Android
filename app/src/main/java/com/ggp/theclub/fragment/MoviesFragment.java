package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.TenantActivity;
import com.ggp.theclub.adapter.DateListAdapter;
import com.ggp.theclub.adapter.MovieListAdapter;
import com.ggp.theclub.adapter.TheaterListAdapter;
import com.ggp.theclub.event.DateSelectEvent;
import com.ggp.theclub.event.TheaterCountChangedEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.FeedbackManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import org.joda.time.LocalDate;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import lombok.Setter;

public class MoviesFragment extends BaseFragment {
    @Bind(R.id.calendar_ribbon) Toolbar calendarRibbon;
    @Bind(R.id.date_selector) CustomRecyclerView dateSelector;
    @Bind(R.id.movie_list) CustomRecyclerView movieListView;
    @Bind(R.id.no_movies_view) LinearLayout noMoviesMessage;
    @Bind(R.id.no_movies_message) TextView noMoviesMessageTitle;
    @Bind(R.id.theater_list) CustomRecyclerView theaterListView;
    @Bind(R.id.theater_name) TextView theaterNameView;
    @Bind(R.id.theater_location) TextView theaterLocationView;
    @Bind(R.id.text_logo) TextView logoNameView;
    @Bind(R.id.image_logo) ImageView logoImageView;
    @Bind(R.id.theater_header) View singleTheaterHeader;
    @Bind(R.id.select_different_date) TextView selectDifferentDate;

    private final int DATE_PICKER_DAYS = 7;
    private FeedbackManager feedbackManager = MallApplication.getApp().getFeedbackManager();
    private MovieListAdapter movieListAdapter;
    @Setter private List<MovieTheater> movieTheaters;

    public static MoviesFragment newInstance() {
        return new MoviesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        feedbackManager.incrementFeedbackEventCount(getActivity());
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Movies);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.movies_fragment, container);
        noMoviesMessageTitle.setText(getString(R.string.no_movies_message));
        selectDifferentDate.setText(getString(R.string.select_different_date));
        return view;
    }

    @Override
    protected void configureView() {
        theaterListView.setDataLoaded(false);
        setTheaterList();
        setDateList();
    }

    private void setTheaterList() {
        mallRepository.queryForTheaters(theaters -> {
            setMovieTheaters(theaters);
            setHeader();
            setMovieList(theaters.size() > 1);
        });
    }

    private void setHeader() {
        if(movieTheaters == null) {
            return;
        }

        boolean isMultiTheater = movieTheaters.size() > 1;

        if(isMultiTheater) {
            theaterListView.setAdapter(new TheaterListAdapter(getActivity(), movieTheaters));
        } else if (movieTheaters.size() == 1) {
            MovieTheater movieTheater = movieTheaters.get(0);
            theaterNameView.setText(movieTheater.getName());
            theaterLocationView.setText(MapManager.getInstance().getTenantLocationByLeaseId(movieTheater.getLeaseId()));
            ImageUtils.setLogo(logoImageView, logoNameView, movieTheater.getLogoUrl(), movieTheater.getName());
        }

        setHeaderVisibility(isMultiTheater);
    }

    private void setHeaderVisibility(boolean isMultiTheater) {
        singleTheaterHeader.setVisibility(isMultiTheater ? View.GONE : View.VISIBLE);
        theaterListView.setVisibility(isMultiTheater ? View.VISIBLE : View.GONE);
    }

    private void setDateList() {
        List<LocalDate> availableDates = DateUtils.createDateList(DATE_PICKER_DAYS);
        if (!availableDates.isEmpty()) {
            calendarRibbon.setVisibility(View.VISIBLE);
            dateSelector.setAdapter(new DateListAdapter(availableDates));
        }
    }

    private void setMovieList(boolean showTheaterNames) {
        mallRepository.queryForMovies(movies -> {
            movieListAdapter = new MovieListAdapter(getActivity(), movies, showTheaterNames);
            movieListView.setAdapter(movieListAdapter);
            movieListView.setDataLoaded(true);
            checkMoviesAvailable();
        });
    }

    private void checkMoviesAvailable() {
        noMoviesMessage.setVisibility(movieListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public void onEvent(DateSelectEvent event) {
        movieListAdapter.setSelectedDate(event.getDate());
        checkMoviesAvailable();
    }

    public void onEvent(TheaterCountChangedEvent event) {
        configureView();
    }

    @OnClick(R.id.theater_header)
    public void onClick() {
        MovieTheater theater = movieTheaters != null && !movieTheaters.isEmpty() ? movieTheaters.get(0) : null;
        if(theater != null) {
            startActivity(TenantActivity.buildIntent(getActivity(), theater));
        }
    }
}