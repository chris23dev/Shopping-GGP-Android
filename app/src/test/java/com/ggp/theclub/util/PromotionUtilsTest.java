package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PromotionUtilsTest extends BaseTest {
    private List<Tenant> tenants = new ArrayList<>();
    private List<MallEvent> mallEvents = new ArrayList<>();
    private List<Sale> sales = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        super.setup();
        String storesJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("resources/tenants_data.json"));
        tenants = gson.fromJson(storesJson, new TypeToken<List<Tenant>>() {}.getType());
        String mallEventsJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("resources/mall_events_data.json"));
        mallEvents = gson.fromJson(mallEventsJson, new TypeToken<List<MallEvent>>() {}.getType());
        String salesJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("resources/sales_data.json"));
        sales = gson.fromJson(salesJson, new TypeToken<List<Sale>>() {}.getType());
        accountManager.getCurrentUser().addFavoriteTenant(tenants.get(0));
        accountManager.getCurrentUser().addFavoriteTenant(tenants.get(1));
        accountManager.getCurrentUser().addFavoriteTenant(tenants.get(2));
    }

    @After
    public void tearDown() {
        tenants.clear();
        mallEvents.clear();
        sales.clear();
        accountManager.getCurrentUser().getFavorites().clear();
    }

    @Test
    public void testGetMallEventsWithStores() {
        List<MallEvent> mallEventsWithStores = PromotionUtils.getPromotionsWithStores(mallEvents, tenants);
        assertEquals(mallEventsWithStores.size(), mallEvents.size());
        assertFalse(mallEventsWithStores.get(0).getTenant() == null);
        assertFalse(mallEventsWithStores.get(1).getTenant() == null);
        assertTrue(mallEventsWithStores.get(2).getTenant() == null);
        assertFalse(mallEventsWithStores.get(3).getTenant() == null);
        assertFalse(mallEventsWithStores.get(4).getTenant() == null);
        assertTrue(mallEventsWithStores.get(5).getTenant() == null);
        assertTrue(mallEventsWithStores.get(6).getTenant() == null);
        assertTrue(mallEventsWithStores.get(7).getTenant() == null);
        assertFalse(mallEventsWithStores.get(8).getTenant() == null);
        assertTrue(mallEventsWithStores.get(9).getTenant() == null);
        assertEquals(mallEventsWithStores.get(0).getTenant().getId(), 1);
        assertEquals(mallEventsWithStores.get(1).getTenant().getId(), 2);
        assertEquals(mallEventsWithStores.get(3).getTenant().getId(), 3);
        assertEquals(mallEventsWithStores.get(4).getTenant().getId(), 3);
        assertEquals(mallEventsWithStores.get(8).getTenant().getId(), 2);

        mallEventsWithStores = PromotionUtils.getPromotionsWithStores(null, tenants);
        assertTrue(mallEventsWithStores.isEmpty());

        mallEventsWithStores = PromotionUtils.getPromotionsWithStores(mallEvents, null);
        assertEquals(mallEventsWithStores.size(), mallEvents.size());
    }

    public void testSalesWithStores() {
        List<Sale> salesWithStores = PromotionUtils.getPromotionsWithStores(sales, tenants);
        assertEquals(salesWithStores, sales);
        assertEquals(salesWithStores.get(0).getTenant().getId(), 1);
        assertEquals(salesWithStores.get(1).getTenant().getId(), 2);
        assertEquals(salesWithStores.get(2).getTenant().getId(), 2);
        assertEquals(salesWithStores.get(3).getTenant().getId(), 4);
        assertEquals(salesWithStores.get(4).getTenant().getId(), 5);
    }

    @Test
    public void testGetSortedMallEvents() {
        List<MallEvent> mallEventsWithStores = PromotionUtils.getPromotionsWithStores(mallEvents, tenants);
        List<MallEvent> sortedMallEvents = PromotionUtils.getSortedMallEvents(mallEventsWithStores);
        assertEquals(sortedMallEvents.size(), mallEvents.size());
        assertEquals(sortedMallEvents.get(0).getId(), 1);
        assertEquals(sortedMallEvents.get(1).getId(), 5);
        assertEquals(sortedMallEvents.get(2).getId(), 2);
        assertEquals(sortedMallEvents.get(3).getId(), 4);
        assertEquals(sortedMallEvents.get(4).getId(), 9);
        assertEquals(sortedMallEvents.get(5).getId(), 3);
        assertEquals(sortedMallEvents.get(6).getId(), 8);
        assertEquals(sortedMallEvents.get(7).getId(), 10);
        assertEquals(sortedMallEvents.get(8).getId(), 7);
        assertEquals(sortedMallEvents.get(9).getId(), 6);

        sortedMallEvents = PromotionUtils.getSortedMallEvents(null);
        assertTrue(sortedMallEvents.isEmpty());
    }

    @Test
    public void testGetMallEventsByStoreId() {
        List<MallEvent> mallEventsWithStores = PromotionUtils.getPromotionsWithStores(mallEvents, tenants);

        List<MallEvent> mallEventsByStoreId = PromotionUtils.getMallEventsByStoreId(0, mallEventsWithStores);
        assertTrue(mallEventsByStoreId.isEmpty());

        mallEventsByStoreId = PromotionUtils.getMallEventsByStoreId(1, null);
        assertTrue(mallEventsByStoreId.isEmpty());

        mallEventsByStoreId = PromotionUtils.getMallEventsByStoreId(1, mallEventsWithStores);
        assertFalse(mallEventsByStoreId.isEmpty());
        assertEquals(mallEventsByStoreId.size(), 1);
        assertEquals(mallEventsByStoreId.get(0).getId(), 1);

        mallEventsByStoreId = PromotionUtils.getMallEventsByStoreId(2, mallEventsWithStores);
        assertFalse(mallEventsByStoreId.isEmpty());
        assertEquals(mallEventsByStoreId.size(), 2);
        assertEquals(mallEventsByStoreId.get(0).getId(), 2);
        assertEquals(mallEventsByStoreId.get(1).getId(), 9);
    }

    @Test
    public void testGetMallEventsByEndDate() {
        List<MallEvent> mallEventsByEndDate = PromotionUtils.getMallEventsByEndDate(mallEvents);
        assertEquals(mallEventsByEndDate.size(), mallEvents.size());
        assertEquals(mallEventsByEndDate.get(0).getId(), 3);
        assertEquals(mallEventsByEndDate.get(1).getId(), 8);
        assertEquals(mallEventsByEndDate.get(2).getId(), 1);
        assertEquals(mallEventsByEndDate.get(3).getId(), 5);
        assertEquals(mallEventsByEndDate.get(4).getId(), 10);
        assertEquals(mallEventsByEndDate.get(5).getId(), 2);
        assertEquals(mallEventsByEndDate.get(6).getId(), 7);
        assertEquals(mallEventsByEndDate.get(7).getId(), 6);
        assertEquals(mallEventsByEndDate.get(8).getId(), 4);
        assertEquals(mallEventsByEndDate.get(9).getId(), 9);

        mallEventsByEndDate = PromotionUtils.getMallEventsByEndDate(null);
        assertTrue(mallEventsByEndDate.isEmpty());
    }

    @Test
    public void testGetFavoriteMallEventsByEndDate() {
        List<MallEvent> mallEventsWithStores = PromotionUtils.getPromotionsWithStores(mallEvents, tenants);
        List<MallEvent> favoriteMallEventsByEndDate = PromotionUtils.getFavoriteMallEventsByEndDate(mallEventsWithStores);
        assertEquals(favoriteMallEventsByEndDate.size(), 5);
        assertEquals(favoriteMallEventsByEndDate.get(0).getId(), 1);
        assertEquals(favoriteMallEventsByEndDate.get(1).getId(), 5);
        assertEquals(favoriteMallEventsByEndDate.get(2).getId(), 2);
        assertEquals(favoriteMallEventsByEndDate.get(3).getId(), 4);
        assertEquals(favoriteMallEventsByEndDate.get(4).getId(), 9);

        favoriteMallEventsByEndDate = PromotionUtils.getFavoriteMallEventsByEndDate(null);
        assertTrue(favoriteMallEventsByEndDate.isEmpty());
    }

    @Test
    public void testGetFeaturedSales() {
        List<Sale> featuredSales = PromotionUtils.getFeaturedSales(sales);
        assertEquals(featuredSales.size(), 9);
        assertEquals(featuredSales.get(0).getId(), 1);
        assertEquals(featuredSales.get(1).getId(), 2);
        assertEquals(featuredSales.get(2).getId(), 4);
        assertEquals(featuredSales.get(3).getId(), 5);
        assertEquals(featuredSales.get(4).getId(), 8);
        assertEquals(featuredSales.get(5).getId(), 9);
        assertEquals(featuredSales.get(6).getId(), 10);
        assertEquals(featuredSales.get(7).getId(), 11);
        assertEquals(featuredSales.get(8).getId(), 12);

        featuredSales = PromotionUtils.getFeaturedSales(null);
        assertTrue(featuredSales.isEmpty());
    }

    @Test
    public void testGetFavoriteSales() {
        List<Sale> favoriteSales = PromotionUtils.getFavoritePromotions(sales);
        assertEquals(favoriteSales.size(), 3);
        assertEquals(favoriteSales.get(0).getId(), 1);
        assertEquals(favoriteSales.get(1).getId(), 2);
        assertEquals(favoriteSales.get(2).getId(), 3);

        favoriteSales = PromotionUtils.getFavoritePromotions(null);
        assertTrue(favoriteSales.isEmpty());
    }

    @Test
    public void testGetDistinctFavoriteSales() {
        List<Sale> distinctFavoriteSales = PromotionUtils.getDistinctFavoriteSales(sales);
        assertEquals(distinctFavoriteSales.size(), 2);
        assertEquals(distinctFavoriteSales.get(0).getId(), 1);
        assertEquals(distinctFavoriteSales.get(1).getId(), 2);

        distinctFavoriteSales = PromotionUtils.getDistinctFavoriteSales(null);
        assertTrue(distinctFavoriteSales.isEmpty());
    }

    @Test
    public void testGetSalesByTopRetailer() {
        List<Sale> salesByTopRetailer = PromotionUtils.getSalesByTopRetailer(sales);
        assertEquals(salesByTopRetailer.size(), 8);
        assertEquals(salesByTopRetailer.get(0).getId(), 1);
        assertEquals(salesByTopRetailer.get(1).getId(), 4);
        assertEquals(salesByTopRetailer.get(2).getId(), 5);
        assertEquals(salesByTopRetailer.get(3).getId(), 8);
        assertEquals(salesByTopRetailer.get(4).getId(), 9);
        assertEquals(salesByTopRetailer.get(5).getId(), 10);
        assertEquals(salesByTopRetailer.get(6).getId(), 11);
        assertEquals(salesByTopRetailer.get(7).getId(), 12);

        salesByTopRetailer = PromotionUtils.getSalesByTopRetailer(null);
        assertTrue(salesByTopRetailer.isEmpty());
    }

    @Test
    public void testGetSalesByStoreId() {
        List<Sale> salesByStoreId = PromotionUtils.getSalesByStoreId(-1, sales);
        assertTrue(salesByStoreId.isEmpty());

        salesByStoreId = PromotionUtils.getSalesByStoreId(1, null);
        assertTrue(salesByStoreId.isEmpty());

        salesByStoreId = PromotionUtils.getSalesByStoreId(1, sales);
        assertFalse(salesByStoreId.isEmpty());
        assertEquals(salesByStoreId.size(), 1);
        assertEquals(salesByStoreId.get(0).getId(), 1);

        salesByStoreId = PromotionUtils.getSalesByStoreId(0, sales);
        assertFalse(salesByStoreId.isEmpty());
        assertEquals(salesByStoreId.size(), 2);
        assertEquals(salesByStoreId.get(0).getId(), 13);
        assertEquals(salesByStoreId.get(1).getId(), 14);
    }

    @Test
    public void testGetSalesByStoreName() {
        List<Sale> salesByStoreName = PromotionUtils.getSalesOrderedByStoreName(sales);
        assertEquals(salesByStoreName.size(), sales.size());
        assertEquals(salesByStoreName.get(0).getId(), 15);
        assertEquals(salesByStoreName.get(1).getId(), 1);
        assertEquals(salesByStoreName.get(2).getId(), 11);
        assertEquals(salesByStoreName.get(3).getId(), 12);
        assertEquals(salesByStoreName.get(4).getId(), 3);
        assertEquals(salesByStoreName.get(5).getId(), 2);
        assertEquals(salesByStoreName.get(6).getId(), 5);
        assertEquals(salesByStoreName.get(7).getId(), 7);
        assertEquals(salesByStoreName.get(8).getId(), 6);
        assertEquals(salesByStoreName.get(9).getId(), 8);
        assertEquals(salesByStoreName.get(10).getId(), 10);
        assertEquals(salesByStoreName.get(11).getId(), 9);
        assertEquals(salesByStoreName.get(12).getId(), 4);
        assertEquals(salesByStoreName.get(13).getId(), 13);
        assertEquals(salesByStoreName.get(14).getId(), 14);

        salesByStoreName = PromotionUtils.getSalesOrderedByStoreName(null);
        assertTrue(salesByStoreName.isEmpty());
    }

    @Test
    public void testGetSalesByEndDate() {
        List<Sale> salesByEndDate = PromotionUtils.getSalesByEndDate(sales);
        assertEquals(salesByEndDate.size(), sales.size());
        assertEquals(salesByEndDate.get(0).getId(), 3);
        assertEquals(salesByEndDate.get(1).getId(), 8);
        assertEquals(salesByEndDate.get(2).getId(), 1);
        assertEquals(salesByEndDate.get(3).getId(), 5);
        assertEquals(salesByEndDate.get(4).getId(), 10);
        assertEquals(salesByEndDate.get(5).getId(), 2);
        assertEquals(salesByEndDate.get(6).getId(), 7);
        assertEquals(salesByEndDate.get(7).getId(), 6);
        assertEquals(salesByEndDate.get(8).getId(), 4);
        assertEquals(salesByEndDate.get(9).getId(), 9);
        assertEquals(salesByEndDate.get(10).getId(), 11);
        assertEquals(salesByEndDate.get(11).getId(), 12);
        assertEquals(salesByEndDate.get(12).getId(), 13);
        assertEquals(salesByEndDate.get(13).getId(), 14);
        assertEquals(salesByEndDate.get(14).getId(), 15);

        salesByEndDate = PromotionUtils.getSalesByEndDate(null);
        assertTrue(salesByEndDate.isEmpty());
    }
}