package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.ManageFavoritesActivity;
import com.ggp.theclub.adapter.MallEventsAdapter;
import com.ggp.theclub.event.FavoritesUpdateEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.PromotionUtils;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class HomeFeedAuthenticatedFragment extends HomeFeedBaseFragment {
    @Bind(R.id.no_favorite_sales_message) View noFavoriteSalesMessage;
    @Bind(R.id.events_view) CustomRecyclerView favoriteEventsView;
    @Bind(R.id.no_favorite_sales_header) TextView noFavoriteSalesHeader;
    @Bind(R.id.no_favorite_sales_button) Button noFavoriteSalesButton;
    @Bind(R.id.now_open) TextView nowOpen;
    @Bind(R.id.up_to_date) TextView upToDate;
    @Bind(R.id.add_favorites) TextView addFavorites;


    private MallEventsAdapter mallEventsAdapter;

    public static HomeFeedAuthenticatedFragment newInstance() {
        return new HomeFeedAuthenticatedFragment();
    }

    @Override
    public void onFragmentVisible() {
        analyticsManager.trackScreen(AnalyticsManager.Screens.JustForYou);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.home_feed_fragment_authenticated, container);
        nowOpen.setText(getString(R.string.store_opening));
        upToDate.setText(getString(R.string.up_to_date));
        addFavorites.setText(getString(R.string.add_favorites));
        return view;
    }

    @Override
    protected void configureView() {
        super.configureView();

        mallEventsAdapter = new MallEventsAdapter(getActivity());
        favoriteEventsView.setAdapter(mallEventsAdapter);
    }

    @Override
    protected void refreshData() {
        storeOpeningView.setDataLoaded(false);
        singleSaleView.setDataLoaded(false);
        remainingSalesView.setDataLoaded(false);
        favoriteEventsView.setDataLoaded(false);

        homeController.getHomeFeedAuthenticatedObservable().subscribe((homeFeedAuthenticatedViewModel) -> {
            setupBaseContent(homeFeedAuthenticatedViewModel);
            setupEvents(homeFeedAuthenticatedViewModel.getFavoriteEvents());
            setupNoFavoritesMessage(homeFeedAuthenticatedViewModel.getSales(), homeFeedAuthenticatedViewModel.getTenants());
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    protected void setupNoFavoritesMessage(List<Sale> sales, List<Tenant> tenants) {
        if (sales.isEmpty()) {
            boolean hasFavoriteTenants = !TenantUtils.getFavoriteTenants(tenants).isEmpty();
            noFavoriteSalesHeader.setText(hasFavoriteTenants ? R.string.no_favorite_sales_header : R.string.no_favorites_header);
            noFavoriteSalesButton.setText(hasFavoriteTenants ? R.string.no_favorite_sales_button : R.string.no_favorites_button);
            noFavoriteSalesMessage.setVisibility(View.VISIBLE);
        } else {
            noFavoriteSalesMessage.setVisibility(View.GONE);
        }
    }

    private void setupEvents(List<MallEvent> events) {
        mallEventsAdapter.setMallEvents(PromotionUtils.getFavoritePromotions(events));
        favoriteEventsView.setDataLoaded(true);
    }

    @OnClick(R.id.no_favorite_sales_button)
    public void onNoFavoritesClick() {
        startActivity(ManageFavoritesActivity.buildIntent(getActivity()));
    }

    public void onEvent(FavoritesUpdateEvent favoritesUpdateEvent) {
        refreshData();
    }
}