package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.CategoriesAdapter;
import com.ggp.theclub.event.FilterUpdateEvent;
import com.ggp.theclub.model.TenantCategory;
import com.ggp.theclub.util.TenantCategoryUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.List;

import butterknife.Bind;

public class CategoriesActivity extends BaseActivity {
    @Bind(R.id.category_list) CustomRecyclerView categoryList;
    private String tenantCategoryCode;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, CategoriesActivity.class);
    }

    public static Intent buildIntent(Context context, String tenantCategoryCode) {
        return buildIntent(context, CategoriesActivity.class, tenantCategoryCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tenantCategoryCode = getIntentExtra(String.class);
        setContentView(R.layout.categories_activity);
    }

    @Override
    protected void configureView() {
        setTextActionButton(R.string.cancel_text);
        enableBackButton();

        mallRepository.queryForTenantCategories(tenantCategories -> {
            TenantCategory tenantCategory = TenantCategoryUtils.findTenantCategory(tenantCategories, tenantCategoryCode);
            if (tenantCategory != null) {
                setTitle(tenantCategory.getLabel());
                configureAdapter(tenantCategory.getChildCategories());
            } else {
                setTitle(R.string.categories_title);
                configureAdapter(tenantCategories);
            }
        });
    }

    private void configureAdapter(List<TenantCategory> tenantCategories) {
        categoryList.setAdapter(new CategoriesAdapter(this, tenantCategories));
    }

    public void onEvent(FilterUpdateEvent event) {
        setResult(RESULT_OK);
        onBackPressed();
    }

    @Override
    public void onActionButtonClick() {
        setResult(RESULT_OK);
        onBackPressed();
    }
}