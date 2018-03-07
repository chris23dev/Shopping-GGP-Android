package com.ggp.theclub.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;

public class ExpandableCategoryView extends FrameLayout {

    @Bind(R.id.collapsed_background) ImageView collapsedBackground;
    @Bind(R.id.expanded_background) ImageView expandedBackground;
    @Bind(R.id.collapsed_overlay) View collapsedOverlay;
    @Bind(R.id.expanded_overlay) View expandedOverlay;
    @Bind(R.id.category_title) TextView categoryTitleView;
    @Bind(R.id.child_category_list) RecyclerView childCategoryList;

    @Getter String title;
    @Getter boolean isExpanded = false;
    private Drawable collapsedImage;
    private Drawable expandedImage;

    public ExpandableCategoryView(Context context) {
        super(context);
        configureView(context);
    }

    public ExpandableCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        configureView(context);
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableCategoryView);
        try {
            collapsedImage = attributes.getDrawable(R.styleable.ExpandableCategoryView_collapsedImage);
            expandedImage = attributes.getDrawable(R.styleable.ExpandableCategoryView_expandedImage);
        } finally {
            attributes.recycle();
        }
    }

    private void configureView(Context context) {
        inflate(context, R.layout.expandable_category_view, this);
        ButterKnife.bind(this);
        collapsedBackground.setImageDrawable(collapsedImage);
        expandedBackground.setImageDrawable(expandedImage);
    }

    private void toggleVisibility(View view) {
        view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public void toggleExpansion() {
        toggleVisibility(categoryTitleView);
        toggleVisibility(childCategoryList);
        toggleVisibility(expandedBackground);
        toggleVisibility(expandedOverlay);
        toggleVisibility(collapsedOverlay);
        isExpanded = !isExpanded;
    }

    public void collapse() {
        if(isExpanded()) {
            toggleExpansion();
        }
    }

    public void setTitle(String title) {
        this.title = title;
        categoryTitleView.setText(title);
    }

    public RecyclerView getExpandedListView() {
        return childCategoryList;
    }

    public ImageView getCollapsedBackground() {
        return collapsedBackground;
    }

    public ImageView getExpandedBackground() {
        return expandedBackground;
    }
}