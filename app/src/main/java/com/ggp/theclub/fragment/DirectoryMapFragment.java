package com.ggp.theclub.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.TenantActivity;
import com.ggp.theclub.activity.WayfindActivity;
import com.ggp.theclub.event.FilterUpdateEvent.FilterType;
import com.ggp.theclub.event.MapReadyEvent;
import com.ggp.theclub.event.MapSelectEvent;
import com.ggp.theclub.event.TenantsFilterUpdateEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MapAmenityFilter;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.TenantCategory;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.CategoryUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.ProductUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantCategoryUtils;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.FilterView;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class DirectoryMapFragment extends MapFragment {
    @Bind(R.id.fab) FloatingActionButton wayfindingFab;
    @Bind(R.id.filter_fab) FloatingActionMenu filterFab;
    @Bind(R.id.tenant_sheet) View bottomSheet;
    @Bind(R.id.tenant_name) TextView tenantName;
    @Bind(R.id.tenant_category) TextView categoryView;
    @Bind(R.id.text_logo) TextView textLogo;
    @Bind(R.id.image_logo) ImageView imageLogo;
    @Bind(R.id.filter_view) FilterView filterView;
    @Bind(R.id.restroom_filter_button) View restroomButton;
    @Bind(R.id.atm_filter_button) View atmButton;
    @Bind(R.id.kiosk_filter_button) View kioskButton;
    @Bind(R.id.management_filter_button) View managementButton;
    private Tenant selectedTenant;
    private BottomSheetBehavior bottomSheetBehavior;
    private final HashMap<Integer, MapAmenityFilter> amenityFilterButtonLookup = new HashMap<Integer, MapAmenityFilter>() {{
        put(R.id.restroom_filter_button, MapAmenityFilter.RESTROOM);
        put(R.id.atm_filter_button, MapAmenityFilter.ATM);
        put(R.id.kiosk_filter_button, MapAmenityFilter.KIOSK);
        put(R.id.management_filter_button, MapAmenityFilter.MANAGEMENT);
    }};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableMapSelection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.bottom_sheet_map_layout, container);
        mapStatusView.setText(getString(R.string.map_loading));
        com.github.clans.fab.FloatingActionButton restroomBtn = (com.github.clans.fab.FloatingActionButton) restroomButton;
        restroomBtn.setLabelText(getString(R.string.amenity_restroom_label));
        com.github.clans.fab.FloatingActionButton atmBtn = (com.github.clans.fab.FloatingActionButton) atmButton;
        atmBtn.setLabelText(getString(R.string.amenity_atm_label));
        com.github.clans.fab.FloatingActionButton kioskBtn = (com.github.clans.fab.FloatingActionButton) kioskButton;
        kioskBtn.setLabelText(getString(R.string.amenity_kiosk_label));
        com.github.clans.fab.FloatingActionButton managementBtn = (com.github.clans.fab.FloatingActionButton) managementButton;
        managementBtn.setLabelText(getString(R.string.amenity_management_label));
        return view;
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        filterFab.close(false);
        analyticsManager.trackScreen(AnalyticsManager.Screens.DirectoryMap);
    }

    @Override
    public void onFragmentInvisible() {
        filterFab.close(false);
        super.onFragmentInvisible();
    }

    @Override
    public void onEvent(MapReadyEvent event) {
        super.onEvent(event);
        setupFilterMenu();
    }

    @Override
    protected void configureView() {
        setupFilterMenu();
        setupBottomSheet();
    }

    private void setFilterCountIcons(List<Tenant> filteredTenants) {
        if (!filteredTenants.isEmpty()) {
            List<Integer> filterCounts = mapManager.getTenantCountsPerLevel(filteredTenants);
            for (int i = 0; i < filterCounts.size(); i++) {
                mapLevelAdapter.setFilterIndicator(i, filterCounts.get(i));
            }
        }
    }

    private void hideFilterCountIcons() {
        mapLevelAdapter.clearFilterIndicators();
    }

    private void setupFilterMenu() {
        filterFab.setClosedOnTouchOutside(true);
        filterFab.setIconAnimated(false);
        filterFab.setOnMenuToggleListener(isOpen -> {
            int drawableId = isOpen ? R.drawable.icon_close : R.drawable.icon_filter;
            setFabIcon(drawableId, false);
        });

        filterFab.setOnMenuButtonClickListener((view) -> {
            filterFab.toggle(true);
            int drawableId = !filterFab.isOpened() ? R.drawable.icon_close : R.drawable.icon_filter;
            setFabIcon(drawableId, true);
        });

        restroomButton.setVisibility(mapManager.hasAmenitiesOfFilter(MapAmenityFilter.RESTROOM) ? View.VISIBLE: View.GONE);
        atmButton.setVisibility(mapManager.hasAmenitiesOfFilter(MapAmenityFilter.ATM) ? View.VISIBLE: View.GONE);
        kioskButton.setVisibility(mapManager.hasAmenitiesOfFilter(MapAmenityFilter.KIOSK) ? View.VISIBLE: View.GONE);
        managementButton.setVisibility(mapManager.hasAmenitiesOfFilter(MapAmenityFilter.MANAGEMENT) ? View.VISIBLE: View.GONE);
        filterFab.setVisibility(amentiesExist() ? View.VISIBLE : View.GONE);
    }

    private boolean amentiesExist() {
        return mapManager.hasAmenitiesOfFilter(MapAmenityFilter.RESTROOM) ||
                mapManager.hasAmenitiesOfFilter(MapAmenityFilter.ATM) ||
                mapManager.hasAmenitiesOfFilter(MapAmenityFilter.KIOSK) ||
                mapManager.hasAmenitiesOfFilter(MapAmenityFilter.MANAGEMENT);
    }

    private void setFabIcon(int drawableId, boolean shouldAnimate) {
        ImageView menuIconView = filterFab.getMenuIconView();
        menuIconView.invalidateDrawable(menuIconView.getDrawable());
        menuIconView.setImageBitmap(BitmapFactory.decodeResource(getResources(), drawableId));
        AnimationUtils.enterReveal(menuIconView, shouldAnimate);
    }

    private void populateBottomSheetTenant() {
        bottomSheet.setVisibility(View.VISIBLE);
        tenantName.setText(selectedTenant.getName());
        categoryView.setText(CategoryUtils.getDisplayNameForCategories(selectedTenant.getCategories()));
        ImageUtils.setLogo(imageLogo, textLogo, selectedTenant.getLogoUrl(), selectedTenant.getName());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        showWayfindingFab();
    }

    private void showWayfindingFab() {
        if (wayfindingFab.getVisibility() != View.VISIBLE && mallManager.getMall().getMallConfig().isWayfindingEnabled()) {
            AnimationUtils.enterReveal(wayfindingFab);
        }
    }

    private void hideWayfindingFab() {
        if (wayfindingFab.getVisibility() == View.VISIBLE) {
            AnimationUtils.exitReveal(wayfindingFab);
        }
    }

    private void showFilterFab() {
        if (!filterFab.isShown()) {
            AnimationUtils.enterReveal(filterFab);
        }
    }

    private void hideFilterFab() {
        if (filterFab.isShown()) {
            AnimationUtils.exitReveal(filterFab);
            filterFab.close(false);
        }
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if (selectedTenant != null) {
                            showWayfindingFab();
                            startActivity(TenantActivity.buildIntent(getActivity(), selectedTenant));
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        showWayfindingFab();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        hideWayfindingFab();
                        selectedTenant = null;
                        resetMap();
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        hideWayfindingFab();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void updateSelectedTenant() {
        if (selectedTenant != null) {
            if (mapManager.isMultiLevel()) {
                AnimationUtils.exitReveal(levelList);
            }
            hideFilterFab();
            populateBottomSheetTenant();
            showWayfindingFab();
        } else {
            resetMap();
        }
    }

    private void resetMap() {
        mapManager.setSelection(0);
        if (mapManager.isMultiLevel()) {
            AnimationUtils.enterReveal(levelList);
        }
        showFilterFab();
    }

    private void updateFilteredStores(TenantsFilterUpdateEvent event) {
        if (event == null) {
            return;
        }

        if (event.getFilterType().equals(FilterType.CATEGORY) && !event.getCategoryCode().equals(CategoryUtils.CATEGORY_ALL_STORES)) {
            handleCategoryFilter(event);
        } else {
            mallRepository.queryForTenants(tenants -> {
                List<Tenant> filteredTenants = new ArrayList<>();
                if (event.getFilterType().equals(FilterType.PRODUCT_TYPE) && !event.getProductTypeCode().equals(ProductUtils.PRODUCT_TYPE_ALL_STORES)) {
                    filteredTenants = TenantUtils.getTenantsByProductTypeCode(event.getProductTypeCode(), tenants);
                } else if (event.getFilterType().equals(FilterType.BRAND)) {
                    filteredTenants = TenantUtils.getTenantsByBrand(event.getBrand(), tenants);
                    event.setFilterCount(filteredTenants.size());
                }

                updateTenantsForEvent(filteredTenants, event);
            });
        }
    }

    private void handleCategoryFilter(TenantsFilterUpdateEvent event) {
        mallRepository.queryForTenantCategories(tenantCategories -> {
            TenantCategory tenantCategory = TenantCategoryUtils.findTenantCategory(tenantCategories, event.getCategoryCode());

            if (tenantCategory != null) {
                updateTenantsForEvent(tenantCategory.getTenants(), event);
            }
        });
    }

    private void updateTenantsForEvent(List<Tenant> tenants, TenantsFilterUpdateEvent event) {
        filterView.updateView(event, false);

        if (!tenants.isEmpty()) {
            clearAmenityHighlights();
            mapManager.setHighlights(TenantUtils.getTenantLeaseIds(tenants));
            setFilterCountIcons(tenants);
        } else if(!event.getFilterType().equals(FilterType.AMENITY)){
            clearAmenityHighlights();
            mapManager.setHighlights(new ArrayList<>());
            hideFilterCountIcons();
        }

    }

    private void clearAmenityHighlights() {
        mapManager.setFilteredAmenities(MapAmenityFilter.NONE);
        mapLevelAdapter.setAmenityFilterIndicator(MapAmenityFilter.NONE);
    }

    private void trackSelectedAmenity(MapAmenityFilter mapAmenityFilter) {
        HashMap<String, Object> contextData = new HashMap<>();
        String amenityCategoryName = null;
        switch (mapAmenityFilter) {
            case ATM: amenityCategoryName = AnalyticsManager.ContextDataValues.AmenityCategoryATM;
                break;
            case RESTROOM: amenityCategoryName = AnalyticsManager.ContextDataValues.AmenityCategoryRestrooms;
                break;
        }
        if (!StringUtils.isEmpty(amenityCategoryName)) {
            analyticsManager.safePut(AnalyticsManager.ContextDataKeys.AmenityCategoryName, amenityCategoryName, contextData);
            analyticsManager.trackAction(AnalyticsManager.Actions.AmenityCategory, contextData);
        }
    }

    public void onEvent(TenantsFilterUpdateEvent event) {
        updateFilteredStores(event);
    }

    public void onEvent(MapSelectEvent event) {
        if (event.getLeaseId() == 0) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            mallRepository.queryForTenants(stores -> {
                if (event.isAnchor()) {
                    selectedTenant = TenantUtils.getTenantByLeaseId(event.getLeaseId(), TenantUtils.getAnchorTenants(stores));
                } else {
                    selectedTenant = TenantUtils.getTenantByLeaseId(event.getLeaseId(), stores);
                }
                updateSelectedTenant();
            });
        }
    }

    @OnClick(R.id.tenant_sheet)
    public void sheetClicked() {
        startActivity(TenantActivity.buildIntent(getActivity(), selectedTenant));
    }

    @OnClick(R.id.fab)
    public void fabClicked() {
        startActivity(WayfindActivity.buildIntent(getActivity(), selectedTenant));
    }

    @OnClick({R.id.restroom_filter_button, R.id.atm_filter_button, R.id.kiosk_filter_button, R.id.management_filter_button})
    public void onFilterButtonClick(View view) {
        String filterLabel = ((com.github.clans.fab.FloatingActionButton) view).getLabelText();
        MapAmenityFilter mapAmenityFilter = amenityFilterButtonLookup.get(view.getId());
        mapManager.setFilteredAmenities(mapAmenityFilter);
        mapLevelAdapter.setAmenityFilterIndicator(mapAmenityFilter);
        TenantsFilterUpdateEvent filterUpdateEvent = new TenantsFilterUpdateEvent();
        filterUpdateEvent.setFilterCount(mapManager.getAmenityCount(mapAmenityFilter));
        filterUpdateEvent.setFilterType(FilterType.AMENITY);
        filterUpdateEvent.setFilterLabel(filterLabel);
        EventBus.getDefault().post(filterUpdateEvent);
        filterView.updateView(filterUpdateEvent, true);
        filterFab.close(true);
        trackSelectedAmenity(mapAmenityFilter);
    }
}
