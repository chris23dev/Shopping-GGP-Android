package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.Category;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.SaleCategory;
import com.ggp.theclub.model.Tenant;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class SaleCategoryUtilsTest extends BaseTest {
    private String salesJson;
    private List<Sale> sales;
    private List<Category> categories;
    private List<Category> campaignCategories;

    private final int CATEGORY_ID_SHOPPING = 1;
    private final int CATEGORY_ID_SHOES = 2;
    private final int CATEGORY_ID_CLOTHES = 3;
    private final int CATEGORY_ID_ACCESSORIES = 4;
    private final int CATEGORY_ID_ELECTRONICS = 5;
    private final int CATEGORY_ID_COMPUTERS = 6;
    private final int CATEGORY_ID_TOYS = 7;
    private final int CATEGORY_ID_BABY_TOYS = 8;
    private final int CATEGORY_ID_VALENTINE = 9;
    private final String CATEGORY_CODE_FASHION = "FASHION";
    private final String CATEGORY_CODE_SHOPPING = "SHOPPING";
    private final String CATEGORY_CODE_SHOES = "SHOES";
    private final String CATEGORY_CODE_CLOTHES = "CLOTHES";
    private final String CATEGORY_CODE_ACCESSORIES = "ACCESSORIES";
    private final String CATEGORY_CODE_ELECTRONICS = "ELECTRONICS";
    private final String CATEGORY_CODE_COMPUTERS = "COMPUTERS";
    private final String CATEGORY_CODE_TOYS = "TOYS";
    private final String CATEGORY_CODE_BABY_TOYS = "BABY_TOYS";
    private final String CATEGORY_CODE_VALENTINES = "VALENTINES";
    private final int SALE_ID_SHOPPING_CLOTHES = 1;
    private final int SALE_ID_ACCESSORIES = 2;
    private final int SALE_ID_COMPUTER1 = 3;
    private final int SALE_ID_COMPUTER2 = 4;

    @Before
    public void setup() throws Exception {
        super.setup();
        salesJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("resources/sales_data.json"));
        sales = gson.fromJson(salesJson, new TypeToken<List<Sale>>() {}.getType());
        categories = StreamSupport.stream(sales).flatMap(sale -> StreamSupport.stream(sale.getCategories())).distinct().collect(Collectors.toList());
    }

    @After
    public void teardown() {
        salesJson = null;
        sales = null;
        categories = null;
    }

    @Test
    public void testCreateSaleCategories() {
        sales = setupSales();
        categories = setupCategories();
        campaignCategories = setupCampaignCategories();

        List<SaleCategory> saleCategories = SaleCategoryUtils.createSaleCategories(sales, categories, campaignCategories);

        SaleCategory allSales = saleCategories.get(0);
        SaleCategory electronics = saleCategories.get(1);
        SaleCategory shopping = saleCategories.get(2);
        SaleCategory campaign = saleCategories.get(3);

        SaleCategory allElectronics = electronics.getChildCategories().get(0);
        SaleCategory computers = electronics.getChildCategories().get(1);

        SaleCategory allShopping = shopping.getChildCategories().get(0);
        SaleCategory accessories = shopping.getChildCategories().get(1);
        SaleCategory clothes = shopping.getChildCategories().get(2);

        assertEquals(4, saleCategories.size());
        assertEquals(CategoryUtils.CATEGORY_ALL_SALES, allSales.getCode());
        assertEquals(4, allSales.getSales().size());
        assertEquals(0, allSales.getChildCategories().size());
        assertTrue(salesContainSaleId(allSales.getSales(), SALE_ID_SHOPPING_CLOTHES));
        assertTrue(salesContainSaleId(allSales.getSales(), SALE_ID_ACCESSORIES));
        assertTrue(salesContainSaleId(allSales.getSales(), SALE_ID_COMPUTER1));
        assertTrue(salesContainSaleId(allSales.getSales(), SALE_ID_COMPUTER2));

        assertEquals(CATEGORY_CODE_ELECTRONICS, electronics.getCode());
        assertEquals(CategoryUtils.CATEGORY_CODE_ALL_PREFIX.concat(CATEGORY_CODE_ELECTRONICS), allElectronics.getCode());
        assertEquals(CATEGORY_CODE_COMPUTERS, computers.getCode());
        assertEquals(0, electronics.getSales().size());
        assertEquals(2, electronics.getChildCategories().size());
        assertEquals(2, allElectronics.getSales().size());
        assertEquals(0, allElectronics.getChildCategories().size());
        assertEquals(2, computers.getSales().size());
        assertEquals(0, computers.getChildCategories().size());
        assertTrue(salesContainSaleId(allElectronics.getSales(), SALE_ID_COMPUTER1));
        assertTrue(salesContainSaleId(allElectronics.getSales(), SALE_ID_COMPUTER2));
        assertTrue(salesContainSaleId(computers.getSales(), SALE_ID_COMPUTER1));
        assertTrue(salesContainSaleId(computers.getSales(), SALE_ID_COMPUTER2));

        assertEquals(CATEGORY_CODE_SHOPPING, shopping.getCode());
        assertEquals(CategoryUtils.CATEGORY_CODE_ALL_PREFIX.concat(CATEGORY_CODE_SHOPPING), allShopping.getCode());
        assertEquals(CATEGORY_CODE_ACCESSORIES, accessories.getCode());
        assertEquals(CATEGORY_CODE_CLOTHES, clothes.getCode());
        assertEquals(1, shopping.getSales().size());
        assertEquals(3, shopping.getChildCategories().size());
        assertEquals(2, allShopping.getSales().size());
        assertEquals(0, allShopping.getChildCategories().size());
        assertEquals(1, clothes.getSales().size());
        assertEquals(0, clothes.getChildCategories().size());
        assertEquals(1, accessories.getSales().size());
        assertEquals(0, accessories.getChildCategories().size());


        assertEquals(CategoryUtils.CATEGORY_VALENTINES, campaign.getCode());
        assertEquals(1, campaign.getSales().size());
    }

    @Test
    public void testGetSaleCategoryByCode() {
        List<SaleCategory> saleCategories = SaleCategoryUtils.createSaleCategories(sales, categories, null);

        assertNull(SaleCategoryUtils.getSaleCategoryByCode(null, saleCategories));
        assertNull(SaleCategoryUtils.getSaleCategoryByCode("INVALID_CODE", saleCategories));
        assertEquals(CATEGORY_CODE_FASHION, SaleCategoryUtils.getSaleCategoryByCode(CATEGORY_CODE_FASHION, saleCategories).getCode());
        assertEquals(CATEGORY_CODE_SHOES, SaleCategoryUtils.getSaleCategoryByCode(CATEGORY_CODE_SHOES, saleCategories).getCode());
    }

    @Test
    public void testGetTenantsFromSaleCategory() throws Exception {
        List<Tenant> tenants = SaleCategoryUtils.getTenantsFromSaleCategory(null);
        assertTrue(tenants.isEmpty());

        List<SaleCategory> saleCategories = SaleCategoryUtils.createSaleCategories(sales, categories, campaignCategories);
        tenants = SaleCategoryUtils.getTenantsFromSaleCategory(saleCategories.get(0));
        assertEquals(tenants.size(), 10);
    }

    @Test
    public void testGetTenantsFromSaleCategoryWithIds() throws Exception {
        List<Tenant> tenants = SaleCategoryUtils.getTenantsFromSaleCategoryWithIds(null, null);
        assertTrue(tenants.isEmpty());

        List<SaleCategory> saleCategories = SaleCategoryUtils.createSaleCategories(sales, categories, campaignCategories);
        Set<Integer> tenantIds = StreamSupport.stream(Arrays.asList(1, 2, 4, 5)).collect(Collectors.toSet());
        tenants = SaleCategoryUtils.getTenantsFromSaleCategoryWithIds(tenantIds, saleCategories.get(0));
        assertEquals(tenants.size(), 4);
        assertEquals(tenants.get(0).getId(), 1);
        assertEquals(tenants.get(1).getId(), 2);
        assertEquals(tenants.get(2).getId(), 5);
        assertEquals(tenants.get(3).getId(), 4);
    }

    private List<Sale> setupSales() {
        Category shopping = createCategory(CATEGORY_ID_SHOPPING, 0, CATEGORY_CODE_SHOPPING);
        Category clothes = createCategory(CATEGORY_ID_CLOTHES, 0, CATEGORY_CODE_CLOTHES);
        Sale shoppingAndClothesSale = createSale(SALE_ID_SHOPPING_CLOTHES, Arrays.asList(shopping, clothes));

        Category accessories = createCategory(CATEGORY_ID_ACCESSORIES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_ACCESSORIES);
        Sale accessoriesSale = createSale(SALE_ID_ACCESSORIES, Arrays.asList(accessories));

        Category computer1 = createCategory(CATEGORY_ID_COMPUTERS, CATEGORY_ID_ELECTRONICS, CATEGORY_CODE_COMPUTERS);
        Sale computer1Sale = createSale(SALE_ID_COMPUTER1, Arrays.asList(computer1));

        Category computer2 = createCategory(CATEGORY_ID_COMPUTERS, CATEGORY_ID_ELECTRONICS, CATEGORY_CODE_COMPUTERS);
        Sale computer2Sale = createSale(SALE_ID_COMPUTER2, Arrays.asList(computer2));

        Category valentineCategory = createCategory(CATEGORY_ID_VALENTINE, 0, CATEGORY_CODE_VALENTINES);
        computer2Sale.setCampaignCategories(Arrays.asList(valentineCategory));

        return Arrays.asList(shoppingAndClothesSale, accessoriesSale, computer1Sale, computer2Sale);
    }

    private List<Category> setupCategories() {
        Category shopping = createCategory(CATEGORY_ID_SHOPPING, 0, CATEGORY_CODE_SHOPPING);
        Category shoes = createCategory(CATEGORY_ID_SHOES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_SHOES);
        Category clothes = createCategory(CATEGORY_ID_CLOTHES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_CLOTHES);
        Category accessories = createCategory(CATEGORY_ID_ACCESSORIES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_ACCESSORIES);
        Category electronics = createCategory(CATEGORY_ID_ELECTRONICS, 0, CATEGORY_CODE_ELECTRONICS);
        Category computers = createCategory(CATEGORY_ID_COMPUTERS, CATEGORY_ID_ELECTRONICS, CATEGORY_CODE_COMPUTERS);
        Category toys = createCategory(CATEGORY_ID_TOYS, 0, CATEGORY_CODE_TOYS);
        Category babyToys = createCategory(CATEGORY_ID_BABY_TOYS, CATEGORY_ID_TOYS, CATEGORY_CODE_BABY_TOYS);

        return Arrays.asList(shopping, shoes, clothes, accessories, electronics, computers, toys, babyToys);
    }

    private List<Category> setupCampaignCategories() {
        Category valentine = createCategory(CATEGORY_ID_SHOPPING, 0, CATEGORY_CODE_VALENTINES);
        return Arrays.asList(valentine);
    }

    private Category createCategory(int id, int parentId, String code) {
        Category category = new Category();
        category.setId(id);
        category.setParentId(parentId);
        category.setCode(code);
        category.setLabel(code.toLowerCase());

        return category;
    }

    private Sale createSale(int saleId, List<Category> categories) {
        Sale sale = new Sale();
        sale.setId(saleId);
        sale.setCategories(categories);

        return sale;
    }

    private boolean salesContainSaleId (List<Sale> sales, int saleId) {
        return StreamSupport.stream(sales).anyMatch(s -> s.getId() == saleId);
    }
}