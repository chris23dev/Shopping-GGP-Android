package com.ggp.theclub.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.adapter.TenantListAdapter;
import com.ggp.theclub.adapter.TenantProductTypesAdapter;
import com.ggp.theclub.adapter.TenantPromotionsAdapter;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.FeedbackManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.FilterProductType;
import com.ggp.theclub.model.Hours;
import com.ggp.theclub.model.HoursLineItem;
import com.ggp.theclub.model.MallConfig;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.model.Promotion;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.CategoryUtils;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.ProductUtils;
import com.ggp.theclub.util.PromotionUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.util.ViewUtils;
import com.jibestream.jibestreamandroidlibrary.main.EngineView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.OnClick;
import java8.util.Objects;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class TenantActivity extends BaseActivity {
    @BindColor(R.color.white) int lightTextColor;
    @BindColor(R.color.dark_gray_text) int darkTextColor;
    @BindString(R.string.open_table_url_template) String openTableUrlTemplate;
    @BindString(R.string.parent_tenant_location_prefix) String parentTenantLocationPrefix;
    @BindString(R.string.park_near_text) String parkNearPrefix;
    @BindString(R.string.get_directions) String directionsMessage;
    @BindString(R.string.tenant_child_stores_header_format) String childStoresHeaderFormat;
    @Bind(R.id.appbar_layout) AppBarLayout appBarLayout;
    @Bind(R.id.image_logo) ImageView logoImageView;
    @Bind(R.id.text_logo) TextView logoTextView;
    @Bind(R.id.tenant_detail_scroll_view) NestedScrollView tenantDetailScrollView;
    @Bind(R.id.store_opening_view) TextView storeOpeningView;
    @Bind(R.id.name_view) TextView nameView;
    @Bind(R.id.category_view) TextView categoryView;
    @Bind(R.id.call_button) LinearLayout callButton;
    @Bind(R.id.reserve_button) LinearLayout reserveButton;
    @Bind(R.id.wayfind_button) LinearLayout wayfindButton;
//    @Bind(R.id.favorite_button) LinearLayout favoriteButton;
    @Bind(R.id.favorite_on) TextView favoriteActiveIcon;
    @Bind(R.id.description_layout) FrameLayout descriptionLayout;
    @Bind(R.id.description_view) TextView descriptionView;
    @Bind(R.id.description_scrim) View descriptionScrim;
    @Bind(R.id.hours_layout) LinearLayout hoursLayout;
    @Bind(R.id.weekday_view) TextView weekdayView;
    @Bind(R.id.hours_view) TextView hoursView;
    @Bind(R.id.hours_arrow_view) TextView hoursArrowView;
    @Bind(R.id.website_layout) LinearLayout websiteLayout;
    @Bind(R.id.website_view) TextView websiteView;
    @Bind(R.id.location_layout) LinearLayout locationLayout;
    @Bind(R.id.location_view) TextView locationView;
    @Bind(R.id.directions_layout) LinearLayout directionsLayout;
    @Bind(R.id.directions_view) TextView directionsView;
    @Bind(R.id.static_map_layout) FrameLayout staticMapLayout;
    @Bind(R.id.engine_view) EngineView engineView;
    @Bind(R.id.map_view) ImageView mapView;
    @Bind(R.id.promotions_list) RecyclerView promotionsList;
    @Bind(R.id.product_type_layout) LinearLayout productTypeLayout;
    @Bind(R.id.product_types_arrow_view) View productTypesArrowView;
    @Bind(R.id.product_type_list) RecyclerView productTypeList;
    @Bind(R.id.child_stores_layout) LinearLayout childStoresLayout;
    @Bind(R.id.child_stores_header) TextView childStoresHeader;
    @Bind(R.id.child_stores_list) RecyclerView childStoresList;
    @Bind(R.id.call) TextView call;
    @Bind(R.id.reserve) TextView reserve;
    @Bind(R.id.favorite) TextView favorite;
    @Bind(R.id.wayfind) TextView wayfind;
    @Bind(R.id.product_types_header) TextView productTypesHeader;

    private final int ANIMATION_DURATION_RATIO = 20;
    private final int PRODUCT_SCROLL_DISTANCE = 150;
    private FeedbackManager feedbackManager = MallApplication.getApp().getFeedbackManager();
    private MapManager mapManager = MapManager.getInstance();
    private Tenant tenant;
    //used for parking and navigation, set after querying for parent tenant.
    private Tenant navigationTenant;
    private List<FilterProductType> productTypes;
    private Boolean favoriteActive;
    private Boolean favoritePendingLogin = false;
    private MallConfig mallConfig = mallManager.getMall().getMallConfig();

    public static Intent buildIntent(Context context, Tenant tenant) {
        return buildIntent(context, TenantActivity.class, tenant);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tenant = getIntentExtra(Tenant.class);
        if (tenant == null) {
            tenant = getIntentExtra(MovieTheater.class);
        }
        productTypes = ProductUtils.getTenantProductTypeList(tenant.getProductTypes());
        feedbackManager.incrementFeedbackEventCount(this);
        setContentView(R.layout.tenant_activity);
        call.setText(getString(R.string.call));
        reserve.setText(getString(R.string.reserve));
        favorite.setText(getString(R.string.favorite));
        wayfind.setText(getString(R.string.wayfind));
        storeOpeningView.setText(getString(R.string.tenant_store_opening));
        childStoresHeader.setText(getString(R.string.product_types_header));
        productTypesHeader.setText(getString(R.string.product_types_header));
    }

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.TenantDetail, tenant.getName());
        trackStoreInStore();
    }

    @Override
    protected void configureView() {
        favoriteActive = TenantUtils.isFavoriteTenant(tenant, accountManager.getCurrentUser().getFavorites());
        enableBackButton();
        setTitle(tenant.getName());
        ImageUtils.setLogo(logoImageView, logoTextView, tenant.getLogoUrl(), tenant.getName());

        mallRepository.queryForTenants((tenants) -> {
            Optional<Tenant> parentTenantOptional = TenantUtils.hasParentTenant(tenant) ? Optional.of(TenantUtils.getParentTenant(tenant, tenants)) : Optional.empty();
            navigationTenant = parentTenantOptional.isPresent() ? parentTenantOptional.get() : tenant;
            setLocationLayoutItems(parentTenantOptional);
            setMapLayoutItems(parentTenantOptional);
            setActionRibbonItems(parentTenantOptional);
            setParkingDirectionsLayoutItems(parentTenantOptional);

            List<Integer> childIds = tenant.getChildIds() != null ? tenant.getChildIds() : new ArrayList<>();
            List<Tenant> childTenants = StreamSupport.stream(childIds)
                    .map(id -> TenantUtils.getTenantById(id, tenants))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            setupChildTenants(childTenants);
        });

        setCollapsingToolbarItems();
        setDescriptionLayoutItems();
        setHoursLayoutItems();
        setToolbarColors(false);

        if (!StringUtils.isEmpty(tenant.getShortWebsiteUrl())) {
            websiteView.setText(tenant.getShortWebsiteUrl());
            websiteLayout.setVisibility(View.VISIBLE);
        }
        
        getPromotionsList();
        getProductTypeList();
    }

    private void trackStoreInStore() {
        if (TenantUtils.hasParentTenant(tenant)) {
            analyticsManager.trackAction(AnalyticsManager.Actions.StoreInStore, null, tenant.getName());
        }
    }

    private void trackCallClicked() {
        HashMap<String, Object> contextData = new HashMap<String, Object>(){{
            put(AnalyticsManager.ContextDataKeys.TenantPhoneNumber, tenant.getPhoneNumber());
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantCall, contextData, tenant.getName().toLowerCase());
    }

    private void setCollapsingToolbarItems() {
        if (!(tenant instanceof MovieTheater)) {
            storeOpeningView.setVisibility(TenantUtils.isNewTenant(tenant) ? View.VISIBLE : View.GONE);
        }
        nameView.setText(tenant.getName());
        categoryView.setText(CategoryUtils.getDisplayNameForCategories(tenant.getCategories()));
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) >= getToolbarScrollTriggerOffset()) {
                setToolbarColors(true);
                titleView.setAlpha(getToolbarAlpha(verticalOffset));
            } else {
                setToolbarColors(false);
            }
        });
    }

    private void setToolbarColors(boolean isCollapsed) {
        if(isCollapsed) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setTextColor(lightTextColor);
            backButton.setTextColor(lightTextColor);
        } else {
            titleView.setVisibility(View.INVISIBLE);
            titleView.setTextColor(darkTextColor);
            backButton.setTextColor(darkTextColor);
        }
    }

    private void setActionRibbonItems(Optional<Tenant> parentTenantOptional) {
        Tenant wayfindTenant = parentTenantOptional.isPresent() ? parentTenantOptional.get() : tenant;
        callButton.setVisibility(StringUtils.isEmpty(tenant.getPhoneNumber()) ? View.GONE : View.VISIBLE);
        reserveButton.setVisibility(StringUtils.isEmpty(tenant.getOpenTableId()) ? View.GONE : View.VISIBLE);
        wayfindButton.setVisibility(mapManager.isDestinationWayfindingEnabled(wayfindTenant) ? View.VISIBLE : View.GONE);
//        favoriteButton.setVisibility(tenant.getPlaceWiseRetailerId() == null ? View.GONE : View.VISIBLE);
        updateFavoriteButton();
    }

    private void setDescriptionLayoutItems() {
        if (!StringUtils.isEmpty(tenant.getDescription())) {
            descriptionView.setText(Html.fromHtml(tenant.getDescription()));
            descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
            descriptionLayout.setVisibility(View.VISIBLE);
            descriptionView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    descriptionView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    descriptionView.setMaxLines(isDescriptionExpandable() ? descriptionView.getMinLines() : descriptionView.getLineCount());
                    descriptionScrim.setVisibility(isDescriptionExpandable() ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    private void setHoursLayoutItems() {
        HoursLineItem formattedStoreHours = HoursUtils.getWeeklyHoursList(tenant.getOperatingHours(), tenant.getOperatingHoursExceptions(), LocalDate.now());
        if (!StringUtils.isEmpty(formattedStoreHours.getLeftColumn()) &&
                !StringUtils.isEmpty(formattedStoreHours.getRightColumn()) &&
                !tenant.isTemporarilyClosed()) {
            weekdayView.setText(formattedStoreHours.getLeftColumn());
            hoursView.setText(formattedStoreHours.getRightColumn());
            setupExpandableHours();
        }
    }

    private void setupExpandableHours() {
        Set<Hours> hoursSet = HoursUtils.getHoursByDate(tenant.getOperatingHours(), tenant.getOperatingHoursExceptions(), LocalDate.now());
        //if hoursSet is empty we don't have hours for today, so just leave "Business Hours" visible
        int numHoursToday = !hoursSet.isEmpty() ? hoursSet.size() : 1;
        hoursLayout.setVisibility(View.VISIBLE);
        hoursView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                hoursView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                weekdayView.setMinLines(numHoursToday);
                hoursView.setMinLines(numHoursToday);
                weekdayView.setMaxLines(isHoursExpandable() ? hoursView.getMinLines() : hoursView.getLineCount());
                hoursView.setMaxLines(isHoursExpandable() ? hoursView.getMinLines() : hoursView.getLineCount());
                hoursArrowView.setVisibility(isHoursExpandable() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setLocationLayoutItems(Optional<Tenant> parentTenantOptional) {
        if (parentTenantOptional.isPresent()) {
            ViewUtils.setClickableSpan(locationView, parentTenantLocationPrefix, parentTenantOptional.get().getName(), null,
                    () -> startActivity(TenantActivity.buildIntent(this, parentTenantOptional.get())));
            locationLayout.setVisibility(View.VISIBLE);
        } else {
            String location = mapManager.getTenantLocationByLeaseId(tenant.getLeaseId());
            if (!StringUtils.isEmpty(location)) {
                locationView.setText(location);
                locationLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setParkingDirectionsLayoutItems(Optional<Tenant> parentTenantOptional) {
        if (mallConfig.isParkingAvailable()) {
            String parkNearMessage;
            Tenant targetTenant;
            if (parentTenantOptional.isPresent()) {
                parkNearMessage = parkNearPrefix + " " + parentTenantOptional.get().getName();
                targetTenant = parentTenantOptional.get();
            } else {
                parkNearMessage = mapManager.getParkingLocationByLeaseId(tenant.getLeaseId());
                targetTenant = tenant;
            }
            //parkNearMessage will be empty if this is an anchor or if this tenant is unmapped in jibestream
            String beforeClickableText = StringUtils.isEmpty(parkNearMessage) ? null : parkNearMessage + ", ";
            String clickableText = directionsMessage;

            ViewUtils.setClickableSpan(directionsView, beforeClickableText, clickableText, null, () -> {
                trackDirectionsAnalytics();
                IntentUtils.showDirectionsForTenant(targetTenant, this);
            });
        } else {
            directionsLayout.setVisibility(View.GONE);
        }
    }

    private void setMapLayoutItems(Optional<Tenant> parentTenantOptional) {
        Tenant targetTenant = parentTenantOptional.isPresent() ? parentTenantOptional.get() : tenant;
        if(mapManager.isDestinationMapped(targetTenant.getLeaseId())) {
            staticMapLayout.setVisibility(View.VISIBLE);
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mapView.getWidth() > 0) {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mapManager.renderTenantImage(targetTenant.getLeaseId(), mapView, engineView, TenantActivity.this);
                    }
                }
            });
        }
    }

    private void setupChildTenants(List<Tenant> childTenants) {
        TenantListAdapter tenantListAdapter = new TenantListAdapter(this);
        tenantListAdapter.resetTenants(childTenants);
        childStoresList.setLayoutManager(new LinearLayoutManager(this));
        childStoresList.setAdapter(tenantListAdapter);
        childStoresHeader.setText(String.format(childStoresHeaderFormat, tenant.getName()));
        childStoresLayout.setVisibility(childTenants.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void getPromotionsList() {
        List<Promotion> promotions = new ArrayList<>();
        mallRepository.queryForMallEvents(mallEvents -> {
            promotions.addAll(PromotionUtils.getMallEventsByStoreId(tenant.getId(), mallEvents));
            mallRepository.queryForSales(sales -> {
                List<Sale> salesByStoreId = PromotionUtils.getSalesByStoreId(tenant.getId(), sales);
                promotions.addAll(PromotionUtils.getSalesByEndDate(salesByStoreId));
                if (!promotions.isEmpty()) {
                    promotionsList.setLayoutManager(new LinearLayoutManager(this));
                    promotionsList.setAdapter(new TenantPromotionsAdapter(this, tenant, promotions));
                }
            });
        });
    }

    private void getProductTypeList() {
        if (shouldDisplayProductTypeList()) {
            productTypeLayout.setVisibility(View.VISIBLE);
            productTypeList.setLayoutManager(new LinearLayoutManager(this));
            productTypeList.setAdapter(new TenantProductTypesAdapter(productTypes));
        }
    }

    private boolean shouldDisplayProductTypeList() {
        if (!mallConfig.isProductEnabled() || productTypes.isEmpty()) {
            return false;
        }
        return !(productTypes.size() == 1 && productTypes.get(0).getChildren().size() <= 1);
    }

    private boolean isDescriptionExpandable() {
        return descriptionLayout.getVisibility() == View.VISIBLE && descriptionView.getLineCount() > descriptionView.getMinLines();
    }

    private boolean isHoursExpandable() {
        return hoursLayout.getVisibility() == View.VISIBLE && hoursView.getLineCount() > hoursView.getMinLines();
    }

    public void setFavoriteActive(boolean active) {
        favoriteActive = active;
        trackFavorite();
        updateFavoriteButton();
        if (favoriteActive) {
            accountManager.addFavoriteTenant(tenant);
            feedbackManager.incrementFeedbackEventCount(this);
        } else {
            accountManager.removeFavoriteTenant(tenant);
        }
    }

    private void updateFavoriteButton() {
        if(favoriteActive) {
            AnimationUtils.enterReveal(favoriteActiveIcon);
        } else {
            AnimationUtils.exitReveal(favoriteActiveIcon);
        }
    }

    private void trackFavorite() {
        String favoriteValue = favoriteActive ? AnalyticsManager.ContextDataValues.TenantFavorite : AnalyticsManager.ContextDataValues.TenantFavoriteUnfavorite;
        HashMap<String, Object> contextData = new HashMap<String, Object>(){{
            put(AnalyticsManager.ContextDataKeys.TenantFavorite, favoriteValue);
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantFavorite, contextData, tenant.getName());
    }

    private void trackDetailsExpanded() {
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantDetails, null, tenant.getName());
    }

    private void trackRegisterAnalytics() {
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantRegister, null, tenant.getName());
    }

    private void trackDirectionsAnalytics() {
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantDirections, null, tenant.getName());
    }

    private int getToolbarScrollTriggerOffset() {
        return appBarLayout.getTotalScrollRange() - Math.min(toolbar.getHeight(), logoImageView.getHeight());
    }

    private float getToolbarAlpha(int currentOffset) {
        int scrollRange = appBarLayout.getTotalScrollRange() - toolbar.getHeight();
        float alpha = (1f / toolbar.getHeight()) * (Math.abs(currentOffset) - scrollRange);
        return alpha;
    }

    public void onEvent(AccountLoginEvent event) {
        if(favoritePendingLogin) {
            favoritePendingLogin = false;
            if(event.isLoggedIn()) {
                setFavoriteActive(true);
            }
        }
    }

    @OnClick(R.id.call_button)
    public void onCallButtonClick() {
        trackCallClicked();
        IntentUtils.startPhoneNumberIntent(tenant.getPhoneNumber(), this);
    }

    @OnClick(R.id.reserve_button)
    public void onReservationButtonClick() {
        analyticsManager.trackAction(AnalyticsManager.Actions.ReserveOpenTable, null, tenant.getName());
        IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(openTableUrlTemplate, tenant.getOpenTableId()))), this);
    }

    @OnClick(R.id.wayfind_button)
    public void onWayfindingButtonClick() {
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantWayfinding, null, tenant.getName().toLowerCase());
        startActivity(WayfindActivity.buildIntent(this, tenant));
    }

    @OnClick(R.id.favorite_button)
    public void favoriteClicked() {
        if(accountManager.isLoggedIn()) {
            setFavoriteActive(!favoriteActive);
        } else {
            trackRegisterAnalytics();
            favoritePendingLogin = true;
            startActivity(AccountRegistrationActivity.buildIntent(this));
        }
    }

    @OnClick(R.id.product_type_list_header)
    public void productTypeHeaderClicked() {
        productTypesArrowView.setVisibility(View.INVISIBLE);
        AnimationUtils.expandWhileScrollingDown(productTypeList, tenantDetailScrollView, PRODUCT_SCROLL_DISTANCE);
    }

    @OnClick(R.id.description_view)
    public void onDescriptionViewClick() {
        if (isDescriptionExpandable()) {
            trackDetailsExpanded();
            boolean isCollapsed = descriptionView.getMaxLines() == descriptionView.getMinLines();
            if (isCollapsed) {
                int duration = (descriptionView.getLineCount() - descriptionView.getMinLines()) * ANIMATION_DURATION_RATIO;
                ObjectAnimator.ofInt(descriptionView, "maxLines", descriptionView.getLineCount()).setDuration(duration).start();
                descriptionScrim.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.hours_layout)
    public void onHoursViewClick() {
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantHours, null, tenant.getName().toLowerCase());
        if (isHoursExpandable()) {
            boolean isCollapsed = hoursView.getMaxLines() == hoursView.getMinLines();
            if (isCollapsed) {
                int duration = (hoursView.getLineCount() - hoursView.getMinLines()) * ANIMATION_DURATION_RATIO;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(ObjectAnimator.ofInt(weekdayView, "maxLines", hoursView.getLineCount()), ObjectAnimator.ofInt(hoursView, "maxLines", hoursView.getLineCount()));
                animatorSet.setDuration(duration);
                animatorSet.start();
                hoursArrowView.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.website_layout)
    public void onWebsiteViewClick() {
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantWebsite, null, tenant.getName().toLowerCase());
        IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(tenant.getWebsiteUrl())), this);
    }

    @OnClick(R.id.map_view)
    public void onMapViewClick() {
        analyticsManager.trackAction(AnalyticsManager.Actions.TenantMap, null, navigationTenant.getName().toLowerCase());
        startActivity(TenantMapActivity.buildIntent(this, navigationTenant));
    }
}
