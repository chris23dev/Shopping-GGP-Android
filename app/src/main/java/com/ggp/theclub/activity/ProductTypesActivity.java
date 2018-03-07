package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ProductTypesAdapter;
import com.ggp.theclub.event.FilterUpdateEvent;
import com.ggp.theclub.model.FilterProductType;
import com.ggp.theclub.util.ProductUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import butterknife.Bind;

public class ProductTypesActivity extends BaseActivity {
    @Bind(R.id.product_type_list) CustomRecyclerView productTypeList;
    private FilterProductType filterProductType;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, ProductTypesActivity.class);
    }

    public static Intent buildIntent(Context context, FilterProductType filterProductType) {
        return buildIntent(context, ProductTypesActivity.class, filterProductType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterProductType = getIntentExtra(FilterProductType.class);
        setContentView(R.layout.product_types_activity);
    }

    @Override
    protected void configureView() {
        enableBackButton();
        setTextActionButton(R.string.cancel_text);

        if (filterProductType != null) {
            setTitle(filterProductType.getDescription());
            configureAdapter(filterProductType);
        } else {
            setTitle(R.string.products_title);
            mallRepository.queryForTenants(tenants -> configureAdapter(ProductUtils.getTenantProductTypeTree(tenants, getString(R.string.all_stores_category_label))));
        }
    }

    private void configureAdapter(FilterProductType filterProductType) {
        if (!filterProductType.getChildren().isEmpty()) {
            productTypeList.setAdapter(new ProductTypesAdapter(this, filterProductType));
        }
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