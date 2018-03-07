package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.FilterProductType;
import com.ggp.theclub.model.Tenant;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import java8.util.Optional;

import static org.junit.Assert.assertEquals;

public class ProductUtilsTest extends BaseTest {
    private List<Tenant> tenants = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        super.setup();
        String json = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("resources/tenants_data.json"));
        tenants = gson.fromJson(json, new TypeToken<ArrayList<Tenant>>() {}.getType());
    }

    @After
    public void tearDown() {
        tenants.clear();
    }

    @Test
    public void testGetProductTypeFilterLabel() {
        FilterProductType clothes = new FilterProductType("womens-shoes", "Women's Shoes");
        FilterProductType booties = new FilterProductType("womens-shoes/booties", "Booties");

        assertEquals(ProductUtils.getProductTypeFilterLabel(Optional.ofNullable(clothes), booties), "Women's Shoes / Booties");
        assertEquals(ProductUtils.getProductTypeFilterLabel(Optional.ofNullable(null), booties), "Booties");
    }

    /**TODO make great again
    @Test
    public void testGetStoreProductTypeList() {
        List<FilterProductType> filterProductTypes = ProductUtils.getStoreProductTypeList(tenants.get(9).getProductTypes());
        assertFalse(filterProductTypes.isEmpty());
        assertEquals(filterProductTypes.size(), 13);

        List<Integer> childProductTypeSizes = Arrays.asList(12, 8, 12, 7, 10, 9, 11, 5, 6, 7, 10, 4, 6);
        AtomicInteger index = new AtomicInteger(0);
        StreamSupport.stream(filterProductTypes).forEach(filterProductType -> {
            assertEquals(filterProductType.getChildren().size(), childProductTypeSizes.get(index.get()).intValue());
            index.getAndIncrement();
        });

        List<String> parentProductTypeCodes = Arrays.asList("womens-clothing", "womens-shoes", "mens-clothing", "mens-shoes", "girls-clothing", "boys-clothing", "home-bed-bath", "jewelry", "health-beauty", "electronics", "sports", "toys-games", "gourmet-foods");
        List<String> childProductTypeCodes0 = Arrays.asList("womens-clothing/bras-panties-lingerie", "womens-clothing/dresses", "womens-clothing/handbags-purses", "womens-clothing/jackets-outerwear", "womens-clothing/pants", "womens-clothing/shirts-tops", "womens-clothing/shorts", "womens-clothing/socks", "womens-clothing/sunglasses", "womens-clothing/sweaters", "womens-clothing/swimwear", "womens-clothing/tops");
        List<String> childProductTypeCodes1 = Arrays.asList("womens-shoes/booties", "womens-shoes/boots", "womens-shoes/flats", "womens-shoes/heels", "womens-shoes/sandals", "womens-shoes/slippers", "womens-shoes/sneakers-athletic", "womens-shoes/winter-rain-boots");
        List<String> childProductTypeCodes2 = Arrays.asList("mens-clothing/belts", "mens-clothing/jackets-outerwear", "mens-clothing/pants", "mens-clothing/shirts", "mens-clothing/shorts", "mens-clothing/socks", "mens-clothing/suits", "mens-clothing/sunglasses", "mens-clothing/sweaters", "mens-clothing/swimwear", "mens-clothing/ties", "mens-clothing/underwear");
        List<String> childProductTypeCodes3 = Arrays.asList("mens-shoes/boat-shoes", "mens-shoes/boots", "mens-shoes/dress-shoes", "mens-shoes/loafers", "mens-shoes/sandals", "mens-shoes/slippers", "mens-shoes/sneakers-athletic", "mens-shoes/winter-rain-boots");
        List<String> childProductTypeCodes4 = Arrays.asList("girls-clothing/baby", "girls-clothing/dresses", "girls-clothing/jackets-outerwear", "girls-clothing/pants", "girls-clothing/shirts-tops", "girls-clothing/shorts", "girls-clothing/socks", "girls-clothing/sweaters", "girls-clothing/swimwear", "girls-clothing/underwear");
        List<String> childProductTypeCodes5 = Arrays.asList("boys-clothing/baby", "boys-clothing/jackets-outerwear", "boys-clothing/pants", "boys-clothing/shirts", "boys-clothing/shorts", "boys-clothing/socks", "boys-clothing/sweaters", "boys-clothing/swimwear", "boys-clothing/underwear");
        List<String> childProductTypeCodes6 = Arrays.asList("home-bed-bath/appliances", "home-bed-bath/bedding", "home-bed-bath/beds", "home-bed-bath/blankets", "home-bed-bath/candles", "home-bed-bath/kitchen", "home-bed-bath/lamps", "home-bed-bath/linen", "home-bed-bath/storage", "home-bed-bath/tables", "home-bed-bath/towels");
        List<String> childProductTypeCodes7 = Arrays.asList("jewelry/earrings", "jewelry/necklaces", "jewelry/pendants", "jewelry/rings", "jewelry/watches");
        List<String> childProductTypeCodes8 = Arrays.asList("health-beauty/fragrances-perfume", "health-beauty/hair-care", "health-beauty/lotions", "health-beauty/makeup", "health-beauty/nail-care", "health-beauty/supplements");
        List<String> childProductTypeCodes9 = Arrays.asList("electronics/computers", "electronics/games-game-consoles", "electronics/smartphones", "electronics/sound-systems-receivers", "electronics/tablets", "electronics/tech-gadgets", "electronics/televisions");
        List<String> childProductTypeCodes10 = Arrays.asList("sports/baseball", "sports/basketball", "sports/biking", "sports/fishing", "sports/football", "sports/golf", "sports/hunting", "sports/outdoors", "sports/soccer", "sports/swimming");
        List<String> childProductTypeCodes11 = Arrays.asList("toys-games/action-figures", "toys-games/board-games", "toys-games/dolls", "toys-games/puzzles");
        List<String> childProductTypeCodes12 = Arrays.asList("gourmet-foods/candy", "gourmet-foods/chocolate", "gourmet-foods/coffee", "gourmet-foods/snacks", "gourmet-foods/spices", "gourmet-foods/teas");

        testProductTypeCodeEquality(filterProductTypes, parentProductTypeCodes);
        testProductTypeCodeEquality(filterProductTypes.get(0).getChildren(), childProductTypeCodes0);
        testProductTypeCodeEquality(filterProductTypes.get(1).getChildren(), childProductTypeCodes1);
        testProductTypeCodeEquality(filterProductTypes.get(2).getChildren(), childProductTypeCodes2);
        testProductTypeCodeEquality(filterProductTypes.get(3).getChildren(), childProductTypeCodes3);
        testProductTypeCodeEquality(filterProductTypes.get(4).getChildren(), childProductTypeCodes4);
        testProductTypeCodeEquality(filterProductTypes.get(5).getChildren(), childProductTypeCodes5);
        testProductTypeCodeEquality(filterProductTypes.get(6).getChildren(), childProductTypeCodes6);
        testProductTypeCodeEquality(filterProductTypes.get(7).getChildren(), childProductTypeCodes7);
        testProductTypeCodeEquality(filterProductTypes.get(8).getChildren(), childProductTypeCodes8);
        testProductTypeCodeEquality(filterProductTypes.get(9).getChildren(), childProductTypeCodes9);
        testProductTypeCodeEquality(filterProductTypes.get(10).getChildren(), childProductTypeCodes10);
        testProductTypeCodeEquality(filterProductTypes.get(11).getChildren(), childProductTypeCodes11);
        testProductTypeCodeEquality(filterProductTypes.get(12).getChildren(), childProductTypeCodes12);
    }

    @Test
    public void testGetStoreProductTypeTree() {
        List<FilterProductType> filterProductTypes = ProductUtils.getStoreProductTypeTree(tenants).getChildren();
        assertFalse(filterProductTypes.isEmpty());
        assertEquals(filterProductTypes.size(), 14);

        List<Integer> childProductTypeSizes = Arrays.asList(0, 13, 9, 13, 8, 11, 10, 6, 12, 7, 8, 11, 5, 7);
        AtomicInteger index = new AtomicInteger(0);
        StreamSupport.stream(filterProductTypes).forEach(filterProductType -> {
            assertEquals(filterProductType.getChildren().size(), childProductTypeSizes.get(index.get()).intValue());
            index.getAndIncrement();
        });

        List<Integer> parentProductTypeCounts = Arrays.asList(25, 2, 3, 2, 3, 2, 3, 3, 1, 1, 1, 1, 1, 2);
        List<Integer> childProductTypeCounts1 = Arrays.asList(2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
        List<Integer> childProductTypeCounts2 = Arrays.asList(3, 1, 3, 3, 2, 2, 1, 2, 1);
        List<Integer> childProductTypeCounts3 = Arrays.asList(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
        List<Integer> childProductTypeCounts4 = Arrays.asList(3, 1, 3, 2, 3, 2, 1, 2);
        List<Integer> childProductTypeCounts5 = Arrays.asList(2, 3, 2, 2, 2, 2, 2, 1, 1, 2, 1);
        List<Integer> childProductTypeCounts6 = Arrays.asList(3, 3, 2, 2, 2, 2, 1, 1, 1, 1);
        List<Integer> childProductTypeCounts7 = Arrays.asList(3, 3, 3, 3, 3, 3);
        List<Integer> childProductTypeCounts8 = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        List<Integer> childProductTypeCounts9 = Arrays.asList(1, 2, 1, 1, 1, 1, 1);
        List<Integer> childProductTypeCounts10 = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1);
        List<Integer> childProductTypeCounts11 = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        List<Integer> childProductTypeCounts12 = Arrays.asList(1, 1, 1, 1, 1);
        List<Integer> childProductTypeCounts13 = Arrays.asList(2, 2, 2, 2, 2, 2, 2);

        testProductTypeCountEquality(filterProductTypes, parentProductTypeCounts);
        testProductTypeCountEquality(filterProductTypes.get(1).getChildren(), childProductTypeCounts1);
        testProductTypeCountEquality(filterProductTypes.get(2).getChildren(), childProductTypeCounts2);
        testProductTypeCountEquality(filterProductTypes.get(3).getChildren(), childProductTypeCounts3);
        testProductTypeCountEquality(filterProductTypes.get(4).getChildren(), childProductTypeCounts4);
        testProductTypeCountEquality(filterProductTypes.get(5).getChildren(), childProductTypeCounts5);
        testProductTypeCountEquality(filterProductTypes.get(6).getChildren(), childProductTypeCounts6);
        testProductTypeCountEquality(filterProductTypes.get(7).getChildren(), childProductTypeCounts7);
        testProductTypeCountEquality(filterProductTypes.get(8).getChildren(), childProductTypeCounts8);
        testProductTypeCountEquality(filterProductTypes.get(9).getChildren(), childProductTypeCounts9);
        testProductTypeCountEquality(filterProductTypes.get(10).getChildren(), childProductTypeCounts10);
        testProductTypeCountEquality(filterProductTypes.get(11).getChildren(), childProductTypeCounts11);
        testProductTypeCountEquality(filterProductTypes.get(12).getChildren(), childProductTypeCounts12);
        testProductTypeCountEquality(filterProductTypes.get(13).getChildren(), childProductTypeCounts13);

        List<String> parentProductTypeCodes = Arrays.asList(ProductUtils.PRODUCT_TYPE_ALL_STORES, "womens-clothing", "womens-shoes", "mens-clothing", "mens-shoes", "girls-clothing", "boys-clothing", "jewelry", "home-bed-bath", "health-beauty", "electronics", "sports", "toys-games", "gourmet-foods");
        List<String> childProductTypeCodes1 = Arrays.asList("womens-clothing", "womens-clothing/bras-panties-lingerie", "womens-clothing/dresses", "womens-clothing/handbags-purses", "womens-clothing/jackets-outerwear", "womens-clothing/pants", "womens-clothing/shirts-tops", "womens-clothing/shorts", "womens-clothing/socks", "womens-clothing/sunglasses", "womens-clothing/sweaters", "womens-clothing/swimwear", "womens-clothing/tops");
        List<String> childProductTypeCodes2 = Arrays.asList("womens-shoes", "womens-shoes/booties", "womens-shoes/boots", "womens-shoes/flats", "womens-shoes/heels", "womens-shoes/sandals", "womens-shoes/slippers", "womens-shoes/sneakers-athletic", "womens-shoes/winter-rain-boots");
        List<String> childProductTypeCodes3 = Arrays.asList("mens-clothing", "mens-clothing/belts", "mens-clothing/jackets-outerwear", "mens-clothing/pants", "mens-clothing/shirts", "mens-clothing/shorts", "mens-clothing/socks", "mens-clothing/suits", "mens-clothing/sunglasses", "mens-clothing/sweaters", "mens-clothing/swimwear", "mens-clothing/ties", "mens-clothing/underwear");
        List<String> childProductTypeCodes4 = Arrays.asList("mens-shoes", "mens-shoes/boat-shoes", "mens-shoes/boots", "mens-shoes/dress-shoes", "mens-shoes/loafers", "mens-shoes/sandals", "mens-shoes/slippers", "mens-shoes/sneakers-athletic", "mens-shoes/winter-rain-boots");
        List<String> childProductTypeCodes5 = Arrays.asList("girls-clothing", "girls-clothing/baby", "girls-clothing/dresses", "girls-clothing/jackets-outerwear", "girls-clothing/pants", "girls-clothing/shirts-tops", "girls-clothing/shorts", "girls-clothing/socks", "girls-clothing/sweaters", "girls-clothing/swimwear", "girls-clothing/underwear");
        List<String> childProductTypeCodes6 = Arrays.asList("boys-clothing", "boys-clothing/baby", "boys-clothing/jackets-outerwear", "boys-clothing/pants", "boys-clothing/shirts", "boys-clothing/shorts", "boys-clothing/socks", "boys-clothing/sweaters", "boys-clothing/swimwear", "boys-clothing/underwear");
        List<String> childProductTypeCodes7 = Arrays.asList("jewelry", "jewelry/earrings", "jewelry/necklaces", "jewelry/pendants", "jewelry/rings", "jewelry/watches");
        List<String> childProductTypeCodes8 = Arrays.asList("home-bed-bath", "home-bed-bath/appliances", "home-bed-bath/bedding", "home-bed-bath/beds", "home-bed-bath/blankets", "home-bed-bath/candles", "home-bed-bath/kitchen", "home-bed-bath/lamps", "home-bed-bath/linen", "home-bed-bath/storage", "home-bed-bath/tables", "home-bed-bath/towels");
        List<String> childProductTypeCodes9 = Arrays.asList("health-beauty", "health-beauty/fragrances-perfume", "health-beauty/hair-care", "health-beauty/lotions", "health-beauty/makeup", "health-beauty/nail-care", "health-beauty/supplements");
        List<String> childProductTypeCodes10 = Arrays.asList("electronics", "electronics/computers", "electronics/games-game-consoles", "electronics/smartphones", "electronics/sound-systems-receivers", "electronics/tablets", "electronics/tech-gadgets", "electronics/televisions");
        List<String> childProductTypeCodes11 = Arrays.asList("sports", "sports/baseball", "sports/basketball", "sports/biking", "sports/fishing", "sports/football", "sports/golf", "sports/hunting", "sports/outdoors", "sports/soccer", "sports/swimming");
        List<String> childProductTypeCodes12 = Arrays.asList("toys-games", "toys-games/action-figures", "toys-games/board-games", "toys-games/dolls", "toys-games/puzzles");
        List<String> childProductTypeCodes13 = Arrays.asList("gourmet-foods", "gourmet-foods/candy", "gourmet-foods/chocolate", "gourmet-foods/coffee", "gourmet-foods/snacks", "gourmet-foods/spices", "gourmet-foods/teas");

        testProductTypeCodeEquality(filterProductTypes, parentProductTypeCodes);
        testProductTypeCodeEquality(filterProductTypes.get(1).getChildren(), childProductTypeCodes1);
        testProductTypeCodeEquality(filterProductTypes.get(2).getChildren(), childProductTypeCodes2);
        testProductTypeCodeEquality(filterProductTypes.get(3).getChildren(), childProductTypeCodes3);
        testProductTypeCodeEquality(filterProductTypes.get(4).getChildren(), childProductTypeCodes4);
        testProductTypeCodeEquality(filterProductTypes.get(5).getChildren(), childProductTypeCodes5);
        testProductTypeCodeEquality(filterProductTypes.get(6).getChildren(), childProductTypeCodes6);
        testProductTypeCodeEquality(filterProductTypes.get(7).getChildren(), childProductTypeCodes7);
        testProductTypeCodeEquality(filterProductTypes.get(8).getChildren(), childProductTypeCodes8);
        testProductTypeCodeEquality(filterProductTypes.get(9).getChildren(), childProductTypeCodes9);
        testProductTypeCodeEquality(filterProductTypes.get(10).getChildren(), childProductTypeCodes10);
        testProductTypeCodeEquality(filterProductTypes.get(11).getChildren(), childProductTypeCodes11);
        testProductTypeCodeEquality(filterProductTypes.get(12).getChildren(), childProductTypeCodes12);
        testProductTypeCodeEquality(filterProductTypes.get(13).getChildren(), childProductTypeCodes13);

        assertTrue(filterProductTypes.get(0).getDescription().equals(ProductUtils.getDisplayNameByProductTypeCode(ProductUtils.PRODUCT_TYPE_ALL_STORES)));
        testProductTypeDescriptionEquality(filterProductTypes.get(1).getChildren(), childProductTypeCodes1);
        testProductTypeDescriptionEquality(filterProductTypes.get(2).getChildren(), childProductTypeCodes2);
        testProductTypeDescriptionEquality(filterProductTypes.get(3).getChildren(), childProductTypeCodes3);
        testProductTypeDescriptionEquality(filterProductTypes.get(4).getChildren(), childProductTypeCodes4);
        testProductTypeDescriptionEquality(filterProductTypes.get(5).getChildren(), childProductTypeCodes5);
        testProductTypeDescriptionEquality(filterProductTypes.get(6).getChildren(), childProductTypeCodes6);
        testProductTypeDescriptionEquality(filterProductTypes.get(7).getChildren(), childProductTypeCodes7);
        testProductTypeDescriptionEquality(filterProductTypes.get(8).getChildren(), childProductTypeCodes8);
        testProductTypeDescriptionEquality(filterProductTypes.get(9).getChildren(), childProductTypeCodes9);
        testProductTypeDescriptionEquality(filterProductTypes.get(10).getChildren(), childProductTypeCodes10);
        testProductTypeDescriptionEquality(filterProductTypes.get(11).getChildren(), childProductTypeCodes11);
        testProductTypeDescriptionEquality(filterProductTypes.get(12).getChildren(), childProductTypeCodes12);
        testProductTypeDescriptionEquality(filterProductTypes.get(13).getChildren(), childProductTypeCodes13);
    }

    @Test
    public void testGetProductTypesFromStores() {
        Set<ProductType> productTypes = ProductUtils.getProductTypesFromStores(tenants);
        assertFalse(productTypes.isEmpty());
        assertEquals(productTypes.size(), 120);
    }

    @Test
    public void testGetDisplayNameByProductTypeCode() {
        String displayName = ProductUtils.getDisplayNameByProductTypeCode(ProductUtils.PRODUCT_TYPE_ALL_STORES);
        assertEquals(displayName, "All Stores");

        displayName = ProductUtils.getDisplayNameByProductTypeCode("womens-clothing");
        assertEquals(displayName, "Women's Clothing");

        displayName = ProductUtils.getDisplayNameByProductTypeCode("electronics/dvd-blu-ray");
        assertEquals(displayName, "DVD &amp; Blu-Ray");
    }

    @Test
    public void testGetFullDisplayNameByProductTypeCode() {
        String fullDisplayName = ProductUtils.getFullDisplayNameByProductTypeCode(ProductUtils.PRODUCT_TYPE_ALL_STORES);
        assertEquals(fullDisplayName, "All Stores");

        fullDisplayName = ProductUtils.getFullDisplayNameByProductTypeCode("womens-clothing");
        assertEquals(fullDisplayName, "Women's Clothing");

        fullDisplayName = ProductUtils.getFullDisplayNameByProductTypeCode("electronics/dvd-blu-ray");
        assertEquals(fullDisplayName, "Electronics / DVD &amp; Blu-Ray");
    }

    @Test
    public void testStoreMatchesProductTypeCode() {
        assertTrue(ProductUtils.storeMatchesProductTypeCode(null, tenants.get(0)));
        assertTrue(ProductUtils.storeMatchesProductTypeCode(ProductUtils.PRODUCT_TYPE_ALL_STORES, tenants.get(0)));

        assertTrue(ProductUtils.storeMatchesProductTypeCode("mens-shoes/loafers", tenants.get(0)));
        assertFalse(ProductUtils.storeMatchesProductTypeCode("womens-shoes/heels", tenants.get(0)));

        assertFalse(ProductUtils.storeMatchesProductTypeCode("electronics", tenants.get(1)));
        assertFalse(ProductUtils.storeMatchesProductTypeCode("electronics", tenants.get(11)));
    }

	private void testProductTypeCountEquality(List<FilterProductType> filterProductTypes, List<Integer> productTypeCounts) {
        AtomicInteger index = new AtomicInteger(0);
        StreamSupport.stream(filterProductTypes).forEach(filterProductType -> {
            assertEquals(filterProductType.getCount(), productTypeCounts.get(index.get()).intValue());
            index.getAndIncrement();
        });
	}

    private void testProductTypeCodeEquality(List<FilterProductType> filterProductTypes, List<String> productTypeCodes) {
        AtomicInteger index = new AtomicInteger(0);
        StreamSupport.stream(filterProductTypes).forEach(filterProductType -> {
            assertEquals(filterProductType.getCode(), productTypeCodes.get(index.get()));
            index.getAndIncrement();
        });
    }

    private void testProductTypeDescriptionEquality(List<FilterProductType> filterProductTypes, List<String> productTypeCodes) {
        AtomicInteger index = new AtomicInteger(0);
        StreamSupport.stream(filterProductTypes).forEach(filterProductType -> {
            String displayName = ProductUtils.getDisplayNameByProductTypeCode(productTypeCodes.get(index.get()));
            assertEquals(filterProductType.getDescription(), index.intValue() == 0 ? ProductUtils.PRODUCT_TYPE_ALL_PREFIX.concat(displayName) : displayName);
            index.getAndIncrement();
        });
    }
    **/
}