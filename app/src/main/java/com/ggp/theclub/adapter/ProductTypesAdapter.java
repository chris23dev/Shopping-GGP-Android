package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.ProductTypesActivity;
import com.ggp.theclub.activity.RequestCode;
import com.ggp.theclub.adapter.ProductTypesAdapter.ProductTypeViewHolder;
import com.ggp.theclub.event.FilterUpdateEvent.FilterType;
import com.ggp.theclub.event.TenantsFilterUpdateEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.FilterProductType;
import com.ggp.theclub.util.ProductUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import java8.util.Optional;

public class ProductTypesAdapter extends RecyclerView.Adapter<ProductTypeViewHolder> {
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();
    private Context context;
    private FilterProductType parentFilterProductType;
    private List<FilterProductType> filterProductTypes;

    public ProductTypesAdapter(Context context, FilterProductType filterProductType) {
        this.context = context;
        this.filterProductTypes = filterProductType.getChildren();
        parentFilterProductType = filterProductType;
    }

    @Override
    public int getItemCount() {
        return filterProductTypes.size();
    }

    @Override
    public ProductTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        return new ProductTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductTypeViewHolder holder, int position) {
        holder.onBind(filterProductTypes.get(position));
    }

    private void trackSelectedProductType(String productType) {
        HashMap<String, Object> contextData = new HashMap<>();
        contextData.put(AnalyticsManager.ContextDataKeys.DirectoryFilterProductType, productType);
        analyticsManager.trackAction(AnalyticsManager.Actions.DirectoryFilterCategory, contextData);
    }

    public class ProductTypeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name_view) TextView nameView;
        @Bind(R.id.arrow_view) TextView arrowView;

        public ProductTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(FilterProductType filterProductType) {
            nameView.setText(filterProductType.getDescription());
            arrowView.setVisibility(hasChildren(filterProductType) ? View.VISIBLE : View.INVISIBLE);
        }

        private boolean hasChildren(FilterProductType filterProductType) {
            return !filterProductType.getChildren().isEmpty();
        }

        @OnClick(R.id.item_view)
        public void onProductTypeClick() {
            FilterProductType filterProductType = filterProductTypes.get(getAdapterPosition());
            if (hasChildren(filterProductType)) {
                ((ProductTypesActivity) context).startActivityForResult(ProductTypesActivity.buildIntent(context, filterProductType), RequestCode.FINISH_REQUEST_CODE);
            } else {
                String filterLabel = ProductUtils.getProductTypeFilterLabel(Optional.ofNullable(parentFilterProductType), filterProductType);
                EventBus.getDefault().post(new TenantsFilterUpdateEvent(filterProductType.getCode(), filterProductType.getCount(), FilterType.PRODUCT_TYPE, filterLabel));
                trackSelectedProductType(filterLabel);
            }
        }
    }
}
