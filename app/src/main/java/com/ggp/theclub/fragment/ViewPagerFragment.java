package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ViewPagerAdapter;
import com.ggp.theclub.view.CustomViewPager;

import butterknife.Bind;
import butterknife.OnPageChange;

/**
 * This class is meant to be used for fragments that have tabs inside them.
 * It calls the onVisible / onInvisible methods for the tabbed fragments
 */
public class ViewPagerFragment extends BaseFragment {
    @Nullable @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Nullable @Bind(R.id.view_pager) CustomViewPager viewPager;
    protected ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(inflater, R.layout.view_pager_fragment, container);
    }

    @Override
    protected void configureView() {
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();

        if (viewPager != null) {
            BaseFragment currentFragment = viewPager.getCurrentFragment();
            if (currentFragment != null) {
                if (currentFragment.isResumed()) {
                    currentFragment.onFragmentVisible();
                } else {
                    currentFragment.needsIsVisible();
                }
            }
        }
    }

    @Override
    public void onFragmentInvisible() {
        super.onFragmentInvisible();

        if (viewPager != null) {
            BaseFragment currentFragment = viewPager.getCurrentFragment();
            if (currentFragment != null) {
                currentFragment.onFragmentInvisible();
            }
        }
    }

    @OnPageChange(R.id.view_pager)
    void onPageChange(int selectedPosition) {
        if (viewPager != null) {
            viewPager.pageChanged();
        }
    }
}
