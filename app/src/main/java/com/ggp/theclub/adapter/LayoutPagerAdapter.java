package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class LayoutPagerAdapter extends PagerAdapter {

    private Context context;

    private List<View> pagerViews = new ArrayList<>();

    public LayoutPagerAdapter(Context context) {
        this.context = context;
    }

    public void addView(View layoutView) {
        pagerViews.add(layoutView);
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup layout = (ViewGroup) pagerViews.get(position);
        collection.addView(pagerViews.get(position));
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return pagerViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}