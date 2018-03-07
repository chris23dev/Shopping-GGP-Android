package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.AccountRegistrationActivity;
import com.ggp.theclub.manager.AnalyticsManager;

import butterknife.Bind;
import butterknife.OnClick;

public class HomeFeedUnauthenticatedFragment extends HomeFeedBaseFragment {

    @Bind(R.id.now_open) TextView openNow;
//    @Bind(R.id.register_button) Button registerBtn;
//    @Bind(R.id.keep_track) TextView keepTrack;
//    @Bind(R.id.join_ggp) TextView joinGgp;
//
    public static HomeFeedUnauthenticatedFragment newInstance() {
        return new HomeFeedUnauthenticatedFragment();
    }

    @Override
    public void onFragmentVisible() {
        analyticsManager.trackScreen(AnalyticsManager.Screens.Featured);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.home_feed_fragment_unauthenticated, container);
        openNow.setText(getString(R.string.store_opening));
//        registerBtn.setText(getString(R.string.create_account));
//        keepTrack.setText(getString(R.string.keep_track));
//        joinGgp.setText(getString(R.string.join_ggp));

        return view;
    }

    @Override
    protected void refreshData() {
        storeOpeningView.setDataLoaded(false);
        singleSaleView.setDataLoaded(false);
        remainingSalesView.setDataLoaded(false);
        homeController.getHomeFeedUnauthenticatedObservable().subscribe((homeFeedAuthenticatedViewModel) -> {
            setupBaseContent(homeFeedAuthenticatedViewModel);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @OnClick(R.id.register_button)
    public void createAccount() {
        startActivity(AccountRegistrationActivity.buildIntent(getActivity()));
    }
}