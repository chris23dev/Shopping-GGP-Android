package com.ggp.theclub.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.MainActivity;
import com.ggp.theclub.activity.SalesListActivity;
import com.ggp.theclub.adapter.HomeFeedNewTenantsAdapter;
import com.ggp.theclub.adapter.HomeFeedSalesAdapter;
import com.ggp.theclub.comparator.PromotionEndDateComparator;
import com.ggp.theclub.controller.HomeController;
import com.ggp.theclub.event.MapReadyEvent;
import com.ggp.theclub.model.Alert;
import com.ggp.theclub.model.FeaturedContentUrl;
import com.ggp.theclub.model.Hero;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.SaleCategory;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.view.HomeFeedViewModel;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.SaleCategoryUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public abstract class HomeFeedBaseFragment extends BaseFragment {
    @BindColor(R.color.primary_blue) int primaryBlue;
    @BindString(R.string.more_hours_open_format) String todaysHoursOpenFormat;
    @BindString(R.string.more_hours_closed_format) String todaysHoursClosedFormat;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.mall_alert_layout) View alertLayout;
    @Bind(R.id.alert_message) TextView alertMessage;
    @Bind(R.id.mall_hours) TextView mallHoursView;
    @Bind(R.id.store_opening_layout) View storeOpeningLayout;
    @Bind(R.id.store_opening_view) CustomRecyclerView storeOpeningView;
    @Bind(R.id.featured_content_layout) View featuredContentLayout;
    @Bind(R.id.featured_content_mall_name) TextView featuredContentMallName;
    @Bind(R.id.featured_content_logo) ImageView featuredContentLogo;
    @Bind(R.id.featured_content_title) TextView featuredContentTitle;
    @Bind(R.id.featured_content_description) TextView featuredContentDescription;
    @Bind(R.id.featured_content_link) TextView featuredContentLink;
    @Bind(R.id.single_sale_view) CustomRecyclerView singleSaleView;
    @Bind(R.id.remaining_sales_view) CustomRecyclerView remainingSalesView;

    private HomeFeedNewTenantsAdapter newStoresAdapter;
    protected HomeFeedSalesAdapter singleSaleAdapter;
    protected HomeFeedSalesAdapter remainingSalesAdapter;
    private Hero featuredContent;
    protected HomeController homeController = new HomeController();

    protected abstract void refreshData();

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
        swipeRefreshLayout.setColorSchemeColors(primaryBlue);
        singleSaleAdapter = new HomeFeedSalesAdapter(getActivity());
        remainingSalesAdapter = new HomeFeedSalesAdapter(getActivity());
        singleSaleView.setAdapter(singleSaleAdapter);
        remainingSalesView.setAdapter(remainingSalesAdapter);
        HoursUtils.setMallHoursString(mallManager.getMall(), mallHoursView, todaysHoursOpenFormat, todaysHoursClosedFormat);
        refreshData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });
    }

    protected void setupBaseContent(HomeFeedViewModel homeFeedViewModel) {
        setupAlert(homeFeedViewModel.getMallAlert());
        featuredContent = homeFeedViewModel.getFeaturedContent();
        if (featuredContent != null) {
            setupFeaturedContent(featuredContent);
        }
        setupStoreOpenings(homeFeedViewModel.getTenantOpenings());
        populateSales(homeFeedViewModel.getSales());
    }

    /**
     * May take a null alert
     */
    protected void setupAlert(Alert alert) {
        if (alert != null) {
            showAlert(alert.getMessage());
        } else {
            hideAlert();
        }
    }

    protected void setupStoreOpenings(List<Tenant> tenants) {
        if (!tenants.isEmpty()) {
            storeOpeningLayout.setVisibility(View.VISIBLE);
            newStoresAdapter = new HomeFeedNewTenantsAdapter(getActivity(), tenants);
            storeOpeningView.setAdapter(newStoresAdapter);
            storeOpeningView.setDataLoaded(true);
        } else {
            storeOpeningLayout.setVisibility(View.GONE);
        }
    }

    protected void setupFeaturedContent(Hero hero) {
        ImageUtils.loadImage(hero.getImageUrl(), featuredContentLogo, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                featuredContentMallName.setText(mallManager.getMall().getName());
                featuredContentTitle.setText(hero.getTitle());
                featuredContentDescription.setText(hero.getDescription());
                setupFeaturedContentLink(hero);
                featuredContentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {}
        });
    }

    private void setupFeaturedContentLink(Hero hero) {
        if (!StringUtils.isEmpty(hero.getUrl()) && !StringUtils.isEmpty(hero.getUrlText())) {
            featuredContentLink.setText(hero.getUrlText());
            featuredContentLink.setVisibility(View.VISIBLE);
        } else {
            featuredContentLink.setVisibility(View.GONE);
        }
    }

    /**
     * Populates singleSaleView with a random sale, then populates remainingSalesView ordered by end date.
     */
    protected void populateSales(List<Sale> sales) {
        List<Sale> saleList = StreamSupport.stream(sales).sorted(new PromotionEndDateComparator()).collect(Collectors.toList());
        List randomSale = new ArrayList(1);
        if (!saleList.isEmpty()) {
            int randomSaleIndex = new Random().nextInt(saleList.size());
            randomSale.add(saleList.get(randomSaleIndex));
            saleList.remove(randomSaleIndex);
        }

        singleSaleAdapter.setSales(randomSale);
        remainingSalesAdapter.setSales(saleList);

        singleSaleView.setDataLoaded(true);
        remainingSalesView.setDataLoaded(true);
    }

    private void showAlert(String message) {
        alertMessage.setText(message);
        alertLayout.setVisibility(View.VISIBLE);
    }

    private void hideAlert() {
        alertLayout.setVisibility(View.GONE);
    }

    public void onEvent(MapReadyEvent event) {
        //new tenants may not have locations, and need them added
        if (event.isMapReady() && newStoresAdapter != null) {
            newStoresAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.featured_content_layout)
    public void onFeaturedContentClick() {
        if (featuredContent != null && !StringUtils.isEmpty(featuredContent.getUrl())) {
            if (FeaturedContentUrl.getCampaignCategoryCode(featuredContent.getUrl()) != null) {
                startShoppingCampaignActivity(FeaturedContentUrl.getCampaignCategoryCode(featuredContent.getUrl()));
            } else if (FeaturedContentUrl.PARKING.equals(featuredContent.getUrl())) {
                ((MainActivity) getActivity()).switchToTab(MainActivity.Tab.PARKING);
            } else if (FeaturedContentUrl.DIRECTORY.equals(featuredContent.getUrl())) {
                ((MainActivity) getActivity()).switchToTab(MainActivity.Tab.DIRECTORY);
            } else {
                IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(featuredContent.getUrl())), getActivity());
            }
        }
    }

    private void startShoppingCampaignActivity(String campaignCategoryCode) {
        //prevent the user from clicking featured content while waiting for the api call
        featuredContentLayout.setClickable(false);
        //check if there is at least one sale in this category, if not nothing happens.
        mallRepository.queryForSaleCategories((saleCategories) ->  {
            featuredContentLayout.setClickable(true);
            String categoryCode = campaignCategoryCode;
            SaleCategory category = SaleCategoryUtils.getSaleCategoryByCode(categoryCode, saleCategories);
            if(category != null && category.getSales().size() > 0) {
                startActivity(SalesListActivity.buildIntent(getActivity(), categoryCode));
            }
        });
    }
}
