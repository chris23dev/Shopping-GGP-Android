package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.TenantSearchActivity;
import com.ggp.theclub.adapter.ParkingDateListAdapter;
import com.ggp.theclub.adapter.ParkingTimeListAdapter;
import com.ggp.theclub.api.ParkMeClient;
import com.ggp.theclub.event.DateSelectEvent;
import com.ggp.theclub.event.TenantSelectEvent;
import com.ggp.theclub.event.TimeRangeSelectEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.ExcludedTenants;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.model.ParkingLot;
import com.ggp.theclub.model.ParkingLotThreshold;
import com.ggp.theclub.model.ParkingLotsResponse;
import com.ggp.theclub.model.ParkingTimeRange;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.ColorUtils;
import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.ParkingUtils;
import com.ggp.theclub.util.TenantUtils;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.OnClick;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingAvailabilityFragment extends MapFragment {
    @BindColor(R.color.black) int blackColor;
    @BindColor(R.color.gray) int grayColor;
    @BindString(R.string.tenant_search_hint) String parkingProximityViewText;
    @Bind(R.id.background_fade_view) View backgroundFadeView;
    @Bind(R.id.arrival_time_view) TextView arrivalTimeView;
    @Bind(R.id.parking_proximity_view) TextView parkingProximityView;
    @Bind(R.id.parking_proximity_clear_button) LinearLayout parkingProximityClearButton;
    @Bind(R.id.parking_availability_arrival) TextView parkingAvailabilityArrival;
    @Bind(R.id.done_button) Button parkingAvailabilityDone;
    @Bind(R.id.parking_availability_proximity) TextView parkingAvailabilityProximity;
    @Bind(R.id.arrival_time_selector_layout) LinearLayout arrivalTimeSelectorLayout;
    @Bind(R.id.date_selector_list) RecyclerView dateSelectorList;
    @Bind(R.id.time_selector_list) RecyclerView timeSelectorList;

    private final int NUM_CLOSEST_PARKING_LOTS = 3;
    private final int NUM_SELECTABLE_DATES = 7;
    private final String TIME_FORMAT = MallApplication.getApp().getResources().getString(R.string.parking_availability_format);
    private final String TIME_FORMAT_TODAY = MallApplication.getApp().getResources().getString(R.string.parking_availability_today_format);
    private Mall mall = mallManager.getMall();
    private Tenant selectedTenant;
    private LocalDateTime selectedDateTime = HoursUtils.getDateTimeForMall(mall);
    private ParkingTimeRange selectedTimeRange = ParkingTimeRange.NOW;
    private ParkingTimeListAdapter parkingTimeListAdapter = new ParkingTimeListAdapter();
    private List<ParkingLot> allParkingLots = new ArrayList<>();
    private List<Integer> closestParkingLotIds = new ArrayList<>();
    private List<Tenant> allTenants;

    public static ParkingAvailabilityFragment newInstance() {
        return new ParkingAvailabilityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.parking_availability_fragment, container);
        parkingAvailabilityArrival.setText(getString(R.string.parking_availability_arrival));
        parkingAvailabilityDone.setText(getString(R.string.parking_availability_done));
        parkingAvailabilityProximity.setText(getString(R.string.parking_availability_proximity));
        parkingProximityView.setText(getString(R.string.tenant_search_hint));
        mapStatusView.setText(getString(R.string.map_loading));
        arrivalTimeView.setText(getString(R.string.parking_availability_now));
        parkingAvailabilityDone.setText(getString(R.string.parking_availability_done));
        return view;
    }

    @Override
    protected void configureView() {
        fetchParkingLots();
        configureDatePicker();
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        mapManager.showParkingZones();
        if (selectedTenant != null && allTenants != null) {
            handleTenantSelection();
        }
        analyticsManager.trackScreen(AnalyticsManager.Screens.ParkingAvailability);
    }

    @Override
    public void onFragmentInvisible() {
        mapManager.hideParkingZones();
        mapManager.hideMapMarkerPin();
        super.onFragmentInvisible();
    }

    public void onEvent(DateSelectEvent event) {
        LocalDate date = event.getDate();
        selectedDateTime = selectedDateTime.withDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        updateSelectableTimes();
        updateSelectedTimeText();
        fetchParkingLots();
    }

    public void onEvent(TimeRangeSelectEvent event) {
        selectedDateTime = ParkingUtils.getParkingDateTime(event.getTimeRange(), selectedDateTime.toLocalDate(), mallManager.getMall().getTimeZone());
        selectedTimeRange = event.getTimeRange();
        updateSelectedTimeText();
        fetchParkingLots();
    }

    private void configureDatePicker() {
        dateSelectorList.setLayoutManager(new LinearLayoutManager(dateSelectorList.getContext(), LinearLayoutManager.HORIZONTAL, false));
        timeSelectorList.setLayoutManager(new LinearLayoutManager(timeSelectorList.getContext(), LinearLayoutManager.HORIZONTAL, false));
        dateSelectorList.setAdapter(new ParkingDateListAdapter(getParkingDates()));
        timeSelectorList.setAdapter(parkingTimeListAdapter);
    }

    private void fetchParkingLots() {
        HashMap params = ParkMeClient.getLotParameters(selectedDateTime);
        String location = ParkMeClient.getLocation(mall.getLatitude(), mall.getLongitude());
        int radius = ParkMeClient.getRadius();
        ParkMeClient.getInstance().getParkMeApi().getParkingLots(location, radius, params).enqueue(new Callback<ParkingLotsResponse>() {
            @Override
            public void onResponse(Call<ParkingLotsResponse> call, Response<ParkingLotsResponse> response) {
                if (response.isSuccessful()) {
                    allParkingLots = response.body().getParkingLots();
                    highlightParkingLots(closestParkingLotIds.isEmpty() ? allParkingLots : getClosestParkingLots());
                    mapLevelAdapter.enableParkingIndicators();
                    mapManager.setCurrentLevel(mapManager.getAllParkingLevel());
                }
            }

            @Override
            public void onFailure(Call<ParkingLotsResponse> call, Throwable t) {
                Log.w(getFragmentTag(), t);
            }
        });
    }

    private List<ParkingLot> getClosestParkingLots() {
        return StreamSupport.stream(allParkingLots).filter(parkingLot -> closestParkingLotIds.contains(parkingLot.getParkingLotId())).collect(Collectors.toList());
    }

    private void highlightParkingLots(List<ParkingLot> parkingLots) {
        Map<Integer, Integer> parkingLotIdToColorMap = StreamSupport.stream(parkingLots)
                .filter(this::isValidParkingLot).collect(Collectors.toMap(ParkingLot::getParkingLotId, this::getParkingLotColor));
        mapManager.highlightParkingZones(parkingLotIdToColorMap);
    }

    private int getParkingLotColor(ParkingLot parkingLot) {
        ParkingLotThreshold threshold = determineLotThreshold(parkingLot.getOccupancies().get(0).getOccupancyPercentage(), mall.getMallConfig().getLotThresholds());
        if (threshold != null) {
            return ColorUtils.createColor(threshold.getColorHex(), threshold.getAlphaPercentage());
        }
        return 0;
    }

    private ParkingLotThreshold determineLotThreshold(int occupancyPercentage, List<ParkingLotThreshold> lotThresholds) {
        Optional<ParkingLotThreshold> thresholdOptional = StreamSupport.stream(lotThresholds)
                .filter(lotThreshold -> isWithinThreshold(occupancyPercentage, lotThreshold))
                .findFirst();

        return thresholdOptional.isPresent() ? thresholdOptional.get() : null;
    }

    private boolean isWithinThreshold(int occupancyPercentage, ParkingLotThreshold threshold) {
        return occupancyPercentage >= threshold.getMinPercentage() && occupancyPercentage <= threshold.getMaxPercentage();
    }

    private boolean isValidParkingLot(ParkingLot parkingLot) {
        return (parkingLot != null) && parkingLot.getEncodedPolylines() != null &&
                parkingLot.getEncodedPolylines().size() > 0 &&
                parkingLot.getOccupancies() != null &&
                parkingLot.getOccupancies().size() > 0;
    }

    private List<LocalDate> getParkingDates() {
        List<LocalDate> availableDates = new ArrayList<>();
        for(int i = 0; i < NUM_SELECTABLE_DATES; i++) {
            LocalDate date = new LocalDate().plusDays(i);
            availableDates.add(date);
        }
        return availableDates;
    }

    private void updateSelectableTimes() {
        Boolean shouldHideNow = !LocalDate.now().equals(new LocalDate(selectedDateTime));
        parkingTimeListAdapter.setFirstElementHidden(shouldHideNow);
    }

    private void updateSelectedTimeText() {
        String text;
        if(selectedTimeRange.equals(ParkingTimeRange.NOW)) {
            text = ParkingUtils.getTimeRangeText(selectedTimeRange);
        } else if(new LocalDate(selectedDateTime).equals(LocalDate.now())) {
            text = String.format(TIME_FORMAT_TODAY, ParkingUtils.getTimeRangeText(selectedTimeRange));
        } else {
            text = String.format(TIME_FORMAT, ParkingUtils.getTimeRangeText(selectedTimeRange), DateUtils.getParkingDateString(selectedDateTime));
        }

        arrivalTimeView.setText(text);
    }

    private void updateParkingProximityView(Tenant tenant) {
        String locationText = TenantUtils.hasParentTenant(tenant) ? tenant.getLocationDescription() : mapManager.getTenantShortLocationByLeaseId(tenant.getLeaseId());
        String parkingProximityText = String.format("%s %s", tenant.getName(), locationText);
        parkingProximityView.setText(parkingProximityText);
        parkingProximityView.setTextColor(blackColor);
        AnimationUtils.enterReveal(parkingProximityClearButton);
    }

    private void updateParkingProximityMap(Tenant tenant) {
        List<Integer> parkingLotIds = StreamSupport.stream(allParkingLots).map(ParkingLot::getParkingLotId).collect(Collectors.toList());
        closestParkingLotIds = StreamSupport.stream(mapManager.getClosestParkingZoneIdsByLeaseId(tenant.getLeaseId()))
                .filter(parkingLotIds::contains).limit(NUM_CLOSEST_PARKING_LOTS).collect(Collectors.toList());
        highlightParkingLots(getClosestParkingLots());
        mapManager.setCurrentLevel(mapManager.getBestParkingLevel());
        mapManager.showMapMarkerPin(tenant.getLeaseId());
    }

    public void onEvent(TenantSelectEvent event) {
        if(event.getEventType() == TenantSelectEvent.EventType.PARKING) {
            selectedTenant = event.getTenant();
            handleTenantSelection();

            HashMap<String, Object> contextData = new HashMap<>();
            contextData.put(AnalyticsManager.ContextDataKeys.ParkingStore, selectedTenant.getName());
            analyticsManager.trackAction(AnalyticsManager.Actions.ParkingForStore, contextData, selectedTenant.getName());
        }
    }

    private void handleTenantSelection() {
        new Handler().postDelayed(() -> {
            updateParkingProximityView(selectedTenant);
            updateParkingProximityMap(TenantUtils.hasParentTenant(selectedTenant) ? TenantUtils.getParentTenant(selectedTenant, allTenants) : selectedTenant);
        }, 200);
    }

    @OnClick(R.id.arrival_time_button)
    public void onArrivalTimeButtonClick() {
        arrivalTimeSelectorLayout.setVisibility(View.VISIBLE);
        backgroundFadeView.setVisibility(View.VISIBLE);
        AnimationUtils.exitReveal(arrivalTimeView, View.INVISIBLE);
    }

    @OnClick(R.id.done_button)
    public void onDoneButtonClick() {
        arrivalTimeSelectorLayout.setVisibility(View.GONE);
        backgroundFadeView.setVisibility(View.GONE);
        AnimationUtils.enterReveal(arrivalTimeView);

        HashMap<String, Object> contextData = new HashMap<>();
        int daysInAdvance = Days.daysBetween(HoursUtils.getDateTimeForMall(mall), selectedDateTime).getDays();
        contextData.put(AnalyticsManager.ContextDataKeys.ParkingDaysInAdvance, daysInAdvance);
        contextData.put(AnalyticsManager.ContextDataKeys.ParkingTimeOfDay, ParkingUtils.getTimeRangeText(selectedTimeRange));
        analyticsManager.trackAction(AnalyticsManager.Actions.ParkingAvailabilitySelect, contextData);
    }

    @OnClick(R.id.parking_proximity_container)
    public void onParkingProximityViewClick() {
        mallRepository.queryForTenants((tenants) -> {
            allTenants = tenants;
            List<Tenant> excludedTenants = StreamSupport.stream(tenants)
                    .filter(store -> !mapManager.isDestinationMapped(store.getLeaseId()))
                    .collect(Collectors.toList());
            startActivity(TenantSearchActivity.buildIntent(getActivity(), TenantSelectEvent.EventType.PARKING, new ExcludedTenants(excludedTenants)));
        });
    }

    @OnClick(R.id.parking_proximity_clear_button)
    public void onClearButtonClick() {
        selectedTenant = null;
        parkingProximityView.setText(parkingProximityViewText);
        parkingProximityView.setTextColor(grayColor);
        AnimationUtils.exitReveal(parkingProximityClearButton, View.INVISIBLE);
        highlightParkingLots(allParkingLots);
        closestParkingLotIds.clear();
        mapManager.hideMapMarkerPin();
        mapManager.setCurrentLevel(mapManager.getAllParkingLevel());
    }
}
