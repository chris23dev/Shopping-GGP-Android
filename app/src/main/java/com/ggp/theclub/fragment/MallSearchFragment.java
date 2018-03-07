package com.ggp.theclub.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.BaseActivity;
import com.ggp.theclub.adapter.MallsAdapter;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.Mall;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public abstract class MallSearchFragment extends BaseFragment {
    protected String LOG_TAG = getClass().getSimpleName();
    @BindColor(R.color.white) int white;
    @BindColor(R.color.medium_gray) int mediumGray;
    @BindColor(R.color.black) int black;
    @BindColor(R.color.blue) int blue;

    @Nullable @Bind(R.id.toolbar) View toolBar;
    @Nullable @Bind(R.id.back_button) TextView backButton;
    @Nullable @Bind(R.id.title_view) TextView titleView;

    @Bind(R.id.recent_malls_header) LinearLayout recentMallsHeader;
    @Bind(R.id.recent_malls_list) RecyclerView recentMallsList;
    @Bind(R.id.recent_malls_header_text) TextView recentMallsHeadeerTitle;
    @Bind(R.id.magnifying_glass_icon) TextView magnifyingGlassIcon;
    @Bind(R.id.mall_select_input) EditText mallSelectInputView;
    @Bind(R.id.clear_search_button) TextView clearSearchButton;
    @Bind(R.id.no_mall_search_results) View noResultsLayout;
    @Bind(R.id.no_results_message) TextView noResultsMessageView;
    @Bind(R.id.no_results_button) TextView noResultsButton;
    @Bind(R.id.search_result_header) LinearLayout searchResultsHeader;
    @Bind(R.id.search_result_header_text) TextView searchResultsHeaderText;
    @Bind(R.id.mall_results_list) RecyclerView mallSearchResultsList;
//    @Bind(R.id.move_marker_instructions) TextView moveMarkerInstructions;

    protected SearchScreenStyle searchScreenStyle;
    protected MallsAdapter mallsAdapter;

    public enum SearchScreenStyle {ONBOARDING, CHANGE_MALL}
    protected abstract boolean isLocationSearch();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.mall_search_fragment, container);
        recentMallsHeadeerTitle.setText(getString(R.string.recent_malls_header_text));
//        moveMarkerInstructions.setText(getString(R.string.parking_reminder_move_marker_hint));
        noResultsMessageView.setText(getString(R.string.mall_select_name_error));
        noResultsButton.setText(getString(R.string.mall_select_name_error));
        return view;
    }

    @Override
    protected void configureView() {
        if (searchScreenStyle == SearchScreenStyle.CHANGE_MALL) {
            styleChangeMall();
        } else {
            styleOnboarding();
        }

        if(mallManager.getRecentMallsList().size() > 0) {
            recentMallsHeader.setVisibility(View.VISIBLE);
            MallsAdapter recentMallsAdapter = new MallsAdapter((BaseActivity) getActivity(), searchScreenStyle);
            recentMallsAdapter.setMalls(mallManager.getRecentMallsList(), true);
            recentMallsList.setLayoutManager(new LinearLayoutManager(getActivity()));
            recentMallsList.setNestedScrollingEnabled(false);
            recentMallsList.setAdapter(recentMallsAdapter);
        }

        mallsAdapter = new MallsAdapter((BaseActivity) getActivity(), searchScreenStyle);

        mallsAdapter.setLocationSearch(isLocationSearch());
        mallSearchResultsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mallSearchResultsList.setNestedScrollingEnabled(false);
        mallSearchResultsList.setAdapter(mallsAdapter);
    }

    private void styleChangeMall() {
        toolBar.setVisibility(View.GONE);
        mallSelectInputView.setTextColor(black);
        mallSelectInputView.setHintTextColor(mediumGray);
        mallSelectInputView.getBackground().mutate().setColorFilter(blue, PorterDuff.Mode.SRC_IN);
        mallSelectInputView.setHighlightColor(blue);
        mallSelectInputView.setHintTextColor(mediumGray);
        noResultsMessageView.setTextColor(mediumGray);
        magnifyingGlassIcon.setTextColor(mediumGray);
        clearSearchButton.setTextColor(mediumGray);
        searchResultsHeaderText.setTextColor(black);
        noResultsButton.setVisibility(View.GONE);
    }

    private void styleOnboarding() {
        mallSelectInputView.getBackground().mutate().setColorFilter(white, PorterDuff.Mode.SRC_IN);
    }

    protected List<Mall> getNonDispositionedMalls(List<Mall> malls){
        return StreamSupport.stream(malls).filter(mall -> mall.getStatus() != Mall.MallStatus.DISPOSITIONED).collect(Collectors.toList());
    }

    @Nullable
    @OnClick(R.id.back_button)
    public void onBackButtonPress(){
        getActivity().finish();
    }

}