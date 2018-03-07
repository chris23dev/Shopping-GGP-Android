package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.Category;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.TenantCategory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class TenantCategoryUtilsTest extends BaseTest {

    private List<Tenant> tenants;
    private List<Category> categories;

    private final int CATEGORY_ID_SHOPPING = 1;
    private final int CATEGORY_ID_SHOES = 2;
    private final int CATEGORY_ID_CLOTHES = 3;
    private final int CATEGORY_ID_ACCESSORIES = 4;
    private final int CATEGORY_ID_ELECTRONICS = 5;
    private final int CATEGORY_ID_COMPUTERS = 6;
    private final int CATEGORY_ID_TOYS = 7;
    private final int CATEGORY_ID_BABY_TOYS = 8;
    private final int CATEGORY_ID_STORE_OPENING = 9;
    private final String CATEGORY_CODE_SHOPPING = "SHOPPING";
    private final String CATEGORY_CODE_SHOES = "SHOES";
    private final String CATEGORY_CODE_CLOTHES = "CLOTHES";
    private final String CATEGORY_CODE_ACCESSORIES = "ACCESSORIES";
    private final String CATEGORY_CODE_ELECTRONICS = "ELECTRONICS";
    private final String CATEGORY_CODE_COMPUTERS = "COMPUTERS";
    private final String CATEGORY_CODE_TOYS = "TOYS";
    private final String CATEGORY_CODE_BABY_TOYS = "BABY_TOYS";
    private final int TENANT_ID_GAP = 1;
    private final int TENANT_ID_JACKSPADE = 2;
    private final int TENANT_ID_APPLE = 3;
    private final int TENANT_ID_BESTBUY = 4;
    private final String TENANT_NAME_GAP = "Gap";
    private final String TENANT_NAME_JACKSPADE = "Jack Spade";
    private final String TENANT_NAME_APPLE = "Apple";
    private final String TENANT_NAME_BESTBUY = "Best Buy";

    @Before
    public void setup() throws Exception {
        super.setup();
        tenants = setupTenants();
        categories = setupCategories();
    }

    @After
    public void teardown() {
        tenants = null;
        categories = null;
        accountManager.getCurrentUser().getFavorites().clear();
    }

    @Test
    public void testFindTenantCategory() {
        List<TenantCategory> tenantCategories = TenantCategoryUtils.createTenantCategories(tenants, categories);

        assertNull(TenantCategoryUtils.findTenantCategory(tenantCategories, null));
        assertNull(TenantCategoryUtils.findTenantCategory(tenantCategories, "INVALID_CODE"));
        assertEquals(CATEGORY_CODE_SHOPPING, TenantCategoryUtils.findTenantCategory(tenantCategories, CATEGORY_CODE_SHOPPING).getCode());
        assertEquals(CATEGORY_CODE_COMPUTERS, TenantCategoryUtils.findTenantCategory(tenantCategories, CATEGORY_CODE_COMPUTERS).getCode());
    }

    @Test
    public void testCreateTenantCategories() {
        List<TenantCategory> tenantCategories = TenantCategoryUtils.createTenantCategories(tenants, categories);

        TenantCategory allTenants = tenantCategories.get(0);
        TenantCategory favorites = tenantCategories.get(1);
        TenantCategory storeOpenings = tenantCategories.get(2);
        TenantCategory electronics = tenantCategories.get(3);
        TenantCategory shopping = tenantCategories.get(4);

        assertEquals(5, tenantCategories.size());
        assertEquals(CategoryUtils.CATEGORY_ALL_STORES, allTenants.getCode());
        assertEquals(CategoryUtils.CATEGORY_MY_FAVORITES, favorites.getCode());
        assertEquals(CategoryUtils.CATEGORY_STORE_OPENING, storeOpenings.getCode());
        assertEquals(CATEGORY_CODE_ELECTRONICS, electronics.getCode());
        assertEquals(CATEGORY_CODE_SHOPPING, shopping.getCode());

        assertEquals(4, allTenants.getTenants().size());
        assertEquals(1, favorites.getTenants().size());
        assertEquals(2, storeOpenings.getTenants().size());
        assertEquals(0, electronics.getTenants().size());
        assertEquals(1, shopping.getTenants().size());

        assertEquals(0, allTenants.getChildCategories().size());
        assertEquals(0, favorites.getChildCategories().size());
        assertEquals(0, storeOpenings.getChildCategories().size());
        assertEquals(2, electronics.getChildCategories().size());
        assertEquals(3, shopping.getChildCategories().size());

        assertTrue(tenantsContainTenantId(allTenants.getTenants(), TENANT_ID_GAP));
        assertTrue(tenantsContainTenantId(allTenants.getTenants(), TENANT_ID_JACKSPADE));
        assertTrue(tenantsContainTenantId(allTenants.getTenants(), TENANT_ID_APPLE));
        assertTrue(tenantsContainTenantId(allTenants.getTenants(), TENANT_ID_BESTBUY));
        assertTrue(tenantsContainTenantId(favorites.getTenants(), TENANT_ID_APPLE));
        assertTrue(tenantsContainTenantId(storeOpenings.getTenants(), TENANT_ID_GAP));
        assertTrue(tenantsContainTenantId(storeOpenings.getTenants(), TENANT_ID_BESTBUY));
        assertTrue(tenantsContainTenantId(shopping.getTenants(), TENANT_ID_GAP));

        TenantCategory allElectronics = electronics.getChildCategories().get(0);
        TenantCategory computers = electronics.getChildCategories().get(1);
        TenantCategory allShopping = shopping.getChildCategories().get(0);
        TenantCategory accessories = shopping.getChildCategories().get(1);
        TenantCategory clothes = shopping.getChildCategories().get(2);

        assertEquals(2, allElectronics.getTenants().size());
        assertEquals(2, computers.getTenants().size());
        assertEquals(2, allShopping.getTenants().size());
        assertEquals(1, accessories.getTenants().size());
        assertEquals(1, clothes.getTenants().size());

        assertEquals(0, allElectronics.getChildCategories().size());
        assertEquals(0, computers.getChildCategories().size());
        assertEquals(0, allShopping.getChildCategories().size());
        assertEquals(0, accessories.getChildCategories().size());
        assertEquals(0, clothes.getChildCategories().size());

        assertTrue(tenantsContainTenantId(allElectronics.getTenants(), TENANT_ID_APPLE));
        assertTrue(tenantsContainTenantId(allElectronics.getTenants(), TENANT_ID_BESTBUY));
        assertTrue(tenantsContainTenantId(computers.getTenants(), TENANT_ID_APPLE));
        assertTrue(tenantsContainTenantId(computers.getTenants(), TENANT_ID_BESTBUY));
        assertTrue(tenantsContainTenantId(allShopping.getTenants(), TENANT_ID_GAP));
        assertTrue(tenantsContainTenantId(allShopping.getTenants(), TENANT_ID_JACKSPADE));
        assertTrue(tenantsContainTenantId(accessories.getTenants(), TENANT_ID_JACKSPADE));
        assertTrue(tenantsContainTenantId(clothes.getTenants(), TENANT_ID_GAP));
    }

    private List<Tenant> setupTenants() {
        Category storeOpening = createCategory(CATEGORY_ID_STORE_OPENING, 0, CategoryUtils.CATEGORY_STORE_OPENING);

        Category shopping = createCategory(CATEGORY_ID_SHOPPING, 0, CATEGORY_CODE_SHOPPING);
        Category clothes = createCategory(CATEGORY_ID_CLOTHES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_CLOTHES);
        Tenant gap = createTenant(TENANT_ID_GAP, TENANT_NAME_GAP, Arrays.asList(shopping, clothes, storeOpening));

        Category accessories = createCategory(CATEGORY_ID_ACCESSORIES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_ACCESSORIES);
        Tenant jackSpade = createTenant(TENANT_ID_JACKSPADE, TENANT_NAME_JACKSPADE, Arrays.asList(accessories));

        Category computers1 = createCategory(CATEGORY_ID_COMPUTERS, CATEGORY_ID_ELECTRONICS, CATEGORY_CODE_COMPUTERS);
        Tenant apple = createTenant(TENANT_ID_APPLE, TENANT_NAME_APPLE, Arrays.asList(computers1));
        apple.setPlaceWiseRetailerId(TENANT_ID_APPLE);
        accountManager.getCurrentUser().addFavoriteTenant(apple);

        Category computers2 = createCategory(CATEGORY_ID_COMPUTERS, CATEGORY_ID_ELECTRONICS, CATEGORY_CODE_COMPUTERS);
        Tenant bestBuy = createTenant(TENANT_ID_BESTBUY, TENANT_NAME_BESTBUY, Arrays.asList(computers2, storeOpening));

        return Arrays.asList(gap, jackSpade, apple, bestBuy);
    }

    private List<Category> setupCategories() {
        Category storeOpening = createCategory(CATEGORY_ID_STORE_OPENING, 0, CategoryUtils.CATEGORY_STORE_OPENING);
        Category shopping = createCategory(CATEGORY_ID_SHOPPING, 0, CATEGORY_CODE_SHOPPING);
        Category shoes = createCategory(CATEGORY_ID_SHOES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_SHOES);
        Category clothes = createCategory(CATEGORY_ID_CLOTHES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_CLOTHES);
        Category accessories = createCategory(CATEGORY_ID_ACCESSORIES, CATEGORY_ID_SHOPPING, CATEGORY_CODE_ACCESSORIES);
        Category electronics = createCategory(CATEGORY_ID_ELECTRONICS, 0, CATEGORY_CODE_ELECTRONICS);
        Category computers = createCategory(CATEGORY_ID_COMPUTERS, CATEGORY_ID_ELECTRONICS, CATEGORY_CODE_COMPUTERS);
        Category toys = createCategory(CATEGORY_ID_TOYS, 0, CATEGORY_CODE_TOYS);
        Category babyToys = createCategory(CATEGORY_ID_BABY_TOYS, CATEGORY_ID_TOYS, CATEGORY_CODE_BABY_TOYS);

        return Arrays.asList(storeOpening, shopping, shoes, clothes, accessories, electronics, computers, toys, babyToys);
    }

    private Tenant createTenant(int tenantId, String name, List<Category> categories) {
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setName(name);
        tenant.setCategories(categories);

        return tenant;
    }

    private Category createCategory(int id, int parentId, String code) {
        Category category = new Category();
        category.setId(id);
        category.setParentId(parentId);
        category.setCode(code);
        category.setLabel(code.toLowerCase());

        return category;
    }

    private boolean tenantsContainTenantId (List<Tenant> tenants, int tenantId) {
        return java8.util.stream.StreamSupport.stream(tenants).anyMatch(t -> t.getId() == tenantId);
    }
}
