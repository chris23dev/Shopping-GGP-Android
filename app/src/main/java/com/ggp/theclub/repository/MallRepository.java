package com.ggp.theclub.repository;

import com.ggp.theclub.customlocale.gateway.Language;
import com.ggp.theclub.model.Alert;
import com.ggp.theclub.model.Hero;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Movie;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.model.ParkingSite;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.SaleCategory;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.TenantCategory;

import java.util.List;

import rx.Observable;

public interface MallRepository {

    void prefetchData();

    void queryForMall(int mallId, QueryCallback<Mall> callback);
    void queryForSimpleMalls(QueryCallback<List<Mall>> callback);
    void queryForTenants(QueryCallback<List<Tenant>> callback);
    void queryForMallEvents(QueryCallback<List<MallEvent>> callback);
    void queryForSales(QueryCallback<List<Sale>> callback);
    void queryForTenantCategories(QueryCallback<List<TenantCategory>> callback);
    void queryForSaleCategories(QueryCallback<List<SaleCategory>> callback);
    void queryForParkingSite(QueryCallback<ParkingSite> callback);
    void queryForTheaters(QueryCallback<List<MovieTheater>> callback);
    void queryForMovies(QueryCallback<List<Movie>> callback);

    Observable<List<Tenant>> queryForTenants();
    Observable<List<MallEvent>> queryForMallEvents();
    Observable<List<Sale>> queryForSales();
    Observable<Alert> queryForAlerts();
    Observable<Hero> queryForFeaturedContent();
    Observable<List<Language>> queryForLanguages();

    interface QueryCallback<T> {
        void onComplete(T object);
    }
}