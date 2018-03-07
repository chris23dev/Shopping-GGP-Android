package com.ggp.theclub.fragment;

import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.repository.MallRepository;

import butterknife.Bind;
import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment {
    @Bind(R.id.layout_view) public CoordinatorLayout layoutView;
    protected MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    protected MallManager mallManager = MallApplication.getApp().getMallManager();
    protected AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();
    protected View view;
    private boolean needsIsVisible;

    public String getFragmentTag() {
        return getClass().getSimpleName();
    }

    protected final View createView(LayoutInflater inflater, int layout, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(layout, container, false);
            ButterKnife.bind(this, view);
            configureView();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (needsIsVisible) {
            onFragmentVisible();
            needsIsVisible = false;
        }
    }

    public void needsIsVisible() {
        needsIsVisible = true;
    }

    protected void configureView() {}
    public void onFragmentVisible() {}
    public void onFragmentInvisible() {}
    public void onActionButtonClick() {}
}