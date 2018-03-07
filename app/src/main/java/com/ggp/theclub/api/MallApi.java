package com.ggp.theclub.api;

import com.ggp.theclub.model.Alert;
import com.ggp.theclub.model.AuthToken;
import com.ggp.theclub.model.Category;
import com.ggp.theclub.model.DateRange;
import com.ggp.theclub.model.Hero;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.MobileConfig;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.model.ParkingSite;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Sweepstakes;
import com.ggp.theclub.model.Tenant;

import java.util.List;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MallApi {
    String API_PARAM_ID = "id";
    String API_ENDPOINT_AUTH = "oauth/token?grant_type=client_credentials";
    String API_ENDPOINT_CONFIG = "mobile-app/config";
    String API_ENDPOINT_SWEEPSTAKES = "config/sweepstakes";
    String API_ENDPOINT_ALL_SIMPLE_MALLS = "malls?minimalView&size=300";
    String API_ENDPOINT_MALL = "malls/{" + API_PARAM_ID + "}";
    String API_ENDPOINT_STORES = API_ENDPOINT_MALL + "/stores";
    String API_ENDPOINT_THEATERS = API_ENDPOINT_MALL + "/movie-theaters";
    String API_ENDPOINT_MALL_SEARCH_GEO = "malls/search/searchByLatLong";
    String API_ENDPOINT_SALES = API_ENDPOINT_MALL + "/sales";
    String API_ENDPOINT_EVENTS = API_ENDPOINT_MALL + "/events";
    String API_ENDPOINT_ALERTS = API_ENDPOINT_MALL + "/alerts";
    String API_ENDPOINT_HEROES = API_ENDPOINT_MALL + "/heroes";
    String API_ENDPOINT_DATE_RANGES = API_ENDPOINT_MALL + "/date-ranges";
    String API_ENDPOINT_CATEGORIES = "categories";
    String API_ENDPOINT_CAMPAIGNS = "campaigns";
    String API_ENDPOINT_PARK_ASSIST = API_ENDPOINT_MALL + "/park-assist";

    String API_NO_CACHE_HEADER = "Cache-Control: no-cache";

    @POST(API_ENDPOINT_AUTH)
    Call<AuthToken> getAuthToken();

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_CONFIG)
    Call<MobileConfig> getMobileConfig();

    @GET(API_ENDPOINT_SWEEPSTAKES)
    Call<List<Sweepstakes>> getSweepstakes();
    /**
     * This endpoint returns an entry for each mall but does not return fully-populated malls.
     * Only id, name, status, and websiteUrl will be populated
     */
    @GET(API_ENDPOINT_ALL_SIMPLE_MALLS)
    Call<PagedResponse<List<Mall>>> getSimpleMalls();

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_MALL)
    Observable<Mall> getMall(@Path(API_PARAM_ID) int id);

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_STORES)
    Call<List<Tenant>> getTenants(@Path(API_PARAM_ID) int id);

    @GET(API_ENDPOINT_THEATERS)
    Call<List<MovieTheater>> getTheaters(@Path(API_PARAM_ID) int id);

    @GET(API_ENDPOINT_MALL_SEARCH_GEO)
    Call<List<Mall>> getMallsByLocation(@Query("lat") double latitude, @Query("long") double longitude);

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_SALES)
    Call<List<Sale>> getSales(@Path(API_PARAM_ID) int id, @Query("date") String date);

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_EVENTS)
    Call<List<MallEvent>> getMallEvents(@Path(API_PARAM_ID) int id, @Query("date") String date);

    @GET(API_ENDPOINT_ALERTS)
    Call<List<Alert>> getAlerts(@Path(API_PARAM_ID) int id);

    @GET(API_ENDPOINT_HEROES)
    Call<List<Hero>> getHeroes(@Path(API_PARAM_ID) int id);

    @GET(API_ENDPOINT_PARK_ASSIST)
    Call<ParkingSite> getParkingSite(@Path(API_PARAM_ID) int id);

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_CATEGORIES)
    Call<List<Category>> getCategories();

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_CAMPAIGNS)
    Call<List<Category>> getCampaigns();

    @Headers(API_NO_CACHE_HEADER)
    @GET(API_ENDPOINT_DATE_RANGES)
    Observable<List<DateRange>> getDateRanges(@Path(API_PARAM_ID) int id);

    //This is for reading paged responses
    class PagedResponse<T> {
        @Getter T content;
    }
}
