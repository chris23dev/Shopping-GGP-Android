package com.ggp.theclub.repository.impl;

import android.text.TextUtils;
import android.util.Log;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.api.MallApi;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.customlocale.gateway.Language;
import com.ggp.theclub.customlocale.gateway.rest.LocaleApiClient;
import com.ggp.theclub.event.MallUpdateEvent;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.manager.NetworkManager;
import com.ggp.theclub.model.Alert;
import com.ggp.theclub.model.AuthToken;
import com.ggp.theclub.model.Category;
import com.ggp.theclub.model.DateRange;
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
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.ApiUtils;
import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.MovieUtils;
import com.ggp.theclub.util.PromotionUtils;
import com.ggp.theclub.util.SaleCategoryUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantCategoryUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import java8.util.Optional;
import java8.util.stream.StreamSupport;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MallRepositoryImpl implements MallRepository {

    private final String LOG_TAG = getClass().getSimpleName();

    private MallApi mallApi = MallApiClient.getInstance().getMallApi();

    private MallManager getMallManager() {
        return MallApplication.getApp().getMallManager();
    }

    public void prefetchData() {
        if (NetworkManager.getInstance().isNetworkAvailable()) {
            if (TextUtils.isEmpty(MallApiClient.getInstance().getAuthToken())) {
                Log.d(LOG_TAG, "PREFETCH: Generating new authToken");
                generateAuthToken();
            } else if (getMallManager().hasValidMall()) {
                Log.d(LOG_TAG, "PREFETCH: Using existing authToken");
                queryForSimpleMalls(malls -> {});
                prefetchMall();
                prefetchAlerts();
                queryForTenants(stores -> {});
                queryForSales(sales -> {});
                queryForCategories(categories -> {});
                queryForMallEvents(events -> {});
                prefetchTheaters();
            }
        }
    }

    private void generateAuthToken() {
        MallApiClient.getInstance().getMallApi().getAuthToken().enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if (response.isSuccessful()) {
                    MallApiClient.getInstance().setAuthToken(response.body().getAuthToken());
                    prefetchData();
                } else {
                    Log.e(LOG_TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                Log.w(LOG_TAG, t);
            }
        });
    }

    private void prefetchMall() {
        queryForMall(getMallManager().getMall().getId(), mall -> {
            getMallManager().addRecentMall(mall);
            ImageUtils.fetchImage(mall.getNonSvgLogoUrl());
            EventBus.getDefault().removeAllStickyEvents();
            EventBus.getDefault().post(new MallUpdateEvent());
        });
    }

    private void prefetchAlerts() {
        MallApiClient.getInstance().getMallApi().getAlerts(getMallManager().getMall().getId()).enqueue(new Callback<List<Alert>>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {

            }

            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                Log.w(LOG_TAG, t.getMessage());
            }
        });
    }

    private void prefetchTheaters() {
        if (getMallManager().getMall().hasTheater()) {
            MallApiClient.getInstance().getMallApi().getTheaters(getMallManager().getMall().getId()).enqueue(new Callback<List<MovieTheater>>() {
                @Override
                public void onResponse(Call<List<MovieTheater>> call, Response<List<MovieTheater>> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().isEmpty()) {
                            prefetchMovieLogos(response.body().get(0).getMovies());
                        }
                    } else {
                        Log.e(LOG_TAG, response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<MovieTheater>> call, Throwable t) {
                    Log.w(LOG_TAG, t);
                }
            });
        }
    }

    public void queryForMall(int mallId, QueryCallback<Mall> callback) {

        Observable<Mall> mallObservable = mallApi.getMall(mallId).onErrorReturn(throwable -> {
            Log.e(LOG_TAG, !StringUtils.isEmpty(throwable.getMessage()) ? throwable.getMessage() : "Error getting mall: " + throwable.getClass());
            return new Mall();
        });

        Observable<List<DateRange>> dateRangeObservable = mallApi.getDateRanges(mallId).onErrorReturn(throwable -> {
            Log.e(LOG_TAG, !StringUtils.isEmpty(throwable.getMessage()) ? throwable.getMessage() : "Error getting date ranges: " + throwable.getClass());
            return new ArrayList<>(0);
        });

        Observable<Mall> populatedMallObservable = mallObservable.zipWith(dateRangeObservable, (mall, dateRanges) -> {
            ApiUtils.populateDateRanges(mall, dateRanges);
            return mall;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        populatedMallObservable.subscribe((mall) -> {
            callback.onComplete(mall);
        });
    }

    public void queryForSimpleMalls(QueryCallback<List<Mall>> callback) {
        MallApiClient.getInstance().getMallApi().getSimpleMalls().enqueue(new Callback<MallApi.PagedResponse<List<Mall>>>() {
            @Override
            public void onResponse(Call<MallApi.PagedResponse<List<Mall>>> call, Response<MallApi.PagedResponse<List<Mall>>> response) {
                if (response.isSuccessful() && !response.body().getContent().isEmpty()) {
                    callback.onComplete(response.body().getContent());
                } else {
                    callback.onComplete(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MallApi.PagedResponse<List<Mall>>> call, Throwable t) {
                Log.w(LOG_TAG, t);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    public void queryForTenants(QueryCallback<List<Tenant>> callback) {
        MallApiClient.getInstance().getMallApi().getTenants(getMallManager().getMall().getId()).enqueue(new Callback<List<Tenant>>() {
            @Override
            public void onResponse(Call<List<Tenant>> call, Response<List<Tenant>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    prefetchTenantLogos(response.body());
                } else {
                    Log.e(LOG_TAG, response.message());
                }
                callback.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<Tenant>> call, Throwable t) {
                Log.w(LOG_TAG, t);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    public void queryForCategories(QueryCallback<List<Category>> callback) {
        MallApiClient.getInstance().getMallApi().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    callback.onComplete(response.body());
                } else {
                    Log.e(LOG_TAG, response.message());
                    callback.onComplete(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.w(LOG_TAG, t);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    public void queryForCampaigns(QueryCallback<List<Category>> callback) {
        MallApiClient.getInstance().getMallApi().getCampaigns().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    callback.onComplete(response.body());
                } else {
                    Log.e(LOG_TAG, response.message());
                    callback.onComplete(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.w(LOG_TAG, t);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    public void queryForParkingSite(QueryCallback<ParkingSite> callback) {
        MallApiClient.getInstance().getMallApi().getParkingSite(getMallManager().getMall().getId()).enqueue(new Callback<ParkingSite>() {
            @Override
            public void onResponse(Call<ParkingSite> call, Response<ParkingSite> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onComplete(response.body());
                } else {
                    Log.e(LOG_TAG, response.message());
                    callback.onComplete(new ParkingSite());
                }
            }

            @Override
            public void onFailure(Call<ParkingSite> call, Throwable t) {
                Log.w(LOG_TAG, t);
                callback.onComplete(new ParkingSite());
            }
        });
    }

    public void queryForTenantCategories(QueryCallback<List<TenantCategory>> callback) {
        queryForTenants(tenants -> queryForCategories(categories -> {
            List<TenantCategory> tenantCategories = TenantCategoryUtils.createTenantCategories(tenants, categories);
            callback.onComplete(tenantCategories);
        }));
    }

    public void queryForSaleCategories(QueryCallback<List<SaleCategory>> callback) {
        queryForSales(sales -> queryForCategories(categories -> queryForCampaigns(campaigns -> {
                List<SaleCategory> saleCategories = SaleCategoryUtils.createSaleCategories(sales, categories, campaigns);
                callback.onComplete(saleCategories);
            })
        ));
    }

    public Observable<List<Tenant>> queryForTenants() {
        return Observable.create(subscriber -> {
            queryForTenants(tenants -> {
                subscriber.onNext(tenants);
                subscriber.onCompleted();
            });
        });
    }

    public void queryForMallEvents(QueryCallback<List<MallEvent>> callback) {
        MallApiClient.getInstance().getMallApi().getMallEvents(getMallManager().getMall().getId(), DateUtils.todayDateOnly()).enqueue(new Callback<List<MallEvent>>() {
            @Override
            public void onResponse(Call<List<MallEvent>> call, Response<List<MallEvent>> response) {
                if (response.isSuccessful()) {
                    if (!response.body().isEmpty()) {
                        queryForTenants((stores) -> {
                            List<MallEvent> mallEventsWithStores = PromotionUtils.getPromotionsWithStores(response.body(), stores);
                            callback.onComplete(mallEventsWithStores);
                        });
                        prefetchMallEventLogos(response.body());
                    } else {
                        callback.onComplete(response.body());
                    }

                } else {
                    Log.e(LOG_TAG, response.message());
                    callback.onComplete(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<MallEvent>> call, Throwable t) {
                Log.w(LOG_TAG, t);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    public Observable<List<MallEvent>> queryForMallEvents() {
        Observable<List<MallEvent>> eventsObservable = Observable.create(subscriber -> {
            queryForMallEvents(events -> {
                subscriber.onNext(events);
                subscriber.onCompleted();
            });
        });
        return eventsObservable;
    }

    public void queryForSales(QueryCallback<List<Sale>> callback) {
        MallApiClient.getInstance().getMallApi().getSales(getMallManager().getMall().getId(), DateUtils.todayDateOnly()).enqueue(new Callback<List<Sale>>() {
            @Override
            public void onResponse(Call<List<Sale>> call, Response<List<Sale>> response) {
                if (response.isSuccessful()) {
                    if (!response.body().isEmpty()) {
                        queryForTenants((tenants) -> {
                            List<Sale> salesWithTenants = PromotionUtils.getPromotionsWithStores(response.body(), tenants);
                            callback.onComplete(salesWithTenants);
                        });
                        prefetchSaleLogos(response.body());
                    } else {
                        callback.onComplete(response.body());
                    }
                } else {
                    Log.e(LOG_TAG, response.message());
                    callback.onComplete(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Sale>> call, Throwable t) {
                Log.w(LOG_TAG, t);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    public Observable<List<Sale>> queryForSales() {
        Observable<List<Sale>> salesObservable = Observable.create(subscriber -> {
            queryForSales(sales -> {
                subscriber.onNext(sales);
                subscriber.onCompleted();
            });
        });
        return salesObservable;
    }

    public Observable<Alert> queryForAlerts() {
        Observable<Alert> alertsObservable = Observable.create(subscriber -> {
            MallApiClient.getInstance().getMallApi().getAlerts(getMallManager().getMall().getId()).enqueue(new Callback<List<Alert>>() {
                @Override
                public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        subscriber.onNext(null);
                    }
                    Optional<Alert> activeAlertOptional = StreamSupport.stream(response.body()).filter(alert ->
                            alert.getEffectiveStartDateTime() != null && alert.getEffectiveStartDateTime().isBefore(HoursUtils.getDateTimeForMall(getMallManager().getMall())))
                            .findFirst();
                    if (activeAlertOptional.isPresent()) {
                        subscriber.onNext(activeAlertOptional.get());
                    } else {
                        subscriber.onNext(null);
                    }
                    subscriber.onCompleted();
                }

                @Override
                public void onFailure(Call<List<Alert>> call, Throwable t) {
                    Log.i(LOG_TAG, t.getMessage());
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            });
        });
        return alertsObservable;
    }

    public Observable<Hero> queryForFeaturedContent() {
        Observable<Hero> heroesObservable = Observable.create(subscriber ->
                MallApiClient.getInstance().getMallApi().getHeroes(MallRepositoryImpl.this.getMallManager().getMall().getId()).enqueue(new Callback<List<Hero>>() {
                    @Override
                    public void onResponse(Call<List<Hero>> call, Response<List<Hero>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            subscriber.onNext(null);
                        }
                        Optional<Hero> activeHeroOptional = StreamSupport.stream(response.body()).filter(hero -> DateUtils.isTodayInDateRange(hero.getStartDate(), hero.getEndDate())).findFirst();
                        if (activeHeroOptional.isPresent()) {
                            subscriber.onNext(activeHeroOptional.get());
                        } else {
                            subscriber.onNext(null);
                        }
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Call<List<Hero>> call, Throwable t) {
                        Log.i(LOG_TAG, t.getMessage());
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                }));
        return heroesObservable;
    }

    @Override
    public Observable<List<Language>> queryForLanguages() {
        return Observable.create(subscriber -> LocaleApiClient.getLocaleClient().getLanguages().enqueue(
                new Callback<List<Language>>() {
                    @Override
                    public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            subscriber.onError(new Throwable("No result from server!"));
                        }

                        subscriber.onNext(response.body());
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Call<List<Language>> call, Throwable t) {
                        Log.i(LOG_TAG, t.getMessage());
                        subscriber.onError(t);
                    }
                }
                )
        );
    }

    public void queryForTheaters(QueryCallback<List<MovieTheater>> callback) {
        MallApiClient.getInstance().getMallApi().getTheaters(getMallManager().getMall().getId()).enqueue(new Callback<List<MovieTheater>>() {
            @Override
            public void onResponse(Call<List<MovieTheater>> call, Response<List<MovieTheater>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    List<MovieTheater> movieTheaters = response.body();
                    if(movieTheaters!=null && !movieTheaters.isEmpty()){
                        for(MovieTheater movieTheater: movieTheaters){
                            String tmsId = movieTheater.getTmsId();
                            if(tmsId == null || tmsId.isEmpty()){
                                movieTheater.setTheaterUrl(movieTheater.getWebsiteUrl());
                            }
                        }
                    }
                    callback.onComplete(response.body());
                } else {
                    Log.e(LOG_TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<MovieTheater>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }

    public void queryForMovies(QueryCallback<List<Movie>> callback) {
        queryForTheaters(movieTheaters -> {
            List<Movie> movieList = MovieUtils.getUniqueMovies(movieTheaters);
            MovieUtils.populateTheaterShowtimes(movieList, movieTheaters);
            callback.onComplete(movieList);
        });
    }

    private void prefetchTenantLogos(List<Tenant> tenants) {
        if (tenants != null) {
            StreamSupport.stream(tenants).forEach(tenant -> ImageUtils.fetchImage(tenant.getLogoUrl()));
        }
    }

    private void prefetchMallEventLogos(List<MallEvent> mallEvents) {
        if (mallEvents != null) {
            StreamSupport.stream(mallEvents).forEach(mallEvent -> ImageUtils.fetchImage(mallEvent.getImageUrl()));
        }
    }

    private void prefetchSaleLogos(List<Sale> sales) {
        if (sales != null) {
            StreamSupport.stream(sales).forEach(sale -> ImageUtils.fetchImage(sale.getImageUrl()));
        }
    }

    private void prefetchMovieLogos(List<Movie> movies) {
        if (movies != null) {
            StreamSupport.stream(movies).forEach(movie -> ImageUtils.fetchImage(movie.getLargePosterImageUrl()));
        }
    }
}
