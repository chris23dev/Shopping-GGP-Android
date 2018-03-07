package com.ggp.theclub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.RefineState;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.TenantUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import lombok.Getter;

public class RefineTenantFilterAdapter extends TenantFilterAdapter {
    private enum ViewType {FAVORITES_ITEM, TENANT_ITEM}

    private boolean hasFavoriteTenants;
    private List<Integer> favoriteTenantIds = new ArrayList<>();
    private FavoritesViewHolder favoritesViewHolder;
    @Getter private RefineState refineState;

    public RefineTenantFilterAdapter(List<Tenant> tenants, RefineState refineState) {
        super(tenants);

        if (MallApplication.getApp().getAccountManager().isLoggedIn()) {
            favoriteTenantIds = getFavoriteTenantIds(tenants);
            hasFavoriteTenants = !favoriteTenantIds.isEmpty();
            if (refineState.isFavoritesSelected()) {
                refineState.getSelectedTenantIds().addAll(favoriteTenantIds);
            }
        }

        this.refineState = refineState;
    }

    @Override
    public int getItemCount() {
        return hasFavoriteTenants ? super.getItemCount() + 1 : super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && hasFavoriteTenants) ? ViewType.FAVORITES_ITEM.ordinal() : ViewType.TENANT_ITEM.ordinal();
    }

    @Override
    public TenantFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refine_tenant_item, parent, false);
        if (viewType == ViewType.FAVORITES_ITEM.ordinal()) {
            favoritesViewHolder = new FavoritesViewHolder(view);
            return favoritesViewHolder;
        } else {
            return new TenantViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(TenantFilterViewHolder viewHolder, int position) {
        if (hasFavoriteTenants && getItemViewType(position) == ViewType.TENANT_ITEM.ordinal()) {
            super.onBindViewHolder(viewHolder, position - 1);
        } else {
            super.onBindViewHolder(viewHolder, position);
        }
    }

    private List<Integer> getFavoriteTenantIds(List<Tenant> tenants) {
        return StreamSupport.stream(TenantUtils.getFavoriteTenants(tenants)).map(Tenant::getId).collect(Collectors.toList());
    }

    public void clearSelectedTenants() {
        refineState.getSelectedTenantIds().clear();
        if (favoritesViewHolder != null) {
            favoritesViewHolder.clearCheckBox();
        }
        notifyDataSetChanged();
    }

    public class FavoritesViewHolder extends TenantFilterViewHolder{
        @Bind(R.id.name_view) TextView nameView;
        @Bind(R.id.select_checkbox) Checkable selectCheckBox;

        public FavoritesViewHolder(View view) {
            super(view);
        }

        @Override
        public void onBind(Tenant tenant) {
            nameView.setText(R.string.refine_tenant_favorite);
            selectCheckBox.setChecked(refineState.isFavoritesSelected());
        }

        public void clearCheckBox() {
            refineState.setFavoritesSelected(false);
            selectCheckBox.setChecked(false);
        }

        @OnClick(R.id.item_view)
        public void onItemViewClick() {
            selectCheckBox.toggle();
            if (selectCheckBox.isChecked()) {
                refineState.setFavoritesSelected(true);
                refineState.getSelectedTenantIds().addAll(favoriteTenantIds);
            } else {
                refineState.setFavoritesSelected(false);
                refineState.getSelectedTenantIds().removeAll(favoriteTenantIds);
            }
            notifyDataSetChanged();
        }
    }

    public class TenantViewHolder extends TenantFilterViewHolder {
        @Bind(R.id.name_view) TextView nameView;
        @Bind(R.id.select_checkbox) Checkable selectCheckBox;

        public TenantViewHolder(View view) {
            super(view);
        }

        public void onBind(Tenant tenant) {
            nameView.setText(tenant.getName());
            selectCheckBox.setChecked(refineState.getSelectedTenantIds().contains(tenant.getId()));
        }

        private void trackSelectedTenant(String tenantName) {
            HashMap<String, Object> contextData = new HashMap<>();
            AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();
            analyticsManager.safePut(AnalyticsManager.ContextDataKeys.ShoppingFilterStore, tenantName, contextData);
            analyticsManager.trackAction(AnalyticsManager.Actions.ShoppingFilterByStore, contextData);
        }

        @OnClick(R.id.item_view)
        public void onItemViewClick() {
            Tenant tenant = tenants.get(hasFavoriteTenants ? getAdapterPosition() - 1 : getAdapterPosition());

            selectCheckBox.toggle();
            if (selectCheckBox.isChecked()) {
                refineState.getSelectedTenantIds().add(tenant.getId());
                trackSelectedTenant(tenant.getName());
            } else {
                refineState.getSelectedTenantIds().remove(tenant.getId());
            }
        }
    }
}