package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.ParkingMapActivity;
import com.ggp.theclub.activity.TenantActivity;
import com.ggp.theclub.activity.TenantSearchActivity;
import com.ggp.theclub.event.TenantSelectEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.MallConfig;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantUtils;
import com.jibestream.jibestreamandroidlibrary.main.EngineView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ParkingOverviewFragment extends BaseFragment {
    @Bind(R.id.static_map_layout) FrameLayout staticMapLayout;
    @Bind(R.id.engine_view) EngineView engineView;
    @Bind(R.id.map_view) ImageView mapView;
    @Bind(R.id.parking_description) WebView parkingDescriptionView;
    @Bind(R.id.parking_search_layout) LinearLayout parkingSearchLayout;
    @Bind(R.id.parking_search_title) TextView parkingSearchTitle;
    @Bind(R.id.parking_search_description) TextView parkingSearchDescription;
    @Bind(R.id.tenant_info_layout) LinearLayout tenantInfoLayout;
    @Bind(R.id.tenant_search_box) TextView tenantSearchBox;
    @Bind(R.id.tenant_search_box_location) TextView tenantSearchBoxLocation;
    @Bind(R.id.tenant_name) TextView tenantNameView;
    @Bind(R.id.tenant_location) TextView tenantLocationView;
    @Bind(R.id.tenant_parking_location) TextView tenantParkingLocationView;
    @Bind(R.id.tenant_directions_view) TextView tenantDerectionView;
    @BindString(R.string.tenant_child_location_format) String childStoreLocationFormat;

    private Tenant tenant;
    private MapManager mapManager = MapManager.getInstance();
    private MallConfig mallConfig = mallManager.getMall().getMallConfig();

    public static ParkingOverviewFragment newInstance() {
        return new ParkingOverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFragmentVisible() {
        analyticsManager.trackScreen(AnalyticsManager.Screens.Parking);
    }

    @Override
    public void onDestroy() {
        destroyWebView();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.parking_overview_fragment, container);
        parkingSearchTitle.setText(getString(R.string.parking_search_title));
        parkingSearchDescription.setText(getString(R.string.parking_search_description));
        tenantDerectionView.setText(getString(R.string.get_directions));
        tenantSearchBox.setText(getString(R.string.tenant_search_hint));
        return view;
    }

    @Override
    protected void configureView() {
        configureParkingMap();
        updateTenantList();

        if (!StringUtils.isEmpty(mallManager.getMall().getParkingDescription())) {
            parkingDescriptionView.loadData(mallManager.getMall().getParkingDescription(), "text/html; charset=UTF-8", null);
        }
    }

    private void configureParkingMap() {
        if (!mallConfig.isParkingAvailabilityEnabled()) {
            staticMapLayout.setVisibility(View.VISIBLE);
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mapView.getWidth() > 0) {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mapManager.renderParkingImage(mapView, engineView, getActivity());
                    }
                }
            });
        }
    }

    private void destroyWebView() {
        parkingDescriptionView.removeAllViews();
        parkingDescriptionView.destroy();
    }

    private void onItemSelected(Tenant tenant) {
        this.tenant = tenant;
        mallRepository.queryForTenants(stores -> {
            Tenant navigationTenant = TenantUtils.hasParentTenant(tenant) ? TenantUtils.getParentTenant(tenant, stores) : tenant;
            tenantSearchBox.setText(tenant.getName());
            tenantNameView.setText(tenant.getName());
            tenantSearchBoxLocation.setText(TenantUtils.hasParentTenant(tenant) ? tenant.getLocationDescription() : null);
            tenantSearchBoxLocation.setVisibility(TenantUtils.hasParentTenant(tenant) ? View.VISIBLE : View.GONE);
            String tenantLocation = TenantUtils.hasParentTenant(tenant) ? tenant.getLocationDescription() : mapManager.getTenantLocationByLeaseId(tenant.getLeaseId());
            tenantLocationView.setText(tenantLocation);
            tenantLocationView.setVisibility(!StringUtils.isEmpty(tenantLocation) ? View.VISIBLE : View.GONE);
            String tenantParkingLocation = mapManager.getParkingLocationByLeaseId(navigationTenant.getLeaseId());
            tenantParkingLocationView.setText(tenantParkingLocation);
            tenantParkingLocationView.setVisibility(!StringUtils.isEmpty(tenantParkingLocation) ? View.VISIBLE : View.GONE);
            tenantInfoLayout.setVisibility(View.VISIBLE);
        });


        HashMap<String, Object> contextData = new HashMap<>();
        contextData.put(AnalyticsManager.ContextDataKeys.ParkingStore, tenant.getName());
        analyticsManager.trackAction(AnalyticsManager.Actions.ParkingForStore, contextData, tenant.getName());
    }

    private void trackParkingDirectionsAnalytics() {
        analyticsManager.trackAction(AnalyticsManager.Actions.GetParkingDirections, null, tenant.getName());
    }

    private void updateTenantList() {
        if (mallConfig.isParkingAvailable() && !mallConfig.isParkingAvailabilityEnabled()) {
            mallRepository.queryForTenants(stores -> {
                if (!stores.isEmpty()) {
                    parkingSearchLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void onEvent(TenantSelectEvent event) {
        if(event.getEventType() == TenantSelectEvent.EventType.PARKING) {
            onItemSelected(event.getTenant());
        }
    }

    @OnClick(R.id.tenant_search_box)
    public void onSearchBoxClick() {
        startActivity(TenantSearchActivity.buildIntent(getActivity(), TenantSelectEvent.EventType.PARKING, null));
    }

    @OnClick(R.id.map_view)
    public void onMapViewClick() {
        startActivity(ParkingMapActivity.buildIntent(getActivity()));
    }

    @OnClick(R.id.tenant_name)
    public void onTenantNameClick() {
        if (tenant != null) {
            startActivity(TenantActivity.buildIntent(getActivity(), tenant));
        }
    }

    @OnClick(R.id.tenant_directions_view)
    public void onTenantDirectionsViewClick() {
        trackParkingDirectionsAnalytics();
        IntentUtils.showDirectionsForTenant(tenant, getActivity());
    }
}
