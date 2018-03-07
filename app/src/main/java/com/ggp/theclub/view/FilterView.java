package com.ggp.theclub.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.event.FilterUpdateEvent;
import com.ggp.theclub.event.TenantsFilterUpdateEvent;
import com.ggp.theclub.util.CategoryUtils;
import com.ggp.theclub.util.ProductUtils;
import com.ggp.theclub.util.StringUtils;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class FilterView extends LinearLayout {
    @BindColor(R.color.blue) int filterLayoutColor;
    @BindString(R.string.product_filter_header_format) String headerFormat;
    @Bind(R.id.header_view) TextView headerView;
    @Bind(R.id.description_view) TextView descriptionView;

    public FilterView(Context context) {
        super(context);
        configureView(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureView(context);
    }

    private void configureView(Context context) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.filter_view, this);
        ButterKnife.bind(this);
        hideView();
    }

    /**
     * This is only here for use by the DirectoryMapFragment to hide the header view
     * on a product type filter, since the level selector already denotes the count
     */
    public void updateView(FilterUpdateEvent event, boolean showHeaderView) {
        updateView(event);
        if (!showHeaderView) {
            headerView.setVisibility(GONE);
        }
    }

    public void updateView(FilterUpdateEvent event) {
        if (event == null) {
            hideView();
        } else {
            if (!StringUtils.isEmpty(event.getFilterLabel())) {
                descriptionView.setText(event.getFilterLabel());
            }
            int filterCount = event.getFilterCount();
            if (event.getFilterType().equals(FilterUpdateEvent.FilterType.CATEGORY) && shouldShowView(event.getCategoryCode())) {
                showView();
            } else if (event.getFilterType().equals(FilterUpdateEvent.FilterType.PRODUCT_TYPE) && shouldShowView(event.getProductTypeCode())) {
                showView();
                if (filterCount > 1) {
                    setHeaderView(filterCount);
                }
            } else if (event.getFilterType().equals(FilterUpdateEvent.FilterType.BRAND)) {
                showView();
                if (filterCount > 1) {
                    setHeaderView(filterCount);
                }
            } else if (event.getFilterType().equals(FilterUpdateEvent.FilterType.AMENITY)) {
                showView();
            } else {
                hideView();
            }
        }
    }

    private void showView() {
        headerView.setVisibility(GONE);
        setVisibility(VISIBLE);
    }

    private void hideView() {
        headerView.setVisibility(GONE);
        setVisibility(GONE);
    }

    private boolean shouldShowView(String eventCode) {
        return !StringUtils.isEmpty(eventCode) &&
               !eventCode.equals(CategoryUtils.CATEGORY_ALL_SALES) &&
               !eventCode.equals(CategoryUtils.CATEGORY_ALL_STORES) &&
               !eventCode.equals(ProductUtils.PRODUCT_TYPE_ALL_STORES);
    }

    private void setHeaderView(int filterCount) {
        headerView.setText(String.format(headerFormat, filterCount));
        headerView.setVisibility(VISIBLE);
    }

    @OnClick(R.id.clear_button)
    public void onClearButtonClick() {
        hideView();
        EventBus.getDefault().post(new TenantsFilterUpdateEvent());
    }
}