package com.ggp.theclub.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.TenantActivity;
import com.ggp.theclub.activity.WayfindActivity;
import com.ggp.theclub.adapter.HomeFeedNewTenantsAdapter.NewTenantsViewHolder;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.GGPLeaseStatusComingSoon;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFeedNewTenantsAdapter extends RecyclerView.Adapter<NewTenantsViewHolder> {
    private final String mNowOpenPrefix;
    private Activity activity;
    private MapManager mapManager = MapManager.getInstance();
    private List<Tenant> tenants = new ArrayList<>();
    private int proportionedWidth, proportionedHeight;

    public HomeFeedNewTenantsAdapter(Activity activity, List<Tenant> tenants) {
        this.activity = activity;
        this.tenants = tenants;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        proportionedWidth = (int) (displayMetrics.widthPixels * 0.75);
        proportionedHeight = proportionedWidth  / 2;

        mNowOpenPrefix = activity.getString(R.string.now_open_tenant_time);
    }

    @Override
    public int getItemCount() {
        return tenants.size();
    }

    @Override
    public NewTenantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_feed_new_tenant_item, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = proportionedWidth;
        view.setLayoutParams(layoutParams);
        return new NewTenantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewTenantsViewHolder holder, int position) {
        holder.onBind(tenants.get(position));
    }

    public class NewTenantsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.card_view) CardView cardView;
        @Bind(R.id.image_logo) ImageView imageLogoView;
        @Bind(R.id.text_logo) TextView textLogoView;
        @Bind(R.id.name_view) TextView nameView;
        @Bind(R.id.coming_soon_view) TextView comingSoon;
        @Bind(R.id.wayfind_button) View wayfindButton;

        public NewTenantsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
            layoutParams.width = proportionedWidth;
            layoutParams.height = proportionedHeight;
            cardView.setLayoutParams(layoutParams);
        }

        public void onBind(Tenant tenant) {
            ImageUtils.setLogo(imageLogoView, textLogoView, tenant.getLogoUrl(), tenant.getName());
            nameView.setText(tenant.getName());

            //try to set locations, these may not yet be available
            comingSoon.setText(generateComingSoonLabel(tenant));

            wayfindButton.setVisibility(mapManager.isDestinationWayfindingEnabled(tenant) ? View.VISIBLE : View.GONE);
        }

        @NonNull
        private String generateComingSoonLabel(Tenant tenant) {
            String comingSoonLabel;
            if(tenant.getLeaseStatus() == GGPLeaseStatusComingSoon.O){
                comingSoonLabel = mNowOpenPrefix;
            } else {
                comingSoonLabel = tenant.getComingSoonLabel();
            }

            return comingSoonLabel;
        }

        private void trackWayfind(String tenantName) {
            MallApplication.getApp().getAnalyticsManager().trackAction(AnalyticsManager.Actions.TenantWayfinding, null, tenantName.toLowerCase());
        }

        @OnClick(R.id.item_view)
        public void onStoreClick() {
            Tenant tenant = tenants.get(getAdapterPosition());
            activity.startActivity(TenantActivity.buildIntent(activity, tenant));
        }

        @OnClick(R.id.wayfind_button)
        public void onWayfindButtonClick() {
            Tenant tenant = tenants.get(getAdapterPosition());
            trackWayfind(tenant.getName());
            activity.startActivity(WayfindActivity.buildIntent(activity, tenant));
        }
    }
}