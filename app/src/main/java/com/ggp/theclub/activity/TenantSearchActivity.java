package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.TenantSearchAdapter;
import com.ggp.theclub.event.TenantSelectEvent;
import com.ggp.theclub.model.ExcludedTenants;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnTextChanged;

public class TenantSearchActivity extends BaseActivity {
    @Bind(R.id.search_view) EditText searchView;
    @Bind(R.id.search_results) RecyclerView searchResultsList;
    @Bind(R.id.no_results) LinearLayout noResults;
    @Bind(R.id.wayfinding_no_results) TextView wayfindingNoResults;

    private TenantSearchAdapter tenantSearchAdapter;
    private TenantSelectEvent.EventType eventType;
    private List<Tenant> excludedTenantList;

    public static Intent buildIntent(Context context, TenantSelectEvent.EventType eventType, ExcludedTenants excludedTenants) {
        return buildIntent(context, TenantSearchActivity.class, eventType, excludedTenants);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventType = getIntentExtra(TenantSelectEvent.EventType.class);
        ExcludedTenants excludedTenants = getIntentExtra(ExcludedTenants.class);
        excludedTenantList = excludedTenants != null ? excludedTenants.getTenantList() : null;
        setContentView(R.layout.tenant_search_activity);
        wayfindingNoResults.setText(getString(R.string.wayfinding_no_results));
        searchView.setHint(getString(R.string.wayfinding_search_placeholder));
    }

    @Override
    protected void configureView() {
        searchResultsList.setLayoutManager(new LinearLayoutManager(this));
        mallRepository.queryForTenants((tenants) -> {
            initializeSearchList(tenants, TenantUtils.getTenantsWithExclusions(excludedTenantList, tenants));
        });

        if(searchView.requestFocus()) {
            showKeyboard(searchView);
        }
    }

    private void initializeSearchList(List<Tenant> allTenants, List<Tenant> searchableTenants) {
        tenantSearchAdapter = new TenantSearchAdapter(allTenants, searchableTenants, eventType);
        searchResultsList.setAdapter(tenantSearchAdapter);
        onSearchTextChange();
    }

    public void onEvent(TenantSelectEvent event) {
        hideKeyboard();
        onBackPressed();
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
            noResults.setVisibility(tenantSearchAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
        iconActionButton.setVisibility(StringUtils.isEmpty(searchText) ? View.INVISIBLE : View.VISIBLE);
    }
}