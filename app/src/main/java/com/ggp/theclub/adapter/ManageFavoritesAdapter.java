package com.ggp.theclub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.TenantUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class ManageFavoritesAdapter extends TenantFilterAdapter {
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    public ManageFavoritesAdapter(List<Tenant> tenants) {
        super(tenants);
    }

    @Override
    public ManageFavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_favorites_item, parent, false);
        return new ManageFavoritesViewHolder(view);
    }

    public class ManageFavoritesViewHolder extends TenantFilterViewHolder {
        @Bind(R.id.tenant_name) TextView nameView;
        @Bind(R.id.favorite_on) View favoriteOnView;

        private boolean favoriteActive;

        public ManageFavoritesViewHolder(View view) {
            super(view);
        }

        public void onBind(Tenant tenant) {
            nameView.setText(tenant.getName());

            favoriteActive = TenantUtils.isFavoriteTenant(tenant, accountManager.getCurrentUser().getFavorites());
            favoriteOnView.setVisibility(favoriteActive ? View.VISIBLE : View.INVISIBLE);
        }

        @OnClick(R.id.item_view)
        public void onItemClick() {
            favoriteActive = !favoriteActive;
            trackFavorite();
            Tenant tenant = tenants.get(getAdapterPosition());
            if (favoriteActive) {
                accountManager.addFavoriteTenant(tenant);
                AnimationUtils.enterReveal(favoriteOnView);
            } else {
                accountManager.removeFavoriteTenant(tenant);
                AnimationUtils.exitReveal(favoriteOnView);
            }
        }

        private void trackFavorite() {
            Tenant tenant = tenants.get(getAdapterPosition());;
            String favoriteValue = favoriteActive ? AnalyticsManager.ContextDataValues.TenantFavorite : AnalyticsManager.ContextDataValues.TenantFavoriteUnfavorite;
            HashMap<String, Object> contextData = new HashMap<String, Object>(){{
                put(AnalyticsManager.ContextDataKeys.TenantFavorite, favoriteValue);
            }};
            analyticsManager.trackAction(AnalyticsManager.Actions.TenantFavorite, contextData, tenant.getName());
        }
    }
}
