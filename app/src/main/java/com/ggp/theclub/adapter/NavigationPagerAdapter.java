package com.ggp.theclub.adapter;

import android.app.FragmentManager;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.fragment.BaseFragment;
import com.ggp.theclub.fragment.DirectoryFragment;
import com.ggp.theclub.fragment.HomeTabFragment;
import com.ggp.theclub.fragment.MoreFragment;
import com.ggp.theclub.fragment.ParkingTabFragment;
import com.ggp.theclub.fragment.ShoppingFragment;

public class NavigationPagerAdapter extends ViewPagerAdapter {
    
    public NavigationPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        addFragment(HomeTabFragment.newInstance(), "");
        addFragment(DirectoryFragment.newInstance(), MallApplication.getApp().getString(R.string.directory_title));
        addFragment(ShoppingFragment.newInstance(), MallApplication.getApp().getString(R.string.shopping_title));
        addFragment(ParkingTabFragment.newInstance(), MallApplication.getApp().getString(R.string.parking_title));
        addFragment(MoreFragment.newInstance(), MallApplication.getApp().getString(R.string.more_title));
    }

    public int getActionButtonText(int position) {
        switch (position) {
            case 1: return R.string.filter_title;
            default: return 0;
        }
    }

    public void onActionButtonClick(int position) {
        ((BaseFragment) getItem(position)).onActionButtonClick();
    }
}