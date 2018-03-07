package com.ggp.theclub.fragment;

import android.view.View;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.MallSearchSelectActivity;
import com.ggp.theclub.comparator.NameSortingComparator;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.NameFilter;
import com.ggp.theclub.util.StringUtils;

import java.util.List;

import butterknife.OnClick;
import butterknife.OnTextChanged;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class MallSearchByNameFragment extends MallSearchFragment{

    private boolean hasSearchedByName = false;
    private NameFilter<Mall> nameFilter = null;

    public static MallSearchByNameFragment newInstance(SearchScreenStyle searchScreenStyle) {
        MallSearchByNameFragment fragment = new MallSearchByNameFragment();
        fragment.searchScreenStyle = searchScreenStyle;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchSearchableMalls();
    }

    @Override
    protected void configureView() {
        super.configureView();

        titleView.setText(R.string.mall_search_by_name_title);
        backButton.setVisibility(View.VISIBLE);
        mallSelectInputView.setHint(R.string.search_by_name_hint);
        noResultsMessageView.setText(R.string.mall_select_name_error);
        noResultsButton.setText(R.string.search_by_name_no_results_button);
    }

    private void searchMallsByName(String searchText) {
        List<Mall> malls = nameFilter.filterByName(searchText);
        noResultsLayout.setVisibility(malls.size() == 0 ? View.VISIBLE : View.GONE);
        mallsAdapter.setMalls(malls, false);
    }

    /**
     * fetch malls used in type-ahead search by name
     */
    private void fetchSearchableMalls() {
        mallRepository.queryForSimpleMalls(malls -> {
            if (!malls.isEmpty()) {
                List<Mall> filteredList = StreamSupport.stream(getNonDispositionedMalls(malls))
                        .sorted(new NameSortingComparator<>(Mall::getName)).collect(Collectors.toList());
                nameFilter = new NameFilter<>(filteredList, (mall) -> mall.getName());
                //in case the search field is already populated, call onTextChanged
                onTextChanged();
            }
        });
    }

    @Override
    protected boolean isLocationSearch() {
        return false;
    }

    @OnTextChanged(R.id.mall_select_input)
    public void onTextChanged() {
        if (!StringUtils.isEmpty(mallSelectInputView.getText().toString())) {
            AnimationUtils.enterReveal(clearSearchButton);
        } else {
            AnimationUtils.exitReveal(clearSearchButton);
        }
        //Only do a type-ahead search if mall list has already been populated
        if(nameFilter != null) {
            if (!hasSearchedByName) {
                hasSearchedByName = true;
                analyticsManager.trackAction(AnalyticsManager.Actions.SearchByMallName);
            }
            searchMallsByName(mallSelectInputView.getText().toString());
        }
    }

    @OnClick(R.id.clear_search_button)
    public void onClearText() {
        mallSelectInputView.setText("");
        AnimationUtils.exitReveal(clearSearchButton);
        searchResultsHeader.setVisibility(View.GONE);
    }

    @OnClick(R.id.no_results_button)
    public void onNoResultsButtonClick() {
        getActivity().setResult(MallSearchSelectActivity.RESULT_SEARCH_BY_LOCATION);
        getActivity().finish();
    }
}
