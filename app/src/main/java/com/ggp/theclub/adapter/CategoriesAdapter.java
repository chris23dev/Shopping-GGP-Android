package com.ggp.theclub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.CategoriesActivity;
import com.ggp.theclub.activity.RequestCode;
import com.ggp.theclub.adapter.CategoriesAdapter.CategoryViewHolder;
import com.ggp.theclub.event.FilterUpdateEvent.FilterType;
import com.ggp.theclub.event.TenantsFilterUpdateEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.TenantCategory;
import com.ggp.theclub.util.CategoryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private Context context;
    private List<TenantCategory> tenantCategories = new ArrayList<>();
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    public CategoriesAdapter(Context context, List<TenantCategory> tenantCategories) {
        this.context = context;
        this.tenantCategories = tenantCategories;
    }

    @Override
    public int getItemCount() {
        return tenantCategories.size();
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.onBind(tenantCategories.get(position));
    }

    private void trackSelectedCategory(String category) {
        HashMap<String, Object> contextData = new HashMap<>();
        contextData.put(AnalyticsManager.ContextDataKeys.DirectoryFilterCategory, category);
        analyticsManager.trackAction(AnalyticsManager.Actions.DirectoryFilterCategory, contextData);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        @BindString(R.string.count_format) String countFormat;
        @Bind(R.id.name_view) TextView nameView;
        @Bind(R.id.count_view) TextView countView;
        @Bind(R.id.arrow_view) TextView arrowView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(TenantCategory tenantCategory) {
            nameView.setText(tenantCategory.getLabel());
            countView.setText(String.format(countFormat, tenantCategory.getTenants().size()));
            arrowView.setVisibility(tenantCategory.getChildCategories().size() > 0 ? View.VISIBLE : View.INVISIBLE);
        }

        @OnClick(R.id.item_view)
        public void onCategoryClick() {
            TenantCategory tenantCategory = tenantCategories.get(getAdapterPosition());
            if (tenantCategory.getChildCategories().size() > 0) {
                ((CategoriesActivity) context).startActivityForResult(CategoriesActivity.buildIntent(context, tenantCategory.getCode()), RequestCode.FINISH_REQUEST_CODE);
            } else {
                if (CategoryUtils.CATEGORY_MY_FAVORITES.equals(tenantCategory.getCode())) {
                    MallApplication.getApp().getFeedbackManager().incrementFeedbackEventCount((CategoriesActivity) context);
                }
                EventBus.getDefault().post(new TenantsFilterUpdateEvent(tenantCategory.getCode(), FilterType.CATEGORY, tenantCategory.getLabel()));
                trackSelectedCategory(tenantCategory.getLabel().toLowerCase());
            }
        }
    }
}