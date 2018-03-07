package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.BaseActivity;
import com.ggp.theclub.activity.FiltersActivity;
import com.ggp.theclub.adapter.TenantListAdapter;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.event.FilterUpdateEvent.FilterType;
import com.ggp.theclub.event.MapReadyEvent;
import com.ggp.theclub.event.TenantsFilterUpdateEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.GGPLeaseStatusComingSoon;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.TenantCategory;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantCategoryUtils;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.CustomRecyclerView;
import com.ggp.theclub.view.FilterView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;
import de.greenrobot.event.EventBus;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import static android.view.View.GONE;

public class DirectoryListFragment extends BaseFragment {
    @Bind(R.id.filter_view) FilterView filterView;
    @Bind(R.id.search_layout) ViewGroup searchLayout;
    @Bind(R.id.search_view) EditText searchView;
    @Bind(R.id.search_clear_button) View searchClearButton;
    @Bind(R.id.no_results_layout) View noResultsLayout;
    @Bind(R.id.directory_list) CustomRecyclerView directoryList;
    @Bind(R.id.wayfinding_no_results) TextView wayfindingNoResults;
    private TenantListAdapter tenantListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFragmentVisible() {
        //favorites may have changed
        tenantListAdapter.notifyDataSetChanged();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Directory);
    }

    @Override
    public void onFragmentInvisible() {
        ((BaseActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.directory_list_fragment, container);
        searchView.setHint(R.string.directory_search_hint);
        wayfindingNoResults.setText(getString(R.string.wayfinding_no_results));
        return view;
    }

    @Override
    protected void configureView() {
        tenantListAdapter = new TenantListAdapter(getActivity(), true);
        directoryList.setAdapter(tenantListAdapter);
        updateFilteredTenants(null);
        directoryList.addItemDecoration(new StickyHeaderDecoration(tenantListAdapter));
    }

    @Override
    public void onActionButtonClick() {
        startActivity(FiltersActivity.buildIntent(getActivity()));
    }

    private void updateFilteredTenants(TenantsFilterUpdateEvent event) {
        if (event != null && event.getFilterType().equals(FilterType.CATEGORY)) {
            handleCategoryFilter(event);
        } else {
            mallRepository.queryForTenants(tenants -> {
                List<Tenant> filteredTenants = tenants;
                if (event == null) {
                    filteredTenants = tenants;
                } else if (event.getFilterType().equals(FilterType.PRODUCT_TYPE)) {
                    filteredTenants = TenantUtils.getTenantsByProductTypeCode(event.getProductTypeCode(), tenants);
                } else if (event.getFilterType().equals(FilterType.BRAND)) {
                    filteredTenants = TenantUtils.getTenantsByBrand(event.getBrand(), tenants);
                    event.setFilterCount(filteredTenants.size());
                }
                updateTenantsForEvent(filteredTenants, event);
            });
        }
    }

    private void handleCategoryFilter(TenantsFilterUpdateEvent event) {
        mallRepository.queryForTenantCategories(tenantCategories -> {
            TenantCategory tenantCategory = TenantCategoryUtils.findTenantCategory(tenantCategories, event.getCategoryCode());
            if (tenantCategory != null) {
                updateTenantsForEvent(tenantCategory.getTenants(), event);
            }
        });
    }

    private void updateTenantsForEvent(List<Tenant> tenants, TenantsFilterUpdateEvent event) {
        tenants = removeComingSoonTenants(tenants);

        tenantListAdapter.resetTenants(tenants);
        searchView.setText(null);
        directoryList.setDataLoaded(true);
        directoryList.scrollToPosition(0);
        filterView.updateView(event);
        searchLayout.setVisibility(filterView.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
        noResultsLayout.setVisibility(tenantListAdapter.isEmpty() ? View.VISIBLE : GONE);
    }

    private List<Tenant> removeComingSoonTenants(final List<Tenant> tenants) {
        return StreamSupport.stream(tenants).filter(tenant -> {
            if(tenant.getLeaseStatus() == GGPLeaseStatusComingSoon.P ||
                    tenant.getLeaseStatus() == GGPLeaseStatusComingSoon.Q) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    public void onEvent(TenantsFilterUpdateEvent event) {
        updateFilteredTenants(event);
    }

    public void onEvent(AccountLoginEvent event) {
        tenantListAdapter.notifyDataSetChanged();
    }

    public void onEvent(MapReadyEvent event) {
        if (tenantListAdapter != null) {
            tenantListAdapter.notifyDataSetChanged();
        }
    }

    @OnTextChanged(R.id.search_view)
    public void onSearchTextChanged() {
        if (tenantListAdapter != null) {
            tenantListAdapter.filterTenants(searchView.getText().toString());
            noResultsLayout.setVisibility(tenantListAdapter.isEmpty() ? View.VISIBLE : GONE);
        }
        searchClearButton.setVisibility(StringUtils.isEmpty(searchView.getText().toString()) ? GONE : View.VISIBLE);
    }

    @OnClick(R.id.search_clear_button)
    public void onClearButtonClicked() {
        searchView.setText("");
    }
}