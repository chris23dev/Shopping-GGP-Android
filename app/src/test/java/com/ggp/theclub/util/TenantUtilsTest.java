package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.comparator.TenantNameComparator;
import com.ggp.theclub.model.Tenant;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TenantUtilsTest extends BaseTest {
    private List<Tenant> tenants = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        super.setup();
        String json = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("resources/tenants_data.json"));
        tenants = gson.fromJson(json, new TypeToken<ArrayList<Tenant>>() {}.getType());
        accountManager.getCurrentUser().addFavoriteTenant(tenants.get(0));
        accountManager.getCurrentUser().addFavoriteTenant(tenants.get(1));
        accountManager.getCurrentUser().addFavoriteTenant(tenants.get(2));
    }

    @After
    public void tearDown() {
        tenants.clear();
        accountManager.getCurrentUser().getFavorites().clear();
    }

    @Test
    public void testGetSortedTenants() {
        List<Tenant> sortedTenants = TenantUtils.getSortedTenants(tenants, new TenantNameComparator());
        assertEquals(sortedTenants.size(), 12);
        assertEquals(sortedTenants.get(0).getId(), 1);
        assertEquals(sortedTenants.get(1).getId(), 11);
        assertEquals(sortedTenants.get(2).getId(), 12);
        assertEquals(sortedTenants.get(3).getId(), 2);
        assertEquals(sortedTenants.get(4).getId(), 3);
        assertEquals(sortedTenants.get(5).getId(), 5);
        assertEquals(sortedTenants.get(6).getId(), 6);
        assertEquals(sortedTenants.get(7).getId(), 8);
        assertEquals(sortedTenants.get(8).getId(), 10);
        assertEquals(sortedTenants.get(9).getId(), 7);
        assertEquals(sortedTenants.get(10).getId(), 9);
        assertEquals(sortedTenants.get(11).getId(), 4);

        sortedTenants = TenantUtils.getSortedTenants(null, new TenantNameComparator());
        assertTrue(sortedTenants.isEmpty());

        sortedTenants = TenantUtils.getSortedTenants(tenants, null);
        assertEquals(tenants, sortedTenants);
    }

    @Test
    public void testGetSortedNewTenants() {
        List<Tenant> sortedNewTenants = TenantUtils.getSortedNewTenants(tenants);
        assertEquals(sortedNewTenants.size(), 5);
        assertEquals(sortedNewTenants.get(0).getId(), 12);
        assertEquals(sortedNewTenants.get(1).getId(), 9);
        assertEquals(sortedNewTenants.get(2).getId(), 3);
        assertEquals(sortedNewTenants.get(3).getId(), 4);
        assertEquals(sortedNewTenants.get(4).getId(), 2);

        sortedNewTenants = TenantUtils.getSortedNewTenants(null);
        assertTrue(sortedNewTenants.isEmpty());
    }

    @Test
    public void testGetNewTenantsByOpenDate() {
        List<Tenant> newTenantsByOpenDate = TenantUtils.getNewTenantsByOpenDate(tenants);
        assertEquals(newTenantsByOpenDate.size(), 5);
        assertEquals(newTenantsByOpenDate.get(0).getId(), 3);
        assertEquals(newTenantsByOpenDate.get(1).getId(), 4);
        assertEquals(newTenantsByOpenDate.get(2).getId(), 12);
        assertEquals(newTenantsByOpenDate.get(3).getId(), 9);
        assertEquals(newTenantsByOpenDate.get(4).getId(), 2);

        newTenantsByOpenDate = TenantUtils.getNewTenantsByOpenDate(null);
        assertTrue(newTenantsByOpenDate.isEmpty());
    }

    @Test
    public void testGetFeaturedNewTenantsByOpenDate() {
        List<Tenant> featuredNewTenantsByOpenDate = TenantUtils.getFeaturedNewTenantsByOpenDate(tenants);
        assertEquals(featuredNewTenantsByOpenDate.size(), 2);
        assertEquals(featuredNewTenantsByOpenDate.get(0).getId(), 12);
        assertEquals(featuredNewTenantsByOpenDate.get(1).getId(), 9);

        featuredNewTenantsByOpenDate = TenantUtils.getFeaturedNewTenantsByOpenDate(null);
        assertTrue(featuredNewTenantsByOpenDate.isEmpty());
    }

    @Test
    public void testGetFavoriteTenants() {
        List<Tenant> favoriteTenants = TenantUtils.getFavoriteTenants(tenants);
        assertEquals(favoriteTenants.size(), 3);
        assertEquals(favoriteTenants.get(0).getId(), 1);
        assertEquals(favoriteTenants.get(1).getId(), 2);
        assertEquals(favoriteTenants.get(2).getId(), 3);

        favoriteTenants = TenantUtils.getFavoriteTenants(null);
        assertTrue(favoriteTenants.isEmpty());
    }

    @Test
    public void testGetAnchorTenants() {
        List<Tenant> anchorTenants = TenantUtils.getAnchorTenants(tenants);
        assertEquals(anchorTenants.size(), 1);
        assertEquals(anchorTenants.get(0).getId(), 10);

        anchorTenants = TenantUtils.getAnchorTenants(null);
        assertTrue(anchorTenants.isEmpty());
    }

    @Test
    public void testGetTenantsByCategoryCode() {
        List<Tenant> tenantsByCategoryCode = TenantUtils.getTenantsByCategoryCode(null, tenants);
        assertEquals(tenantsByCategoryCode.size(), tenants.size());

        tenantsByCategoryCode = TenantUtils.getTenantsByCategoryCode("DOES_NOT_EXIST_CODE", tenants);
        assertTrue(tenantsByCategoryCode.isEmpty());

        tenantsByCategoryCode = TenantUtils.getTenantsByCategoryCode("FASHION", null);
        assertTrue(tenantsByCategoryCode.isEmpty());

        tenantsByCategoryCode = TenantUtils.getTenantsByCategoryCode("FASHION", tenants);
        assertEquals(tenantsByCategoryCode.size(), 5);
        assertEquals(tenantsByCategoryCode.get(0).getId(), 3);
        assertEquals(tenantsByCategoryCode.get(1).getId(), 5);
        assertEquals(tenantsByCategoryCode.get(2).getId(), 6);
        assertEquals(tenantsByCategoryCode.get(3).getId(), 7);
        assertEquals(tenantsByCategoryCode.get(4).getId(), 8);
    }

    @Test
    public void testGetTenantsByProductTypeCode() {
        List<Tenant> tenantsByProductTypeCode = TenantUtils.getTenantsByProductTypeCode(null, tenants);
        assertEquals(tenantsByProductTypeCode.size(), tenants.size());

        tenantsByProductTypeCode = TenantUtils.getTenantsByProductTypeCode("gourmet-foods/does-not-exist", tenants);
        assertTrue(tenantsByProductTypeCode.isEmpty());

        tenantsByProductTypeCode = TenantUtils.getTenantsByProductTypeCode("mens-shoes", null);
        assertTrue(tenantsByProductTypeCode.isEmpty());

        tenantsByProductTypeCode = TenantUtils.getTenantsByProductTypeCode("mens-shoes", tenants);
        assertEquals(tenantsByProductTypeCode.size(), 3);
        assertEquals(tenantsByProductTypeCode.get(0).getId(), 1);
        assertEquals(tenantsByProductTypeCode.get(1).getId(), 3);
        assertEquals(tenantsByProductTypeCode.get(2).getId(), 10);
    }

    @Test
    public void testGetTenantLeaseIdsByCategoryCode() {
        List<Integer> leaseIdsByCategoryCode = TenantUtils.getTenantLeaseIds(TenantUtils.getTenantsByCategoryCode(null, tenants));
        assertFalse(leaseIdsByCategoryCode.isEmpty());
        assertEquals(leaseIdsByCategoryCode.size(), tenants.size());

        leaseIdsByCategoryCode = TenantUtils.getTenantLeaseIds(TenantUtils.getTenantsByCategoryCode("DOES_NOT_EXIST_CODE", tenants));
        assertTrue(leaseIdsByCategoryCode.isEmpty());

        leaseIdsByCategoryCode = TenantUtils.getTenantLeaseIds(TenantUtils.getTenantsByCategoryCode("FASHION", null));
        assertTrue(leaseIdsByCategoryCode.isEmpty());

        leaseIdsByCategoryCode = TenantUtils.getTenantLeaseIds(TenantUtils.getTenantsByCategoryCode("FASHION", tenants));
        assertFalse(leaseIdsByCategoryCode.isEmpty());
        assertEquals(leaseIdsByCategoryCode.size(), 5);
        assertEquals(leaseIdsByCategoryCode.get(0).intValue(), 3);
        assertEquals(leaseIdsByCategoryCode.get(1).intValue(), 5);
        assertEquals(leaseIdsByCategoryCode.get(2).intValue(), 6);
        assertEquals(leaseIdsByCategoryCode.get(3).intValue(), 7);
        assertEquals(leaseIdsByCategoryCode.get(4).intValue(), 8);
    }

    @Test
    public void testGetTenantsExcludingTenant() {
        List<Tenant> excludedTenants = new ArrayList<>();
        excludedTenants.add(tenants.get(0));
        List<Tenant> tenantsExcludingTenant = TenantUtils.getTenantsWithExclusions(excludedTenants, tenants);
        assertEquals(tenantsExcludingTenant.size(), tenants.size() - 1);
        assertFalse(tenantsExcludingTenant.contains(excludedTenants.get(0)));

        tenantsExcludingTenant = TenantUtils.getTenantsWithExclusions(null, tenants);
        assertEquals(tenantsExcludingTenant.size(), tenants.size());

        tenantsExcludingTenant = TenantUtils.getTenantsWithExclusions(excludedTenants, null);
        assertTrue(tenantsExcludingTenant.isEmpty());
    }

    @Test
    public void testIsNewTenant() {
        assertFalse(TenantUtils.isNewTenant(null));
        assertFalse(TenantUtils.isNewTenant(tenants.get(0)));
        assertTrue(TenantUtils.isNewTenant(tenants.get(1)));
    }

    @Test
    public void testIsFavoriteTenant() {
        assertFalse(TenantUtils.isFavoriteTenant(null, null));
        assertFalse(TenantUtils.isFavoriteTenant(null, accountManager.getCurrentUser().getFavorites()));
        assertFalse(TenantUtils.isFavoriteTenant(tenants.get(0), null));
        assertTrue(TenantUtils.isFavoriteTenant(tenants.get(0), accountManager.getCurrentUser().getFavorites()));
        assertTrue(TenantUtils.isFavoriteTenant(tenants.get(1), accountManager.getCurrentUser().getFavorites()));
        assertTrue(TenantUtils.isFavoriteTenant(tenants.get(2), accountManager.getCurrentUser().getFavorites()));
        assertFalse(TenantUtils.isFavoriteTenant(tenants.get(3), accountManager.getCurrentUser().getFavorites()));
    }

    @Test
    public void testGetTenantById() {
        Tenant tenant = TenantUtils.getTenantById(-1, tenants);
        assertNull(tenant);

        tenant = TenantUtils.getTenantById(1, null);
        assertNull(tenant);

        tenant = TenantUtils.getTenantById(1, tenants);
        assertEquals(tenant, tenants.get(0));
    }

    @Test
    public void testGetTenantByLeaseId() {
        Tenant tenant = TenantUtils.getTenantByLeaseId(-1, tenants);
        assertTrue(tenant == null);

        tenant = TenantUtils.getTenantByLeaseId(1, tenants);
        assertFalse(tenant == null);
        assertEquals(tenant, tenants.get(0));
    }

    @Test
    public void testGetTenantNamesGroupedByOccurrence() {
        Map<String, Integer> namesGroupedByOccurrence = TenantUtils.getTenantNamesGroupedByOccurrence(tenants);
        assertEquals(namesGroupedByOccurrence.size(), 11);
        assertEquals(1, namesGroupedByOccurrence.get("ALDO").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("THE CONTAINER STORE").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("EXPRESS").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("SUBWAY").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("GAP").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("J.CREW").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("PANDORA (Temporarily Closed)").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("J.JILL").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("STARBUCKS").intValue());
        assertEquals(1, namesGroupedByOccurrence.get("MACY'S").intValue());
        assertEquals(2, namesGroupedByOccurrence.get("CARDTRONICS (ATM)").intValue());

        namesGroupedByOccurrence = TenantUtils.getTenantNamesGroupedByOccurrence(null);
        assertTrue(namesGroupedByOccurrence.isEmpty());
    }

    @Test
    public void testFilterByDistinctProperty() {
        Tenant s1 = new Tenant();
        s1.setPlaceWiseRetailerId(1);
        s1.setId(100);
        Tenant s2 = new Tenant();
        s2.setPlaceWiseRetailerId(2);
        s2.setId(100);
        Tenant s3 = new Tenant();
        s3.setPlaceWiseRetailerId(3);
        s3.setId(200);
        Tenant s4 = new Tenant();
        s4.setPlaceWiseRetailerId(2);
        s4.setId(300);

        List<Tenant> tenants = Arrays.asList(s1, s2, s3, s4);

        List<Tenant> uniquePlacewiseIds = TenantUtils.filterByDistinctProperty(tenants, Tenant::getPlaceWiseRetailerId);
        assertTrue(uniquePlacewiseIds.contains(s1));
        assertTrue(uniquePlacewiseIds.contains(s3));
        assertTrue(uniquePlacewiseIds.contains(s2) || uniquePlacewiseIds.contains(s4));
        assertFalse(uniquePlacewiseIds.contains(s2) && uniquePlacewiseIds.contains(s4));

        List<Tenant> uniqueIdTenants = TenantUtils.filterByDistinctProperty(tenants, Tenant::getId);
        assertTrue(uniqueIdTenants.contains(s3));
        assertTrue(uniqueIdTenants.contains(s4));
        assertTrue(uniqueIdTenants.contains(s1) || uniqueIdTenants.contains(s2));
        assertFalse(uniqueIdTenants.contains(s1) && uniqueIdTenants.contains(s2));

        uniqueIdTenants = TenantUtils.filterByDistinctProperty(null, Tenant::getId);
        assertTrue(uniqueIdTenants.isEmpty());
    }

    @Test
    public void testHasParentTenant() {
        Tenant childTenant = TenantUtils.getTenantById(9, tenants);
        Tenant parentTenant = TenantUtils.getTenantById(10, tenants);
        Tenant regularTenant = TenantUtils.getTenantById(11, tenants);
        assertTrue(TenantUtils.hasParentTenant(childTenant));
        assertFalse(TenantUtils.hasParentTenant(parentTenant));
        assertFalse(TenantUtils.hasParentTenant(regularTenant));
        assertFalse(TenantUtils.hasParentTenant(null));
    }

    @Test
    public void testGetParentTenant() {
        Tenant childTenant = TenantUtils.getTenantById(9, tenants);
        Tenant parentTenant = TenantUtils.getTenantById(10, tenants);
        assertEquals(TenantUtils.getParentTenant(childTenant, tenants).getId(), parentTenant.getId());
        assertNull(TenantUtils.getParentTenant(null, tenants));
        assertNull(TenantUtils.getParentTenant(childTenant, null));
    }
}