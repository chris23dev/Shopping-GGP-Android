package com.ggp.theclub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.model.Tenant;

import java.util.List;

import butterknife.Bind;

public class RefineTenantListAdapter extends TenantFilterAdapter {
    public RefineTenantListAdapter(List<Tenant> tenants) {
        super(tenants);
    }

    @Override
    public int getItemCount() {
        return tenants.size();
    }

    @Override
    public TenantFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refine_item, parent, false);
        return new TenantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TenantFilterViewHolder holder, int position) {
        holder.onBind(tenants.get(position));
    }

    public class TenantViewHolder extends TenantFilterViewHolder {
        @Bind(R.id.description_view) TextView descriptionView;

        public TenantViewHolder(View view) {
            super(view);
        }

        public void onBind(Tenant tenant) {
            descriptionView.setText(tenant.getName());
        }
    }
}