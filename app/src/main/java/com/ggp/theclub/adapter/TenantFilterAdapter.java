package com.ggp.theclub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ggp.theclub.comparator.TenantNameComparator;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.NameFilter;
import com.ggp.theclub.util.TenantUtils;

import java.util.List;

import butterknife.ButterKnife;

public abstract class TenantFilterAdapter extends RecyclerView.Adapter<TenantFilterAdapter.TenantFilterViewHolder> {
    private NameFilter<Tenant> nameFilter;
    protected List<Tenant> tenants;

    public TenantFilterAdapter(List<Tenant> tenants) {
        this.tenants = sortTenants(tenants);
        nameFilter = new NameFilter<>(this.tenants, tenant -> tenant.getName());
    }

    /**
     * Resets the tenant list, also resets any name filtering.
     */
    public void resetTenants(List<Tenant> newTenants) {
        tenants = sortTenants(newTenants);
        nameFilter = new NameFilter<>(tenants, tenant -> tenant.getName());
        notifyDataSetChanged();
    }

    public void filterTenants(String searchString) {
        tenants = nameFilter.filterByName(searchString);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return tenants.isEmpty();
    }

    @Override
    public int getItemCount() {
        return tenants.size();
    }

    @Override
    public void onBindViewHolder(TenantFilterViewHolder holder, int position) {
        holder.onBind(tenants.get(position));
    }

    public abstract class TenantFilterViewHolder extends RecyclerView.ViewHolder {
        public TenantFilterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public abstract void onBind(Tenant tenant);
    }

    private List<Tenant> sortTenants(List<Tenant> newTenants) {
        return TenantUtils.getSortedTenants(newTenants, TenantNameComparator.INSTANCE);
    }

}
