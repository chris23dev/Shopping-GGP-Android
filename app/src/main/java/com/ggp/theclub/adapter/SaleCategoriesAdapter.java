package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.SalesListActivity;
import com.ggp.theclub.adapter.SaleCategoriesAdapter.SaleCategoryViewHolder;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.SaleCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaleCategoriesAdapter extends RecyclerView.Adapter<SaleCategoryViewHolder> {
    private Context context;
    private List<SaleCategory> saleCategories = new ArrayList<>();
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    public SaleCategoriesAdapter(Context context, List<SaleCategory> saleCategories) {
        this.context = context;
        this.saleCategories = saleCategories;
    }

    @Override
    public int getItemCount() {
        return saleCategories.size();
    }

    @Override
    public SaleCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_category_item, parent, false);
        return new SaleCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SaleCategoryViewHolder holder, int position) {
        holder.onBind(saleCategories.get(position));
    }

    private void trackSelectedCategory(String categoryName) {
        HashMap<String, Object> contextData = new HashMap<>();
        analyticsManager.safePut(AnalyticsManager.ContextDataKeys.ShoppingSubcategoryName, categoryName, contextData);
        analyticsManager.trackAction(AnalyticsManager.Actions.ShoppingSubcategory, contextData);
    }

    public class SaleCategoryViewHolder extends RecyclerView.ViewHolder {
        @BindString(R.string.count_format) String countFormat;
        @Bind(R.id.category_description) TextView categoryDescriptionView;
        @Bind(R.id.count_view) TextView countView;

        public SaleCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(SaleCategory saleCategory) {
            categoryDescriptionView.setText(saleCategory.getLabel());
            countView.setText(String.format(countFormat, saleCategory.getSales().size()));
        }

        @OnClick(R.id.item_view)
        public void onCategoryClick() {
            SaleCategory saleCategory = saleCategories.get(getAdapterPosition());
            trackSelectedCategory(saleCategory.getLabel());
            context.startActivity(SalesListActivity.buildIntent(context, saleCategory.getCode()));
        }
    }
}