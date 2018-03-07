package com.ggp.theclub.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.model.FilterProductType;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import java8.util.stream.StreamSupport;
import lombok.Getter;

public class TenantProductTypesAdapter extends RecyclerView.Adapter {
    private enum ProductTypeLevel {MAIN_TYPE, SUB_TYPE}
    private List<ProductTypeListItem> productTypeListItems = new ArrayList<>();

    public TenantProductTypesAdapter(List<FilterProductType> filterProductTypes) {
        StreamSupport.stream(filterProductTypes).forEach(productType -> {
            productTypeListItems.add(new ProductTypeListItem(ProductTypeLevel.MAIN_TYPE, productType.getDescription()));
            StreamSupport.stream(productType.getChildren()).forEach(productSubType ->
                    productTypeListItems.add(new ProductTypeListItem(ProductTypeLevel.SUB_TYPE, productSubType.getDescription())));
        });
    }

    @Override
    public int getItemCount() {
        return productTypeListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return productTypeListItems.get(position).getLevel().ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId =  viewType == ProductTypeLevel.MAIN_TYPE.ordinal() ? R.layout.tenant_product_type_list_main : R.layout.tenant_product_type_list_sub;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ProductTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ProductTypeViewHolder) holder).onBind(productTypeListItems.get(position).getDescription());
    }

    public class ProductTypeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.product_type_description) TextView descriptionView;

        public ProductTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void onBind(String description) {
            if (description == null) {
                itemView.setVisibility(View.GONE);
            } else {
                descriptionView.setText(description);
            }
        }
    }

    private class ProductTypeListItem {
        @Getter ProductTypeLevel level;
        @Getter String description;

        public ProductTypeListItem(ProductTypeLevel level, String description) {
            this.level = level;
            this.description = description;
        }
    }
}