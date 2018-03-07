package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ggp.theclub.R;
import com.ggp.theclub.event.MapLevelUpdateEvent;
import com.ggp.theclub.event.TenantSelectEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.ExcludedTenants;
import com.ggp.theclub.model.MapLevel;
import com.ggp.theclub.model.MapMoverType;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import de.greenrobot.event.EventBus;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class WayfindActivity extends MapActivity {
    @BindColor(R.color.gray) int grayColor;
    @BindColor(R.color.black) int blackColor;
    @BindString(R.string.wayfinding_start_placeholder) String startPlaceholderText;
    @BindString(R.string.wayfinding_end_placeholder) String endPlaceholderText;
    @BindString(R.string.wayfinding_sis_title) String storeInStoreAlertTitle;
    @BindString(R.string.wayfinding_sis_message_format) String storeInStoreAlertMessageFormat;
    @BindString(R.string.dismiss_alert) String storeInStoreAlertDismissText;
    @BindString(R.string.tenant_child_location_format) String childTenantLocationFormat;
    @Bind(R.id.start_view) TextView startView;
    @Bind(R.id.end_view) TextView endView;
    @Bind(R.id.start_location_view) TextView startLocationView;
    @Bind(R.id.end_location_view) TextView endLocationView;
    @Bind(R.id.level_layout) LinearLayout levelLayout;
    @Bind(R.id.level_spinner) Spinner levelSpinner;
    @Bind(R.id.wayfind_bottom_sheet) LinearLayout wayfindBottomSheet;
    @Bind(R.id.disclaimer_view) TextView disclaimerView;
    @Bind(R.id.directions_layout) LinearLayout directionsLayout;
    @Bind(R.id.previous_button) LinearLayout previousButton;
    @Bind(R.id.next_button) FloatingActionButton nextButton;
    @Bind(R.id.icon_view) TextView iconView;
    @Bind(R.id.direction_view) TextView directionView;
    @Bind(R.id.next_view) TextView nextView;
    @Bind(R.id.wayfinding_start) TextView wayfindingStart;
    @Bind(R.id.wayfinding_level) TextView wayfindingLevel;
    @Bind(R.id.wayfinding_end) TextView wayfindingEnd;
    private TextView levelToastView;
    private Toast levelToast;
    private BottomSheetBehavior bottomSheetBehavior;
    private Tenant startTenant;
    private Tenant endTenant;
    private int selectedPickerView;
    private List<MapLevel> startTenantLevels;
    private List<Tenant> allTenants = new ArrayList<>();

    private final HashMap<MapMoverType, Integer> moverIconLookup = new HashMap<MapMoverType, Integer>() {{
        put(MapMoverType.Stairs, R.string.stairs_icon);
        put(MapMoverType.Escalator, R.string.escalator_icon);
        put(MapMoverType.Elevator, R.string.elevator_icon);
        put(MapMoverType.Pin, R.string.location_icon);
    }};

    public static Intent buildIntent(Context context, Tenant tenant) {
        return buildIntent(context, WayfindActivity.class, tenant);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mallRepository.queryForTenants((tenants) -> {
            allTenants = tenants;
            setEndTenant(getIntentExtra(Tenant.class));
        });

        setContentView(R.layout.wayfind_activity);
        mapStatusView.setText(getString(R.string.map_loading));
        wayfindingStart.setText(getString(R.string.wayfinding_start));
        wayfindingLevel.setText(getString(R.string.wayfinding_level));
        wayfindingEnd.setText(getString(R.string.wayfinding_end));
        nextView.setText(getString(R.string.wayfinding_next_step));
        disclaimerView.setText(getString(R.string.wayfinding_disclaimer));

    }

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Wayfinding);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkSelectedTenants();
    }

    @Override
    public void onDestroy() {
        mapManager.resetWaypoints();
        super.onDestroy();
    }

    @Override
    protected void configureView() {
        enableBackButton();
        updatePickerViews();
        bottomSheetBehavior = BottomSheetBehavior.from(wayfindBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        layoutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        levelToastView = (TextView) getLayoutInflater().inflate(R.layout.level_toast, null);
        levelToast = new Toast(this);
        levelToast.setGravity(Gravity.CENTER, 0, 0);
        levelToast.setDuration(Toast.LENGTH_SHORT);
    }

    private void setStartTenant(Tenant tenant) {
        if(TenantUtils.hasParentTenant(tenant)) {
            tenant.setLocationDescription(getChildTenantLocationDescription(tenant, allTenants));
        }
        startTenant = tenant;
        updatePickerViews();
        updateStartWaypoint();
    }

    private void setEndTenant(Tenant tenant) {
        if(TenantUtils.hasParentTenant(tenant)) {
            tenant.setLocationDescription(getChildTenantLocationDescription(tenant, allTenants));
        }
        endTenant = tenant;
        if (layoutView != null) {
            updatePickerViews();
        }
        updateEndWaypoint();
    }

    private String getChildTenantLocationDescription(Tenant tenant, List<Tenant> allTenants) {
        if(TenantUtils.hasParentTenant(tenant)) {
            Tenant parentTenant = TenantUtils.getParentTenant(tenant, allTenants);
            return String.format(childTenantLocationFormat, parentTenant.getName());
        } else {
            return null;
        }
    }

    private void updatePickerViews() {
        startView.setText(startTenant != null ? startTenant.getName() : startPlaceholderText);
        startView.setTextColor(startTenant != null ? blackColor : grayColor);
        endView.setText(endTenant != null ? endTenant.getName() : endPlaceholderText);
        endView.setTextColor(endTenant != null ? blackColor : grayColor);
        updateLocationDescriptions();
        AnimationUtils.enterReveal(startView);
        AnimationUtils.enterReveal(endView);
    }

    private void updateLocationDescriptions() {
        if (startTenant != null && !StringUtils.isEmpty(startTenant.getLocationDescription())) {
            startLocationView.setText(startTenant.getLocationDescription());
            AnimationUtils.enterReveal(startLocationView);
        } else {
            startLocationView.setVisibility(View.GONE);
        }

        if (endTenant != null && !StringUtils.isEmpty(endTenant.getLocationDescription())) {
            endLocationView.setText(endTenant.getLocationDescription());
            AnimationUtils.enterReveal(endLocationView);
        } else {
            endLocationView.setVisibility(View.GONE);
        }
    }

    private void updateStartWaypoint() {
        if (startTenant != null) {
            boolean hasParentStore = TenantUtils.hasParentTenant(startTenant);
            if (hasParentStore) {
                Tenant parentTenant = TenantUtils.getParentTenant(startTenant, allTenants);
                List<MapLevel> parentTenantLevels = mapManager.getLevelsByLeaseId(parentTenant.getLeaseId());
                startTenantLevels = parentTenantLevels.isEmpty() ? startTenantLevels : parentTenantLevels.subList(0, 1);
                updateLevelSpinner(parentTenant);
            } else {
                startTenantLevels = mapManager.getLevelsByLeaseId(startTenant.getLeaseId());
                updateLevelSpinner(startTenant);
            }
        } else {
            AnimationUtils.exitReveal(levelLayout);
            mapManager.resetStartWaypoint();
        }
    }

    private void updateEndWaypoint() {
        if (endTenant != null) {
            boolean hasParentStore = TenantUtils.hasParentTenant(endTenant);
            mapManager.setEndWaypoint(hasParentStore ? TenantUtils.getParentTenant(endTenant, allTenants).getLeaseId() : endTenant.getLeaseId());
        } else {
            mapManager.resetEndWaypoint();
        }
    }

    private void updateLevelSpinner(Tenant tenant) {
        if (startTenantLevels.isEmpty()) {
            AnimationUtils.exitReveal(levelLayout);
        } else if (startTenantLevels.size() == 1) {
            AnimationUtils.exitReveal(levelLayout);
            mapManager.setStartWaypoint(tenant.getLeaseId(), startTenantLevels.get(0).getLevel());
        } else {
            List<String> fromTenantLevelNames = StreamSupport.stream(startTenantLevels).map(MapLevel::getDescription).collect(Collectors.toList());
            levelSpinner.setAdapter(new ArrayAdapter<>(levelSpinner.getContext(), R.layout.level_list_item, R.id.level_name, fromTenantLevelNames));
            AnimationUtils.enterReveal(levelLayout);
            mapManager.setStartWaypoint(tenant.getLeaseId(), startTenantLevels.get(0).getLevel());
        }
    }

    private void showLevelToast(int level) {
        levelToastView.setText(mapManager.getMapLevels().get(level).getDescription());
        levelToast.setView(levelToastView);
        levelToast.show();
    }

    private void trackWayfindingAnalytics() {
        if(startTenant != null && endTenant != null) {
            List<MapLevel> mapLevels = mapManager.getMapLevels();
            int startLevel = mapManager.getStartWaypointLevel();
            int endLevel = mapManager.getEndWaypointLevel();
            String startLevelDescription = startLevel >= 0 ? mapLevels.get(startLevel).getDescription().toLowerCase() : null;
            String endLevelDescription = endLevel >= 0 ? mapLevels.get(endLevel).getDescription().toLowerCase() : null;

            HashMap<String, Object> contextData = new HashMap<String, Object>(){{
                put(AnalyticsManager.ContextDataKeys.WayfindingStart, startTenant.getName().toLowerCase());
                put(AnalyticsManager.ContextDataKeys.WayfindingEnd, endTenant.getName().toLowerCase());
                put(AnalyticsManager.ContextDataKeys.WayfindingStartLevel, startLevelDescription);
                put(AnalyticsManager.ContextDataKeys.WayfindingEndLevel, endLevelDescription);
            }};
            analyticsManager.trackAction(AnalyticsManager.Actions.WayfindingNavigate, contextData);
        }
    }

    private void startSearchActivity(Tenant selectedTenant) {
        List<Tenant> excludedTenants = new ArrayList<>();
        if (selectedTenant != null) {
            excludedTenants = StreamSupport.stream(allTenants)
                    .filter(store -> selectedTenant.getId() == store.getId() || !mapManager.isDestinationMapped(store.getLeaseId()))
                    .collect(Collectors.toList());
        }
        startActivity(TenantSearchActivity.buildIntent(this, TenantSelectEvent.EventType.WAYFINDING, new ExcludedTenants(excludedTenants)));
    }

    private void checkSelectedTenants() {
        TenantSelectEvent event = EventBus.getDefault().getStickyEvent(TenantSelectEvent.class);
        if (event != null) {
            onTenantSelection(event);
        }
    }

    public void onTenantSelection(TenantSelectEvent event) {
        if(event.getEventType() == TenantSelectEvent.EventType.WAYFINDING) {
            Tenant tenant = event.getTenant();
            if (selectedPickerView == R.id.start_layout) {
                startTenant = tenant;
                updatePickerViews();

                if (isStoreInStore(startTenant, endTenant)) {
                    showStoreInStoreAlert(() -> setStartTenant(null));
                } else {
                    setStartTenant(tenant);
                    //If the tenant has multiple levels, analytics gets triggered from the Level select spinner
                    if (tenant != null && mapManager.getLevelsByLeaseId(tenant.getLeaseId()).size() <= 1) {
                        trackWayfindingAnalytics();
                    }
                }
            } else {
                endTenant = tenant;
                updatePickerViews();

                if (isStoreInStore(startTenant, endTenant)) {
                    showStoreInStoreAlert(() -> setEndTenant(null));
                } else {
                    setEndTenant(tenant);
                    trackWayfindingAnalytics();
                }
            }
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    public void showStoreInStoreAlert(Runnable runnable) {
        String storeInStoreAlertMessage = isParent(startTenant, endTenant) ?
                String.format(storeInStoreAlertMessageFormat, startTenant.getName(), endTenant.getName()) :
                String.format(storeInStoreAlertMessageFormat, endTenant.getName(), startTenant.getName());
        AlertUtils.showGenericDialog(storeInStoreAlertTitle, storeInStoreAlertMessage, storeInStoreAlertDismissText, this, runnable);
    }

    private boolean isStoreInStore(Tenant tenant1, Tenant tenant2) {
        return isParent(tenant1, tenant2) || isParent(tenant2, tenant1);
    }

    private boolean isParent(Tenant tenant1, Tenant tenant2) {
        if (tenant1 != null && tenant2 != null) {
            if (TenantUtils.hasParentTenant(tenant1) && tenant1.getParentId().equals(tenant2.getId())) {
                return true;
            }
        }
        return false;
    }

    public void onEvent(MapLevelUpdateEvent event) {
        if (event.isWayfindLevel()) {
            showLevelToast(event.getLevel());
            disclaimerView.setVisibility(View.GONE);
            if (!event.isStartWayfindLevel() || !event.isEndWayfindLevel()) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                iconView.setTextColor(event.getMapMoverType().equals(MapMoverType.Pin) ? grayColor : blackColor);
                iconView.setText(moverIconLookup.get(event.getMapMoverType()));
                previousButton.setVisibility(event.isStartWayfindLevel() ? View.GONE : View.VISIBLE);
                nextButton.setVisibility(event.isEndWayfindLevel() ? View.GONE : View.VISIBLE);
                nextView.setVisibility(event.isEndWayfindLevel() ? View.INVISIBLE : View.VISIBLE);
                directionView.setText(event.getTextDirection());
                directionsLayout.setVisibility(View.VISIBLE);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                nextButton.setVisibility(View.GONE);
            }
        } else if (!disclaimerView.isShown()) {
            showLevelToast(event.getLevel());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            nextButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.swap_button)
    public void onSwapButtonClick() {
        if (startTenant != null || endTenant != null) {
            Tenant newToTenant = startTenant;
            setStartTenant(endTenant);
            setEndTenant(newToTenant);
            //If the tenant has multiple levels, analytics gets triggered from the Level select spinner
            if(startTenant != null && mapManager.getLevelsByLeaseId(startTenant.getLeaseId()).size() <= 1) {
                trackWayfindingAnalytics();
            }
        }
    }

    @OnClick({R.id.start_layout, R.id.end_layout})
    public void onPickerViewClick(View view) {
        selectedPickerView = view.getId();
        Tenant excludedTenant = selectedPickerView == R.id.start_layout ? endTenant : startTenant;
        startSearchActivity(excludedTenant);
    }

    @OnItemSelected(R.id.level_spinner)
    public void onLevelSpinnerItemSelected(int position) {
        mapManager.setStartWaypoint(startTenant.getLeaseId(), startTenantLevels.get(position).getLevel());
        trackWayfindingAnalytics();
    }

    @OnClick(R.id.previous_button)
    public void onPreviousButtonClick() {
        mapManager.goToPreviousWayfindLevel();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        mapManager.goToNextWayfindLevel();
    }
}