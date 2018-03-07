package com.ggp.theclub.fragment;

import android.os.Bundle;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.manager.AccountManager;

import butterknife.BindString;
import de.greenrobot.event.EventBus;

public class HomeTabFragment extends ViewPagerFragment {
    @BindString(R.string.just_for_you_title) String authenticatedHomeTitle;
    @BindString(R.string.unauthenticated_home_title) String unauthenticatedHomeTitle;
    @BindString(R.string.events_title) String eventsTitle;
    @BindString(R.string.movies_title) String moviesTitle;
    @BindString(R.string.dining_title) String diningTitle;
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();

    public static HomeTabFragment newInstance() {
        return new HomeTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void configureView() {
        super.configureView();
        if (accountManager.isLoggedIn()) {
             viewPagerAdapter.addFragment(HomeFeedAuthenticatedFragment.newInstance(), authenticatedHomeTitle);
        } else {
             viewPagerAdapter.addFragment(HomeFeedUnauthenticatedFragment.newInstance(), unauthenticatedHomeTitle);
        }

        viewPagerAdapter.addFragment(EventsFragment.newInstance(), eventsTitle);

        if(mallManager.getMall().hasTheater()) {
            viewPagerAdapter.addFragment(MoviesFragment.newInstance(), moviesTitle);
        }

        viewPagerAdapter.addFragment(DiningFragment.newInstance(), diningTitle);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(AccountLoginEvent event) {
        updateHomeFeed();
    }

    private void updateHomeFeed() {
        if (accountManager.isLoggedIn()) {
            viewPagerAdapter.replaceFragment(0, HomeFeedAuthenticatedFragment.newInstance(), authenticatedHomeTitle);
        } else {
            viewPagerAdapter.replaceFragment(0, HomeFeedUnauthenticatedFragment.newInstance(), unauthenticatedHomeTitle);
        }
    }
}
