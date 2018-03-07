package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.TenantListAdapter;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.util.CategoryUtils;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import butterknife.Bind;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class DiningFragment extends BaseFragment {
    @Bind(R.id.dining_list) CustomRecyclerView diningList;
    private TenantListAdapter tenantListAdapter;

    public static DiningFragment newInstance() {
        return new DiningFragment();
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        analyticsManager.trackScreen(AnalyticsManager.Screens.Dining);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(inflater, R.layout.dining_fragment, container);
    }

    @Override
    protected void configureView() {
        tenantListAdapter = new TenantListAdapter(getActivity());
        updateFilteredStores(CategoryUtils.CATEGORY_FOOD);
        diningList.setAdapter(tenantListAdapter);
        diningList.addItemDecoration(new StickyHeaderDecoration(tenantListAdapter));
    }

    private void updateFilteredStores(String categoryCode) {
        mallRepository.queryForTenants(tenants -> {
            tenantListAdapter.resetTenants(TenantUtils.getTenantsByCategoryCode(categoryCode, tenants));
            diningList.setDataLoaded(true);
        });
    }
}