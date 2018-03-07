package com.ggp.theclub.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomRecyclerView extends RelativeLayout {
    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.fast_scroller) FastScroller fastScroller;
    @Bind(R.id.loading_view) ProgressBar loadingView;

    private int paddingTop;
    private int paddingLeft;
    private int paddingRight;
    private int paddingBottom;
    private boolean clipToPadding;
    private boolean nestedScrollingEnabled;
    private boolean hasFixedSize;
    private boolean reversedLayout;
    private int layoutOrientation;
    private boolean fastScrollerEnabled;
    private boolean loadingViewEnabled;
    private int loadingViewColor;
    private int loadingViewPosition;

    public CustomRecyclerView(Context context) {
        this(context, null);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
        setViews(context);
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.CustomRecyclerView);
        try {
            paddingTop = attributes.getDimensionPixelSize(R.styleable.CustomRecyclerView_paddingTop, 0);
            paddingLeft = attributes.getDimensionPixelSize(R.styleable.CustomRecyclerView_paddingLeft, 0);
            paddingRight = attributes.getDimensionPixelSize(R.styleable.CustomRecyclerView_paddingRight, 0);
            paddingBottom = attributes.getDimensionPixelSize(R.styleable.CustomRecyclerView_paddingBottom, 0);
            clipToPadding = attributes.getBoolean(R.styleable.CustomRecyclerView_clipToPadding, true);
            nestedScrollingEnabled = attributes.getBoolean(R.styleable.CustomRecyclerView_nestedScrollingEnabled, true);
            hasFixedSize = attributes.getBoolean(R.styleable.CustomRecyclerView_hasFixedSize, false);
            layoutOrientation = attributes.getInt(R.styleable.CustomRecyclerView_layoutOrientation, LinearLayoutManager.VERTICAL);
            reversedLayout = attributes.getBoolean(R.styleable.CustomRecyclerView_reversedLayout, false);
            fastScrollerEnabled = attributes.getBoolean(R.styleable.CustomRecyclerView_fastScrollerEnabled, false);
            loadingViewEnabled = attributes.getBoolean(R.styleable.CustomRecyclerView_loadingViewEnabled, false);
            loadingViewColor = attributes.getColor(R.styleable.CustomRecyclerView_loadingViewColor, getResources().getColor(R.color.gray));
            loadingViewPosition = attributes.getInt(R.styleable.CustomRecyclerView_loadingViewPosition, CENTER_IN_PARENT);
        } finally {
            attributes.recycle();
        }
    }

    private void setViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_recycler_view, this);
        ButterKnife.bind(this, view);

        recyclerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        recyclerView.setClipToPadding(clipToPadding);
        recyclerView.setNestedScrollingEnabled(nestedScrollingEnabled);
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), layoutOrientation, reversedLayout) {
            @Override
            public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                if (fastScrollerEnabled) {
                    int firstVisibleItemPosition = findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition == -1) {
                        fastScroller.setVisibility(View.GONE);
                    } else if (firstVisibleItemPosition == 0) {
                        int lastVisibleItemPosition = findLastVisibleItemPosition();
                        int visibleItems = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                        fastScroller.setVisibility(recyclerView.getAdapter().getItemCount() > visibleItems ? View.VISIBLE : View.GONE);
                    }
                }
            }
        });

        if (fastScrollerEnabled) {
            fastScroller.setRecyclerView(recyclerView);
            fastScroller.setVisibility(VISIBLE);
        }
        if (loadingViewEnabled) {
            loadingView.getIndeterminateDrawable().setColorFilter(loadingViewColor, PorterDuff.Mode.SRC_IN);
            ((LayoutParams) loadingView.getLayoutParams()).addRule(loadingViewPosition);
            loadingView.setVisibility(VISIBLE);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        if (!loadingViewEnabled) {
            recyclerView.setVisibility(VISIBLE);
        }
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    public void setDataLoaded(boolean dataLoaded) {
        if (loadingViewEnabled) {
            loadingView.setVisibility(dataLoaded ? GONE : VISIBLE);
        }
        recyclerView.setVisibility(dataLoaded ? VISIBLE : GONE);
    }
}