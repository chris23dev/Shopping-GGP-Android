package com.ggp.theclub.view;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ggp.theclub.adapter.ViewPagerAdapter;
import com.ggp.theclub.fragment.BaseFragment;

import lombok.Setter;

public class CustomViewPager extends ViewPager {
    @Setter private boolean swipeEnabled = true;
    private BaseFragment selectedFragment;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onInterceptTouchEvent(event);
    }

    public void pageChanged() {
        BaseFragment oldFragment = selectedFragment;
        selectedFragment = getCurrentFragment();

        if (oldFragment != null) {
            oldFragment.onFragmentInvisible();
        }

        if (selectedFragment != null) {
            if (selectedFragment.isResumed()) {
                selectedFragment.onFragmentVisible();
            } else {
                selectedFragment.needsIsVisible();
            }
        }
    }

    public BaseFragment getCurrentFragment() {
        Fragment fragment = null;
        PagerAdapter pagerAdapter = getAdapter();
        if (pagerAdapter != null && pagerAdapter instanceof ViewPagerAdapter) {
            fragment = ((ViewPagerAdapter)pagerAdapter).getItem(getCurrentItem());
        }
        return fragment != null && (fragment instanceof BaseFragment) ? (BaseFragment)fragment : null;
    }
}