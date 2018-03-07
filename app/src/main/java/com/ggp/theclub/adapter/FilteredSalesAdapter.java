package com.ggp.theclub.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.SaleActivity;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilteredSalesAdapter extends RecyclerView.Adapter<FilteredSalesAdapter.SaleViewHolder> {
    private Context context;
    private List<Sale> sales = new ArrayList<>();

    public FilteredSalesAdapter(Context context, List<Sale> sales) {
        this.context = context;
        setSales(sales);
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    @Override
    public SaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createPromotionViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(SaleViewHolder holder, int position) {
        holder.onBind(sales.get(position));
    }

    protected SaleViewHolder createPromotionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filtered_sale_item, parent, false);
        return new SaleViewHolder(view);
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.promotion_logo) ImageView promotionLogo;
        @Bind(R.id.store_name) TextView storeName;
        @Bind(R.id.promotion_title) TextView promotionTitle;
        @Bind(R.id.promotion_date_range) TextView promotionDateRange;

        public SaleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void onBind(Sale sale) {
            ImageUtils.loadImage(sale.getImageUrl(), promotionLogo);
            Tenant tenant = sale.getTenant();
            storeName.setText(tenant != null ? tenant.getName() : null);
            promotionTitle.setText(sale.getTitle());
            promotionDateRange.setText(sale.getDateRange());
        }

        @OnClick(R.id.item_view)
        public void onClick() {
            Sale sale = sales.get(getAdapterPosition());
            Intent intent = SaleActivity.buildIntent(context, sale);
            context.startActivity(intent);
        }
    }
}