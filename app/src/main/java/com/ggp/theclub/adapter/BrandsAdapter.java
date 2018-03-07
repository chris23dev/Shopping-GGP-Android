package com.ggp.theclub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.event.TenantsFilterUpdateEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.Brand;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.view.FastScroller.FastScrollerListener;
import com.ggp.theclub.view.ListItemHeaderViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import de.greenrobot.event.EventBus;

/**
 * In addition to the brands, this adapter has one extra element,
 * a bottom disclaimer which is always present.
 */
public class BrandsAdapter extends RecyclerView.Adapter implements StickyHeaderAdapter<ListItemHeaderViewHolder>, FastScrollerListener {
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    private List<Brand> brands = new ArrayList<>();

    private int BOTTOM_DISCLAIMER_VIEW_TYPE = 0;
    private int BRAND_VIEW_TYPE = 1;

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return brands.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == BOTTOM_DISCLAIMER_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_bottom_disclaimer, parent, false);
            return new BottomDisclaimerViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_item, parent, false);
            return new BrandViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == BRAND_VIEW_TYPE) {
            ((BrandViewHolder) holder).onBind();
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position == brands.size()) {
            //this is the bottom disclaimer Return last id of brands so we don't make a new header
            return brands.size() == 0 ? 0 : getHeaderId(brands.get(brands.size() - 1));
        } else {
            return StringUtils.getNameForSorting(brands.get(position).getName()).charAt(0);
        }
    }

    private char getHeaderId(Brand brand) {
        if (StringUtils.isEmpty(brand.getName())) {
            return 0;
        }
        return StringUtils.getNameForSorting(brand.getName()).charAt(0);
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

    @Override
    public int getItemViewType(int position) {
        if (position == brands.size()) {
            return BOTTOM_DISCLAIMER_VIEW_TYPE;
        } else {
            return BRAND_VIEW_TYPE;
        }
    }

    private void trackSelectedBrand(String brand) {
        HashMap<String, Object> contextData = new HashMap<>();
        contextData.put(AnalyticsManager.ContextDataKeys.DirectoryFilterBrand, brand);
        analyticsManager.trackAction(AnalyticsManager.Actions.DirectoryFilterCategory, contextData);
    }

    public class BrandViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.brand_name) TextView brandName;

        public BrandViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBind() {
            brandName.setText(brands.get(getAdapterPosition()).getName());
        }

        @OnClick(R.id.item_view)
        public void onClick() {
            Brand brand = brands.get(getAdapterPosition());
            if (brand != null) {
                EventBus.getDefault().post(new TenantsFilterUpdateEvent(brand));
                trackSelectedBrand(brand.getName());
            }
        }
    }

    private class BottomDisclaimerViewHolder extends RecyclerView.ViewHolder {
        public BottomDisclaimerViewHolder(View view) {
            super(view);
        }
    }
}