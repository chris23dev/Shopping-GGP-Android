package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.BrandsAdapter;
import com.ggp.theclub.comparator.NameSortingComparator;
import com.ggp.theclub.event.FilterUpdateEvent;
import com.ggp.theclub.model.Brand;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.NameFilter;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnTextChanged;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class BrandsActivity extends BaseActivity {
    @Bind(R.id.search_text) EditText searchTextView;
    @Bind(R.id.top_disclaimer) TextView topDisclaimer;
    @Bind(R.id.no_brand_results) TextView noBrandResults;
    @Bind(R.id.brand_bottom_disclaimer) TextView brandBottomDisclaimer;
    @Bind(R.id.brand_list) CustomRecyclerView brandListView;
    @Bind(R.id.no_results) LinearLayout noResultsView;
    private BrandsAdapter brandsAdapter;
    private NameFilter<Brand> brandNameFilter;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, BrandsActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brands_activity);
        topDisclaimer.setText(getString(R.string.top_disclaimer));
        brandBottomDisclaimer.setText(getString(R.string.brand_bottom_disclaimer));
        searchTextView.setHint(getString(R.string.brand_search_placeholder));
        noBrandResults.setText(getString(R.string.no_brand_results));
    }

    @Override
    protected void configureView() {
        enableBackButton();
        setTitle(R.string.brands_title);
        setTextActionButton(R.string.cancel_text);

        mallRepository.queryForTenants((List<Tenant> tenants) -> {
            List<Brand> initialBrandList = buildBrandList(tenants);
            brandNameFilter = new NameFilter<>(initialBrandList, (brand) -> brand.getName());
            brandsAdapter = new BrandsAdapter();
            brandsAdapter.setBrands(initialBrandList);
            brandListView.setDataLoaded(true);
            brandListView.setAdapter(brandsAdapter);
            brandListView.addItemDecoration(new StickyHeaderDecoration(brandsAdapter));
            //in case something has been typed already
            onSearchTextChange();
        });
    }

    private List<Brand> buildBrandList (List<Tenant> tenants) {
        return StreamSupport.stream(tenants).flatMap(s -> StreamSupport.stream(s.getBrands())).distinct().
                sorted(new NameSortingComparator<>(Brand::getName)).collect(Collectors.toList());
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

    @OnTextChanged(R.id.search_text)
    public void onSearchTextChange() {
        if (brandNameFilter != null) {
            String searchText = searchTextView.getText().toString();
            List<Brand> filteredBrands = brandNameFilter.filterByName(searchText);
            brandsAdapter.setBrands(filteredBrands);
            noResultsView.setVisibility(filteredBrands.size() > 0 ? View.GONE : View.VISIBLE);
            brandListView.setVisibility(filteredBrands.size() > 0 ? View.VISIBLE : View.GONE);
            topDisclaimer.setVisibility(StringUtils.isEmpty(searchText) && filteredBrands.size() > 0 ? View.VISIBLE : View.GONE);
        }
    }
}