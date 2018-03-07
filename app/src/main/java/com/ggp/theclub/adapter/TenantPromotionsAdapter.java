package com.ggp.theclub.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.MallEventActivity;
import com.ggp.theclub.activity.SaleActivity;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Promotion;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Setter;

public class TenantPromotionsAdapter extends RecyclerView.Adapter {
    @Bind(R.id.promotions_header) TextView promotionHeader;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private Context context;
    @Setter private Tenant tenant;
    @Setter private List<Promotion> promotions = new ArrayList<>();
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    public TenantPromotionsAdapter(Context context, Tenant tenant, List<Promotion> promotions) {
        this.context = context;
        setTenant(tenant);
        setPromotions(promotions);
    }

    @Override
    public int getItemCount() {
        return promotions.isEmpty() ? 0 : promotions.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == TYPE_HEADER ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_HEADER ? createPromotionsHeaderViewHolder(parent) : createPromotionViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_ITEM) {
            ((PromotionViewHolder) holder).onBind(promotions.get(position - 1));
        }
    }

    protected PromotionsHeaderViewHolder createPromotionsHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_promotions_header, parent, false);
        ButterKnife.bind(this,view);
        promotionHeader.setText(context.getString(R.string.tenant_sales_and_events));
        return new PromotionsHeaderViewHolder(view);
    }

    protected PromotionViewHolder createPromotionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_promotion_item, parent, false);

        return new PromotionViewHolder(view);
    }

    public class PromotionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.promotion_logo) ImageView promotionLogo;
        @Bind(R.id.promotion_title) TextView promotionTitle;
        @Bind(R.id.promotion_date_range) TextView promotionDateRange;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void onBind(Promotion promotion) {
            ImageUtils.loadImage(promotion.getImageUrl(), promotionLogo);
            promotionTitle.setText(promotion.getTitle());
            promotionDateRange.setText(promotion.getDateRange());
        }

        @OnClick(R.id.tenant_promotion_item)
        public void onClick() {
            Promotion promotion = promotions.get(getAdapterPosition() - 1);
            trackAnalytics(promotion);
            Intent intent = promotion instanceof Sale ? SaleActivity.buildIntent(context, (Sale) promotion) : MallEventActivity.buildIntent(context, (MallEvent) promotion);
            context.startActivity(intent);
        }

        private void trackAnalytics(Promotion promotion) {
            String promotionType = promotion.getClass() == Sale.class ? AnalyticsManager.ContextDataValues.HomeViewTypeMallSales : AnalyticsManager.ContextDataValues.HomeViewTypeMallEvent;
            HashMap<String, Object> contextData = new HashMap<String, Object>(){{
                put(AnalyticsManager.ContextDataKeys.TileName, promotion.getTitle().toLowerCase());
                put(AnalyticsManager.ContextDataKeys.TileType, promotionType);
            }};
            analyticsManager.trackAction(AnalyticsManager.Actions.TenantSale, contextData, tenant.getName());
        }
    }

    public class PromotionsHeaderViewHolder extends RecyclerView.ViewHolder {
        public PromotionsHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}