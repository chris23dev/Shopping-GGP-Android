package com.ggp.theclub.controller;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.comparator.PromotionEndDateComparator;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.view.HomeFeedAuthenticatedViewModel;
import com.ggp.theclub.model.view.HomeFeedUnauthenticatedViewModel;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.PromotionUtils;
import com.ggp.theclub.util.TenantUtils;

import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import rx.Observable;

public class HomeController {

    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();

    public Observable<HomeFeedAuthenticatedViewModel> getHomeFeedAuthenticatedObservable() {
        return Observable.zip(mallRepository.queryForAlerts(), mallRepository.queryForFeaturedContent(),
                mallRepository.queryForTenants(), mallRepository.queryForSales(), mallRepository.queryForMallEvents(), accountManager.fetchAccountInfo(),
                (alert, hero, tenants, sales, events, currentUser) -> {
                    HomeFeedAuthenticatedViewModel homeFeedAuthenticatedViewModel = new HomeFeedAuthenticatedViewModel();

                    homeFeedAuthenticatedViewModel.setMallAlert(alert);
                    homeFeedAuthenticatedViewModel.setFeaturedContent(hero);
                    homeFeedAuthenticatedViewModel.setTenantOpenings(TenantUtils.getSortedNewTenants(tenants));
                    homeFeedAuthenticatedViewModel.setTenants(tenants);

                    //TODO use user here
                    List<Sale> favoriteSales = StreamSupport.stream(PromotionUtils.getFavoritePromotions(sales)).sorted(new PromotionEndDateComparator()).collect(Collectors.toList());
                    homeFeedAuthenticatedViewModel.setSales(favoriteSales);

                    List<MallEvent> favoriteEvents = StreamSupport.stream(PromotionUtils.getFavoritePromotions(events)).sorted(new PromotionEndDateComparator()).collect(Collectors.toList());
                    homeFeedAuthenticatedViewModel.setFavoriteEvents(favoriteEvents);
                    return homeFeedAuthenticatedViewModel;
                });
    }

    public Observable<HomeFeedUnauthenticatedViewModel> getHomeFeedUnauthenticatedObservable() {
        return Observable.zip(
                mallRepository.queryForAlerts(),
                mallRepository.queryForFeaturedContent(),
                mallRepository.queryForTenants(),
                mallRepository.queryForSales(), (alert, hero, tenants, sales) -> {
                    HomeFeedUnauthenticatedViewModel homeFeedUnauthenticatedData = new HomeFeedUnauthenticatedViewModel();
                    homeFeedUnauthenticatedData.setMallAlert(alert);
                    homeFeedUnauthenticatedData.setFeaturedContent(hero);
                    homeFeedUnauthenticatedData.setTenantOpenings(TenantUtils.getSortedNewTenants(tenants));
                    List<Sale> topRetailerSales = StreamSupport.stream(sales).filter(Sale::isTopRetailer).sorted(new PromotionEndDateComparator()).collect(Collectors.toList());
                    homeFeedUnauthenticatedData.setSales(topRetailerSales);
                    return homeFeedUnauthenticatedData;
                });
    }
}
