package com.ggp.theclub.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    protected final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitles = new ArrayList<>();
    private FragmentManager fragmentManager;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) { return fragments.get(position); }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        if (!fragments.contains(object)) {
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
    }

    public void clear() {
        for (int i = getCount() - 1; i >= 0; i--) {
            removeFragment(i);
        }
    }

    public void replaceFragment(int position, Fragment fragment, String title) {
        removeFragment(position);
        fragments.add(position, fragment);
        fragmentTitles.add(position, title);
        notifyDataSetChanged();
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentTitles.add(title);
        notifyDataSetChanged();
    }

    private void removeFragment(int position) {
        fragmentManager.beginTransaction().remove(fragments.get(position)).commitAllowingStateLoss();
        fragments.remove(position);
        fragmentTitles.remove(position);
    }
}
