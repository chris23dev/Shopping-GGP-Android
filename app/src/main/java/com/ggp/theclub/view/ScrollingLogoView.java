package com.ggp.theclub.view;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ggp.theclub.R;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.StringUtils;
import com.squareup.picasso.Callback;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScrollingLogoView extends AppBarLayout implements OnOffsetChangedListener {
    @BindColor(R.color.white) int backgroundColor;
    @Bind(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.logo_view) ImageView logoView;
    @Bind(R.id.scrim_view) View scrimView;
    @Bind(R.id.toolbar) Toolbar toolbarView;
    private final float ASPECT_RATIO = 1.4f;
    private boolean expanded;

    public ScrollingLogoView(Context context) {
        super(context);
        configureView(context);
    }

    public ScrollingLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureView(context);
    }

    private void configureView(Context context) {
        inflate(context, R.layout.scrolling_logo_view, this);
        ButterKnife.bind(this);
        addOnOffsetChangedListener(this);
        setBackgroundColor(backgroundColor);
    }

    public void setLogo(String logoUrl) {
        if(!StringUtils.isEmpty(logoUrl)) {
            ImageUtils.loadImage(logoUrl, logoView, new Callback() {
                @Override
                public void onSuccess() {
                    setLogoViewBounds();
                }

                @Override
                public void onError() {
                    setVisibility(GONE);
                }
            });
        } else {
            setVisibility(GONE);
        }
    }

    private void setLogoViewBounds() {
        post(()->{
            if (isExpandable()) {
                collapseImage();
            }
        });
    }

    private void collapseImage() {
        setAppBarOffset(getCollapsedImageToolbarHeight());
        collapsingToolbarLayout.setMinimumHeight(getCollapsedImageToolbarHeight());
        scrimView.setVisibility(View.VISIBLE);
        setExpanded(true, false);
    }

    private boolean isExpandable() {
        return logoView.getWidth() / logoView.getHeight() < ASPECT_RATIO;
    }

    private int getCollapsedImageToolbarHeight() {
        return Math.round(logoView.getWidth() / ASPECT_RATIO) + toolbarView.getHeight();
    }

    private void setAppBarOffset(int offsetPx){
        CoordinatorLayout parentCoordinatorLayout = (CoordinatorLayout) getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.onNestedPreScroll(parentCoordinatorLayout, this, null, 0, offsetPx, new int[]{0, 0});
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0 || Math.abs(verticalOffset) >= getHeight() - toolbarView.getHeight() - scrimView.getHeight()) {
            expanded = true;
            scrimView.setVisibility(GONE);
        } else {
            expanded = false;
            scrimView.setVisibility(VISIBLE);
        }
    }

    @OnClick(R.id.logo_view)
    public void onLogoViewClick() {
        if (isExpandable()) {
            setExpanded(!expanded);
        } else if (!expanded) {
            setExpanded(true);
        }
    }
}