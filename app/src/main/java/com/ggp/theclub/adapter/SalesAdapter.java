package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.SaleActivity;
import com.ggp.theclub.adapter.SalesAdapter.SaleViewHolder;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.view.FastScroller.FastScrollerListener;
import com.ggp.theclub.view.ListItemHeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class SalesAdapter extends RecyclerView.Adapter<SaleViewHolder> implements StickyHeaderAdapter<ListItemHeaderViewHolder>, FastScrollerListener {
    private Context context;
    private List<Sale> filteredSales = new ArrayList<>();

    public SalesAdapter(Context context) {
        this.context = context;
    }

    public void setFilteredSales(List<Sale> sales) {
        filteredSales.clear();
        filteredSales.addAll(sales);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredSales.size();
    }

    @Override
    public SaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_item, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SaleViewHolder holder, int position) {
        holder.onBind(filteredSales.get(position));
    }

    @Override
    public long getHeaderId(int position) {
        Tenant tenant = filteredSales.get(position).getTenant();
        if (tenant != null && !StringUtils.isEmpty(filteredSales.get(position).getTenant().getName())) {
            return filteredSales.get(position).getTenant().getName().toUpperCase().charAt(0);
        }
        return 0;
    }

    @Override
    public ListItemHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
        return new ListItemHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(ListItemHeaderViewHolder holder, int position) {
        holder.onBind(String.valueOf((char) getHeaderId(position)).toUpperCase());
    }

    @Override
    public String getFastScrollerBubbleText(int position) {
        return String.valueOf((char) getHeaderId(position)).toUpperCase();
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_logo) ImageView logoView;
        @Bind(R.id.text_logo) TextView logoNameView;
        @Bind(R.id.sale_name) TextView saleNameTextView;
        @Bind(R.id.tenant_name) TextView tenantNameTextView;
        @Bind(R.id.sale_date) TextView dateTextView;

        public SaleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBind(Sale sale) {
            if (sale.getTenant() != null) {
                ImageUtils.setLogo(logoView, logoNameView, sale.getTenant().getLogoUrl(), sale.getTenant().getName());
                tenantNameTextView.setText(sale.getTenant().getName());
            }
            saleNameTextView.setText(sale.getTitle());
            dateTextView.setText(sale.getDateRange());
        }

        @OnClick(R.id.sale_item)
        public void onClick() {
            context.startActivity(SaleActivity.buildIntent(context, filteredSales.get(getAdapterPosition())));
        }
    }
}