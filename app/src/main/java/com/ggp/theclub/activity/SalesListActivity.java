package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.FilteredSalesAdapter;
import com.ggp.theclub.model.RefineState;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.SaleCategory;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.PromotionUtils;
import com.ggp.theclub.util.SaleCategoryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.BindString;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class SalesListActivity extends BaseActivity {

    @Bind(R.id.filter_description) TextView filterDescription;
    @Bind(R.id.sales_list) RecyclerView salesListView;
    @BindString(R.string.filtered_sales_format_default) String filterDescriptionFormatDefault;
    @BindString(R.string.filtered_sales_format_stores) String filterDescriptionFormatStores;
    private String categoryCode;
    private List<Sale> salesList = new ArrayList<>();
    private FilteredSalesAdapter filteredSalesAdapter;
    private SaleCategory saleCategory = new SaleCategory();
    private RefineState refineState = new RefineState();

    public static Intent buildIntent(Context context, String categoryCode) {
        return buildIntent(context, SalesListActivity.class, categoryCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryCode = getIntentExtra(String.class);
        setContentView(R.layout.sales_list_activity);
    }

    @Override
    public void onActionButtonClick() {
        refineState.setSaleCategoryCode(categoryCode);
        startActivityForResult(RefineActivity.buildIntent(this, refineState), RequestCode.REFINE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REFINE_REQUEST_CODE) {
            refineState = (RefineState) data.getExtras().getSerializable(RefineActivity.REFINE_ACTIVITY_RESULT);
            refineSales(refineState);
        }
    }

    @Override
    protected void configureView() {
        enableBackButton();
        setTextActionButton(R.string.refine_title);
        mallRepository.queryForSaleCategories(saleCategories -> {
            saleCategory = SaleCategoryUtils.getSaleCategoryByCode(categoryCode, saleCategories);
            setupSalesList(saleCategory);
            setTitle(saleCategory.getLabel());
            AnimationUtils.enterReveal(titleView, true);
        });
    }

    private void setupSalesList(SaleCategory saleCategory) {
        resetSalesList();
        salesListView.setLayoutManager(new LinearLayoutManager(this));
        filteredSalesAdapter = new FilteredSalesAdapter(this, salesList);
        salesListView.setAdapter(filteredSalesAdapter);
        refineSales(refineState);
    }

    private void refineSales(RefineState refineState) {
        resetSalesList();
        setFilterTenants(refineState.getSelectedTenantIds());
        setSortState(refineState.getRefineSort());
        filteredSalesAdapter.setSales(salesList);
        filterDescription.setText(getFilterDescription());
    }

    private void resetSalesList() {
        salesList = saleCategory.getSales();
    }

    private void setSortState(RefineState.RefineSort refineSort) {
        switch (refineSort) {
            case DESCENDING:
                salesList = PromotionUtils.getSalesOrderedByStoreName(salesList);
                Collections.reverse(salesList);
                break;
            case ASCENDING:
                salesList = PromotionUtils.getSalesOrderedByStoreName(salesList);
                break;
            case DEFAULT:
                salesList = PromotionUtils.getSalesByEndDate(salesList);
        }
    }

    private void setFilterTenants(Set<Integer> selectedTenantIds) {
        salesList = StreamSupport.stream(salesList)
                .filter(sale -> selectedTenantIds.isEmpty() || selectedTenantIds.contains(sale.getTenantId()))
                .collect(Collectors.toList());
    }

    private String getFilterDescription() {
        if(refineState.getSelectedTenantIds().isEmpty()) {
            return String.format(filterDescriptionFormatDefault, salesList.size(), saleCategory.getLabel());
        } else {
            return String.format(filterDescriptionFormatStores, salesList.size(), refineState.getSelectedTenantIds().size());
        }
    }
}