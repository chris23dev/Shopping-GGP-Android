package com.ggp.theclub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.event.TenantSelectEvent;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.TenantUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class TenantSearchAdapter extends TenantFilterAdapter {
    private TenantSelectEvent.EventType eventType;
    private Map<String, Integer> tenantCountMap = new HashMap<>();
    private List<Tenant> allTenants = new ArrayList<>();

    public TenantSearchAdapter(List<Tenant> allTenants, List<Tenant> searchableTenants, TenantSelectEvent.EventType eventType) {
        super(searchableTenants);
        this.eventType = eventType;
        this.allTenants = allTenants;
        tenantCountMap = TenantUtils.getTenantNamesGroupedByOccurrence(searchableTenants);
    }

    @Override
    public TenantListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_search_item, parent, false);
        return new TenantListViewHolder(view);
    }

    public class TenantListViewHolder extends TenantFilterViewHolder {
        @Bind(R.id.tenant_name) TextView nameView;
        @Bind(R.id.tenant_location) TextView locationView;
        @BindString(R.string.tenant_child_location_format) String childTenantLocationFormat;

        public TenantListViewHolder(View view) {
            super(view);
        }

        public void onBind(Tenant tenant) {
            String name = tenant.getName();
            if(tenantCountMap.containsKey(name) && tenantCountMap.get(name) > 1) {
                String locationText = MapManager.getInstance().getTenantLocationByLeaseId(tenant.getLeaseId());
                setLocationText(locationText);
            } else if(TenantUtils.hasParentTenant(tenant)) {
                String locationText = getChildStoreLocationDescription(tenant);
                setLocationText(locationText);
            } else {
                locationView.setVisibility(View.GONE);
            }
            nameView.setText(name);
        }

        private void setLocationText(String locationText) {
            locationView.setText(locationText);
            locationView.setVisibility(View.VISIBLE);
        }

        private String getChildStoreLocationDescription(Tenant tenant) {
            Tenant parentTenant = TenantUtils.getParentTenant(tenant, allTenants);
            return String.format(childTenantLocationFormat, parentTenant.getName());
        }

        @OnClick(R.id.item_view)
        public void onClick() {
            Tenant tenant = tenants.get(getAdapterPosition());
            String name = tenant.getName();
            if(TenantUtils.hasParentTenant(tenant)) {
                tenant.setLocationDescription(getChildStoreLocationDescription(tenant));
            } else if(tenantCountMap.containsKey(name) && tenantCountMap.get(name) > 1) {
                String locationText = MapManager.getInstance().getTenantLocationByLeaseId(tenant.getLeaseId());
                tenant.setLocationDescription(locationText);
            }

            EventBus.getDefault().postSticky(new TenantSelectEvent(tenant, eventType));
        }
    }
}
