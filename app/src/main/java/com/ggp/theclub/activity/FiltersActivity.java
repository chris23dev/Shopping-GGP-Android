package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FiltersActivity extends BaseActivity {
    @Bind(R.id.categories_button) LinearLayout categoriesButton;
    @Bind(R.id.products_button) LinearLayout productsButton;
    @Bind(R.id.brands_button) LinearLayout brandsButton;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, FiltersActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters_activity);
    }

    @Override
    protected void configureView() {
        setTitle(R.string.filter_title);
        setTextActionButton(R.string.cancel_text);

        ((TextView) ButterKnife.findById(categoriesButton, R.id.name_view)).setText(R.string.categories_title);
        ((TextView) ButterKnife.findById(productsButton, R.id.name_view)).setText(R.string.products_title);
        ((TextView) ButterKnife.findById(brandsButton, R.id.name_view)).setText(R.string.brands_title);

        if (!mallManager.getMall().getMallConfig().isProductEnabled()) {
            productsButton.setVisibility(View.GONE);
            brandsButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActionButtonClick() {
        onBackPressed();
    }

    @OnClick(R.id.categories_button)
    public void onCategoriesButtonClick() {
        startActivityForResult(CategoriesActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }

    @OnClick(R.id.products_button)
    public void onProductsButtonClick() {
        startActivityForResult(ProductTypesActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }

    @OnClick(R.id.brands_button)
    public void onBrandsButtonClick() {
        startActivityForResult(BrandsActivity.buildIntent(this), RequestCode.FINISH_REQUEST_CODE);
    }
}