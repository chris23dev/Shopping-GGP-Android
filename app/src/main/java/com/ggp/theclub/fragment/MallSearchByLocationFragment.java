package com.ggp.theclub.fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.BaseActivity;
import com.ggp.theclub.activity.MallSearchSelectActivity;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.event.LocationChangeEvent;
import com.ggp.theclub.event.MapSearchResultEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.LocationServicesManager;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.LocationUtils;
import com.ggp.theclub.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MallSearchByLocationFragment extends MallSearchFragment {

    @BindString(R.string.mall_select_header_text) String mallSearchHeaderText;
    @BindString(R.string.mall_select_header_near) String mallSearchHeaderNearText;
    @BindString(R.string.mall_select_header_you) String mallSearchHeaderYouText;
    @BindString(R.string.mall_select_location_error) String mallLocationErrorText;

    protected Location savedLocation;
    private boolean userHasSearched;
    protected boolean clearTextAfterLocationSearch = false;
    private Location gpsLocation;

    private final String COORDINATES_FORMAT = "%.1f:%.1f";

    public static MallSearchByLocationFragment newInstance(SearchScreenStyle searchScreenStyle) {
        MallSearchByLocationFragment fragment = new MallSearchByLocationFragment();
        fragment.searchScreenStyle = searchScreenStyle;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void configureView() {
        super.configureView();

        titleView.setText(R.string.mall_search_by_location_title);
        backButton.setVisibility(View.VISIBLE);
        mallSelectInputView.setHint(R.string.search_by_location_hint);
        noResultsMessageView.setText(R.string.search_by_location_no_results);
        noResultsButton.setText(R.string.search_by_location_no_results_button);
        LocationServicesManager.getInstance().updateLocation(getActivity());
    }


    private void updateSearchResultsHeader(String searchText) {
        searchResultsHeaderText.setText(String.format(mallSearchHeaderText, mallSearchHeaderNearText, searchText).toUpperCase());
    }

    protected void searchByLocation() {
        noResultsLayout.setVisibility(View.GONE);
        if (savedLocation != null) {
            updateSearchResultsHeader(mallSearchHeaderYouText);
            searchMallsByCoordinates(savedLocation.getLatitude(), savedLocation.getLongitude());
        }
    }

    private void searchMallsByCoordinates(double latitude, double longitude) {
        String search = String.format(COORDINATES_FORMAT, latitude, longitude);
        searchMallsByCoordinates(latitude, longitude, search);
    }

    private void searchMallsByCoordinates(double latitude, double longitude, String search) {
        MallApiClient.getInstance().getMallApi().getMallsByLocation(latitude, longitude).enqueue(new Callback<List<Mall>>() {
            @Override
            public void onResponse(Call<List<Mall>> call, Response<List<Mall>> response) {
                if (response.isSuccessful()) {
                    List<Mall> responseList = response.body();
                    responseList = getNonDispositionedMalls(responseList);
                    Collections.sort(responseList);
                    mallsAdapter.setMalls(responseList, true);
                    int searchResultsCount = responseList.size();
                    onLocationSearchResult(searchResultsCount > 0);
                    trackLocationSearch(searchResultsCount, search);
                } else {
                    Log.e(LOG_TAG, response.message());
                    mallsAdapter.clearMalls();
                }

            }

            @Override
            public void onFailure(Call<List<Mall>> call, Throwable t) {
                Log.w(LOG_TAG, t);
            }
        });
    }

    @Override
    protected boolean isLocationSearch() {
        return true;
    }

    public void onEvent(LocationChangeEvent event) {
        mallSelectInputView.setHint(R.string.search_by_gps_hint);
        gpsLocation = event.getLocation();
        if (!userHasSearched) {
            savedLocation = event.getLocation();
            searchByLocation();
        }
    }

    private void onLocationSearchResult(boolean hasResults) {
        if(hasResults) {
            searchResultsHeader.setVisibility(View.VISIBLE);
        } else {
            searchResultsHeader.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void searchMallsByLocation(String searchText) {
        LocationUtils.getMapsLocation(searchText);
    }

    public void onEvent(MapSearchResultEvent event) {
        if(event.getAddress() != null &&
                event.getAddress().getGeometry() != null &&
                event.getAddress().getGeometry().getLocation() != null) {
            searchMallsByCoordinates(event.getAddress().getGeometry().getLocation().getLat(),
                    event.getAddress().getGeometry().getLocation().getLng(), event.getSearch());
        } else {
            onLocationSearchResult(false);
            mallsAdapter.clearMalls();
        }
    }

    @OnTextChanged(R.id.mall_select_input)
    public void onTextChanged() {
        if (!StringUtils.isEmpty(mallSelectInputView.getText().toString())) {
            AnimationUtils.enterReveal(clearSearchButton);
        }
    }

    @OnEditorAction(R.id.mall_select_input)
    public boolean onEditorAction(int id){
        mallSelectInputView.setHint(R.string.search_by_location_hint);
        if (id == EditorInfo.IME_ACTION_DONE) {
            ((BaseActivity) getActivity()).hideKeyboard();
            String searchText = mallSelectInputView.getText().toString();
            if (!StringUtils.isEmpty(searchText)) {
                userHasSearched = true;
                if (clearTextAfterLocationSearch) {
                    mallSelectInputView.setText("");
                }
                mallSelectInputView.clearFocus();
                updateSearchResultsHeader(searchText);
                searchMallsByLocation(searchText);
            }
        }
        return false;
    }

    @OnClick(R.id.clear_search_button)
    public void onClearText() {
        AnimationUtils.exitReveal(clearSearchButton);
        mallSelectInputView.setText("");
        if (gpsLocation != null) {
            savedLocation = gpsLocation;
            searchByLocation();
            mallSelectInputView.setHint(R.string.search_by_gps_hint);
        } else {
            mallsAdapter.clearMalls();
            searchResultsHeader.setVisibility(View.GONE);
            mallSelectInputView.setHint(R.string.search_by_location_hint);
        }
    }

    @OnClick(R.id.no_results_button)
    public void onNoResultsButtonClick() {
        getActivity().setResult(MallSearchSelectActivity.RESULT_SEARCH_BY_NAME);
        getActivity().finish();
    }

    private void trackLocationSearch(int searchResultsCount, String searchString) {
        HashMap<String, Object> contextData = new HashMap<>();
        contextData.put(AnalyticsManager.ContextDataKeys.SearchMallKeyword, searchString);
        contextData.put(AnalyticsManager.ContextDataKeys.SearchMallCount, searchResultsCount);
        analyticsManager.trackAction(AnalyticsManager.Actions.SearchByLocation, contextData);
    }
}
