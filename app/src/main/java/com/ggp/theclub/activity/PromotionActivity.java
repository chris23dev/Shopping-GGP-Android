package com.ggp.theclub.activity;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.Promotion;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.ViewUtils;
import com.jibestream.jibestreamandroidlibrary.main.EngineView;
import com.squareup.picasso.Callback;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;

public abstract class PromotionActivity extends BaseActivity {
    @BindColor(R.color.blue) int blue;
    @Bind(R.id.main_image_layout) View mainImageLayout;
    @Bind(R.id.main_image) ImageView mainImageView;
    @Bind(R.id.no_image_icon) View noImageIcon;
    @Bind(R.id.windowed_layout) NestedScrollView windowedLayout;
    @Bind(R.id.tap_window) View tapWindow;
    @Bind(R.id.scrim_view) View scrimView;
    @Bind(R.id.logo) FrameLayout logoView;
    @Bind(R.id.image_logo) ImageView logoImageView;
    @Bind(R.id.text_logo) TextView logoTextView;
    @Bind(R.id.promotion_title) TextView title;
    @Bind(R.id.location_layout) ViewGroup locationLayout;
    @Bind(R.id.promotion_location) TextView locationView;
    @Bind(R.id.promotion_date) TextView date;
    @Bind(R.id.promotion_time_layout) ViewGroup timeLayout;
    @Bind(R.id.promotion_time) TextView time;
    @Bind(R.id.description_header) TextView descriptionHeader;
    @Bind(R.id.promotion_description) TextView description;
    @Bind(R.id.promotion_teaser) TextView teaser;
    @Bind(R.id.external_link_list) RecyclerView externalLinksList;
    @Bind(R.id.map_location) TextView mapLocation;
    @Bind(R.id.static_map_layout) FrameLayout staticMapLayout;
    @Bind(R.id.engine_view) EngineView engineView;
    @Bind(R.id.map_view) ImageView mapView;

    protected MapManager mapManager = MapManager.getInstance();
    protected Promotion promotion;
    protected Tenant tenant;

    private final float ASPECT_RATIO = 1.4f;
    private final int COLLAPSE_ERROR_DIFFERENTIAL = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_activity);
        title.setText(getString(R.string.tenant_about_header));
    }

    @Override
    protected void configureView() {
        setupImageScrollListener();
        if (!StringUtils.isEmpty(promotion.getImageUrl())) {
            ImageUtils.loadImage(promotion.getImageUrl(), mainImageView, new Callback() {
                @Override
                public void onSuccess() {
                    if (!isDestroyed()) {
                        setMainImageBounds();
                    }
                }

                @Override
                public void onError() {
                }
            });
        } else {
            mainImageView.setVisibility(View.GONE);
            noImageIcon.setVisibility(View.VISIBLE);
            setMainImageBounds();
        }

        enableBackButton();
        setIconActionButton(R.string.share_icon);

        title.setText(promotion.getTitle());
        ViewUtils.setHtmlText(description, promotion.getDescription());

        setupLocation();
        setupDateTime();
        setupMapLocation();
    }

    protected void setMainImageBounds() {
        mainImageLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mainImageLayout.getHeight() > 0) {
                    mainImageLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    windowedLayout.setPadding(windowedLayout.getPaddingLeft(), mainImageLayout.getHeight(),
                            windowedLayout.getPaddingRight(), windowedLayout.getPaddingBottom());
                    collapseImage();
                }
            }
        });
    }

    protected void setPromotion(Promotion promotion) {
        this.promotion = promotion;
        tenant = promotion.getTenant();
    }

    protected abstract void setupLocation();

    private void setupDateTime() {
        DateUtils.DateTimeStrings dateTimeStrings = DateUtils.getPromotionDateTimeStrings(promotion.getStartDateTime(), promotion.getEndDateTime());
        date.setText(dateTimeStrings.getDateString());
        if(!StringUtils.isEmpty(dateTimeStrings.getTimeString())) {
            time.setText(dateTimeStrings.getTimeString());
        } else {
            timeLayout.setVisibility(View.GONE);
        }
    }

    private void setupMapLocation() {
        if(tenant != null) {
            String mapLocationString =  mapManager.getTenantLocationByLeaseId(tenant.getLeaseId());
            if (!StringUtils.isEmpty(mapLocationString)) {
                mapLocation.setText(mapLocationString);
                mapLocation.setVisibility(View.VISIBLE);
            }
            if (mapManager.isDestinationMapped(tenant.getLeaseId())) {
                staticMapLayout.setVisibility(View.VISIBLE);
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mapView.getWidth() > 0) {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mapManager.renderTenantImage(tenant.getLeaseId(), mapView, engineView, PromotionActivity.this);
                        }
                    }
                });
            }
        }
    }

    private void setupImageScrollListener() {
        windowedLayout.setOnScrollChangeListener((NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            //Force the max scroll position to be the bottom of the scrim position
            if(scrollY < getExpandedPosition()){
                v.scrollTo(0, getExpandedPosition());
            }
            scrimView.setVisibility(isExpanded() ? View.INVISIBLE : View.VISIBLE);
            updateTappableWindowHeight(scrollY);
        });
    }

    private void updateTappableWindowHeight(int currentScrolledPosition) {
        ViewGroup.LayoutParams params = tapWindow.getLayoutParams();
        params.height = Math.max(mainImageLayout.getHeight() - currentScrolledPosition, 0);
        tapWindow.setLayoutParams(params);
    }

    private boolean isImageCollapsible() {
        return mainImageLayout.getWidth() / mainImageLayout.getHeight() < ASPECT_RATIO;
    }

    private int getExpandedPosition() {
        return scrimView.getHeight();
    }

    private int getCollapsedPosition() {
        int collapsedHeight;
        if(isImageCollapsible()) {
            collapsedHeight = Math.min(Math.round(mainImageLayout.getWidth() / ASPECT_RATIO), mainImageLayout.getHeight());
        } else {
            collapsedHeight = mainImageLayout.getHeight();
        }
        return mainImageLayout.getHeight() - collapsedHeight;
    }

    private boolean isExpanded() {
        return Math.abs(windowedLayout.getScrollY() - getExpandedPosition()) < COLLAPSE_ERROR_DIFFERENTIAL;
    }

    private void expandImage() {
        windowedLayout.smoothScrollTo(0, getExpandedPosition());
        updateTappableWindowHeight(getExpandedPosition());
    }

    private void collapseImage() {
        windowedLayout.smoothScrollTo(0, getCollapsedPosition());
        updateTappableWindowHeight(getCollapsedPosition());
    }

    @OnClick(R.id.promotion_location)
    public void onLocationViewClick() {
        if(tenant != null) {
            startActivity(TenantActivity.buildIntent(this, tenant));
            HashMap<String, Object> contextData = new HashMap<>();
            contextData.put(AnalyticsManager.ContextDataKeys.EventSaleName, promotion.getTitle());
            analyticsManager.trackAction(AnalyticsManager.Actions.EventViewLocation, contextData, tenant.getName());
        }
    }

    @OnClick(R.id.map_view)
    public void onMapViewClick() {
        startActivity(PromotionMapActivity.buildIntent(this, tenant));
    }

    @OnClick(R.id.tap_window)
    public void onMainImageClick() {
        if(isExpanded()) {
            collapseImage();
        } else {
            expandImage();
        }
    }
}