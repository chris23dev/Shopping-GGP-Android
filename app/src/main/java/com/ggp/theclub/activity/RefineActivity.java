package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.RefineTenantListAdapter;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.RefineState;
import com.ggp.theclub.model.RefineState.RefineSort;
import com.ggp.theclub.model.SaleCategory;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.SaleCategoryUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ggp.theclub.activity.RefineSortActivity.REFINE_SORT_RESULT;
import static com.ggp.theclub.activity.RefineTenantActivity.REFINE_TENANT_RESULT;

public class RefineActivity extends BaseActivity {
    @BindColor(R.color.blue) int blueColor;
    @BindColor(R.color.black) int blackColor;
    @BindString(R.string.count_format) String countFormat;
    @Bind(R.id.refine_tenant_list) CustomRecyclerView refineTenantList;
    @Bind(R.id.refine_reset_button) LinearLayout refineResetButton;
    @Bind(R.id.refine_tenant_button) LinearLayout refineTenantButton;
    @Bind(R.id.refine_sort_button) LinearLayout refineSortButton;
    @Bind(R.id.refine_tenant_label) TextView refineTenantLabel;
    @Bind(R.id.refine_sort_label) TextView refineSortLabel;

    public static final String REFINE_ACTIVITY_RESULT = "REFINE_ACTIVITY_RESULT";
    private TextView refineResetView;
    private TextView refineTenantOptionView;
    private TextView refineSortOptionView;
    private RefineState refineState;

    private final HashMap<RefineSort, Integer> refineSortLookup = new HashMap<RefineSort, Integer>() {{
        put(RefineSort.DEFAULT, R.string.refine_sort_default);
        put(RefineSort.ASCENDING, R.string.refine_sort_ascending);
        put(RefineSort.DESCENDING, R.string.refine_sort_descending);
    }};

    public static Intent buildIntent(Context context, RefineState refineState) {
        return buildIntent(context, RefineActivity.class, refineState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refineState = getIntentExtra(RefineState.class);
        setContentView(R.layout.refine_activity);
        refineTenantLabel.setText(getString(R.string.refine_tenant_label));
        refineSortLabel.setText(getString(R.string.refine_sort_label));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.refine_title);
        enableBackButton();

        refineResetView = ButterKnife.findById(refineResetButton, R.id.description_view);
        refineResetView.setText(R.string.refine_tenant_reset);
        refineResetView.setTextColor(blueColor);
        refineTenantOptionView = ButterKnife.findById(refineTenantButton, R.id.description_view);
        refineSortOptionView = ButterKnife.findById(refineSortButton, R.id.description_view);

        updateRefineTenantList(refineState.isFavoritesSelected(), refineState.getSelectedTenantIds());
        setRefineSortButton(refineState.getRefineSort());
    }

    private void updateRefineTenantList(boolean favoritesSelected, Set<Integer> tenantIds) {
        if (!tenantIds.isEmpty()) {
            mallRepository.queryForSaleCategories(saleCategories -> {
                SaleCategory saleCategory = SaleCategoryUtils.getSaleCategoryByCode(refineState.getSaleCategoryCode(), saleCategories);
                List<Tenant> tenants = SaleCategoryUtils.getTenantsFromSaleCategoryWithIds(tenantIds, saleCategory);
                if (!tenants.isEmpty()) {
                    refineTenantList.setAdapter(new RefineTenantListAdapter(tenants));
                    setRefineTenantButton();
                } else {
                    resetRefineTenantButton();
                }
            });
        } else {
            resetRefineTenantButton();
        }

        refineState.setFavoritesSelected(favoritesSelected);
        refineState.setSelectedTenantIds(tenantIds);
    }

    private void setRefineTenantButton() {
        refineTenantOptionView.setText(R.string.refine_tenant_change);
        refineTenantOptionView.setTextColor(blueColor);
        refineTenantList.setVisibility(View.VISIBLE);
        refineResetButton.setVisibility(View.VISIBLE);
    }

    private void resetRefineTenantButton() {
        refineTenantOptionView.setText(R.string.refine_tenant_default);
        refineTenantOptionView.setTextColor(blackColor);
        refineTenantList.setVisibility(View.GONE);
        refineResetButton.setVisibility(View.GONE);
    }

    private void setRefineSortButton(RefineSort refineSort) {
        String refineSortName = getString(refineSortLookup.get(refineSort));
        refineState.setRefineSort(refineSort);
        refineSortOptionView.setText(refineSortName);
        trackSelectedSort(refineSortName);
    }

    private void trackSelectedSort(String refineSortName) {
        HashMap<String, Object> contextData = new HashMap<>();
        analyticsManager.safePut(AnalyticsManager.ContextDataKeys.ShoppingFilterSort, refineSortName, contextData);
        analyticsManager.trackAction(AnalyticsManager.Actions.ShoppingFilterBySort, contextData);
    }

    private void setActivityResult() {
        Intent activityResult = new Intent();
        activityResult.putExtra(REFINE_ACTIVITY_RESULT, refineState);
        setResult(RESULT_OK, activityResult);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.REFINE_TENANT_REQUEST_CODE:
                RefineState updatedRefineState = (RefineState) data.getExtras().getSerializable(REFINE_TENANT_RESULT);
                updateRefineTenantList(updatedRefineState.isFavoritesSelected(), updatedRefineState.getSelectedTenantIds());
                break;
            case RequestCode.REFINE_SORT_REQUEST_CODE:
                if (data != null) {
                    RefineSort refineSort = (RefineSort) data.getExtras().getSerializable(REFINE_SORT_RESULT);
                    setRefineSortButton(refineSort);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setActivityResult();
    }

    @OnClick(R.id.refine_reset_button)
    public void onRefineResetButtonClick() {
        updateRefineTenantList(false, new HashSet<>());
    }

    @OnClick(R.id.refine_tenant_button)
    public void onRefineTenantButtonClick() {
        startActivityForResult(RefineTenantActivity.buildIntent(this, refineState), RequestCode.REFINE_TENANT_REQUEST_CODE);
    }

    @OnClick(R.id.refine_sort_button)
    public void onRefineSortButtonClick() {
        startActivityForResult(RefineSortActivity.buildIntent(this), RequestCode.REFINE_SORT_REQUEST_CODE);
    }
}