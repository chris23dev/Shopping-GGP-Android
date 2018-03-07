package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ViewPagerAdapter;
import com.ggp.theclub.fragment.MallSearchByLocationFragment;
import com.ggp.theclub.fragment.MallSearchByNameFragment;
import com.ggp.theclub.fragment.MallSearchFragment;
import com.ggp.theclub.manager.AnalyticsManager;

import butterknife.Bind;
import butterknife.BindString;

public class ChangeMallActivity extends  BaseActivity {
    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;

    @BindString(R.string.search_by_name) String searchByNameTitle;
    @BindString(R.string.search_by_location) String searchByLocationTitle;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, ChangeMallActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_activity);
        analyticsManager.trackScreen(AnalyticsManager.Screens.MallSelection);
        analyticsManager.trackAction(AnalyticsManager.Actions.ChangeMall);
    }

    @Override
    protected void configureView() {
        setTitle(R.string.select_mall_title);
        enableBackButton();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPagerAdapter.addFragment(MallSearchByLocationFragment.newInstance(MallSearchFragment.SearchScreenStyle.CHANGE_MALL), searchByLocationTitle);
        viewPagerAdapter.addFragment(MallSearchByNameFragment.newInstance(MallSearchFragment.SearchScreenStyle.CHANGE_MALL), searchByNameTitle);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}