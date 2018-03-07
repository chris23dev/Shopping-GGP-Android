package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.RefineTenantFilterAdapter;
import com.ggp.theclub.model.RefineState;
import com.ggp.theclub.model.SaleCategory;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.SaleCategoryUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class RefineTenantActivity extends BaseActivity {
    @Bind(R.id.search_view) EditText searchView;
    @Bind(R.id.search_results_list) CustomRecyclerView searchResultsList;
    @Bind(R.id.no_results_layout) LinearLayout noResultsLayout;
    @Bind(R.id.refine_tenant_label) TextView refineTenantLabel;
    @Bind(R.id.refine_reset_button) TextView refineResetBtn;


    public static final String REFINE_TENANT_RESULT = "REFINE_TENANT_RESULT";
    private RefineTenantFilterAdapter refineTenantFilterAdapter;
    private RefineState refineState;

    public static Intent buildIntent(Context context, RefineState refineState) {
        return buildIntent(context, RefineTenantActivity.class, refineState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refineState = getIntentExtra(RefineState.class);
        setContentView(R.layout.refine_tenant_activity);
        searchView.setHint(getString(R.string.manage_favorites_input_hint));
        refineTenantLabel.setText(getString(R.string.refine_tenant_label));
        refineResetBtn.setText(getString(R.string.refine_tenant_reset));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.refine_title);
        enableBackButton();

        mallRepository.queryForSaleCategories(saleCategories -> {
            SaleCategory saleCategory = SaleCategoryUtils.getSaleCategoryByCode(refineState.getSaleCategoryCode(), saleCategories);
            List<Tenant> tenants = SaleCategoryUtils.getTenantsFromSaleCategory(saleCategory);

            if (!tenants.isEmpty()) {
                refineTenantFilterAdapter = new RefineTenantFilterAdapter(tenants, refineState);
                searchResultsList.setAdapter(refineTenantFilterAdapter);
                onSearchTextChange();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent activityResult = new Intent();
        activityResult.putExtra(REFINE_TENANT_RESULT, refineTenantFilterAdapter != null ? refineTenantFilterAdapter.getRefineState() : refineState);
        setResult(RESULT_OK, activityResult);
        finish();
    }

    @OnClick(R.id.refine_reset_button)
    public void onRefineResetButtonClick() {
        refineTenantFilterAdapter.clearSelectedTenants();
    }

    @OnTextChanged(R.id.search_view)
    public void onSearchTextChange() {
        String searchText = searchView.getText().toString();
        if (refineTenantFilterAdapter != null) {
            refineTenantFilterAdapter.filterTenants(searchText);
            boolean hasResults = refineTenantFilterAdapter.getItemCount() > 0;
            noResultsLayout.setVisibility(hasResults ? View.GONE : View.VISIBLE);
            searchResultsList.setVisibility(hasResults ? View.VISIBLE : View.GONE);
        }
    }
}