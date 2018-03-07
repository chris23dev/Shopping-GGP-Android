package com.ggp.theclub.fragment;

import android.os.Bundle;

import com.ggp.theclub.R;
import com.ggp.theclub.event.ParkingConfigChangedEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MallConfig;

import butterknife.BindString;
import de.greenrobot.event.EventBus;

public class ParkingTabFragment extends ViewPagerFragment {
    @BindString(R.string.parking_availability_title) String availabilityTitle;
    @BindString(R.string.parking_info_title) String infoTitle;
    @BindString(R.string.parking_reminder_title) String reminderTitle;
    @BindString(R.string.parking_assist_title) String assistTitle;

    public static ParkingTabFragment newInstance() {
        return new ParkingTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void configureView() {
        super.configureView();
        configureViewPager();
    }

    private void configureViewPager() {
        viewPagerAdapter.clear();

        MallConfig mallConfig = mallManager.getMall().getMallConfig();

        if (mallConfig.isParkAssistEnabled()) {
            viewPagerAdapter.addFragment(ParkingSitesFragment.newInstance(), availabilityTitle);
            viewPagerAdapter.addFragment(ParkAssistSearchFragment.newInstance(), assistTitle);
            viewPagerAdapter.addFragment(ParkingOverviewFragment.newInstance(), infoTitle);
        } else if (mallConfig.isParkingAvailabilityEnabled()) {
            viewPagerAdapter.addFragment(ParkingAvailabilityFragment.newInstance(), availabilityTitle);
            viewPagerAdapter.addFragment(ParkingReminderFragment.newInstance(), reminderTitle);
            viewPagerAdapter.addFragment(ParkingOverviewFragment.newInstance(), infoTitle);
        } else {
            viewPagerAdapter.addFragment(ParkingReminderFragment.newInstance(), reminderTitle);
            viewPagerAdapter.addFragment(ParkingOverviewFragment.newInstance(), infoTitle);
        }

        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setSwipeEnabled(false);
    }

    public void onEvent(ParkingConfigChangedEvent event) {
        configureViewPager();
    }
}
