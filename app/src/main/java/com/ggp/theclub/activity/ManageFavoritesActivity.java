package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.ManageFavoritesAdapter;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import butterknife.Bind;
import butterknife.OnTextChanged;

public class ManageFavoritesActivity extends BaseActivity {
    @Bind(R.id.search_view) EditText searchView;
    @Bind(R.id.search_results) CustomRecyclerView searchResultsList;
    @Bind(R.id.no_results) LinearLayout noResults;
    @Bind(R.id.wayfinding_no_results) TextView wayfindingNoResults;
    private ManageFavoritesAdapter tenantSearchAdapter;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, ManageFavoritesActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_favorites_activity);
        searchView.setHint(R.string.manage_favorites_input_hint);
        wayfindingNoResults.setText(getString(R.string.wayfinding_no_results));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.manage_favorites_title);
        enableBackButton();

        mallRepository.queryForTenants((stores) -> {
            stores = TenantUtils.filterByDistinctProperty(stores, Tenant::getPlaceWiseRetailerId);
            tenantSearchAdapter = new ManageFavoritesAdapter(stores);
            searchResultsList.setAdapter(tenantSearchAdapter);
            onSearchTextChange();
        });
    }

    @Override
    public void onActionButtonClick() {
        searchView.getEditableText().clear();
    }

    @OnTextChanged(R.id.search_view)
    public void onSearchTextChange() {
        String searchText = searchView.getText().toString();
        if (tenantSearchAdapter != null) {
            tenantSearchAdapter.filterTenants(searchText);
        }
        boolean hasResults = tenantSearchAdapter.getItemCount() > 0;
        noResults.setVisibility(hasResults ? View.GONE : View.VISIBLE);
        searchResultsList.setVisibility(hasResults ? View.VISIBLE : View.GONE);
    }

}