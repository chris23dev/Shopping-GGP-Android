package com.ggp.theclub.util;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.comparator.TenantFeaturedOpeningComparator;
import com.ggp.theclub.comparator.TenantOpenDateDescendingComparator;
import com.ggp.theclub.model.Brand;
import com.ggp.theclub.model.GGPLeaseStatusComingSoon;
import com.ggp.theclub.model.Tenant;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java8.util.Optional;
import java8.util.function.Function;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import static java.util.Calendar.DAY_OF_YEAR;

public class TenantUtils {

    public static List<Tenant> getSortedNewTenants(List<Tenant> tenants) {
        LinkedHashSet<Tenant> sortedNewTenants = new LinkedHashSet<>();
        sortedNewTenants.addAll(getFeaturedNewTenantsByOpenDate(tenants));
        sortedNewTenants.addAll(getNewTenantsByOpenDate(tenants));
        return StreamSupport.stream(sortedNewTenants).collect(Collectors.toList());
    }

    protected static List<Tenant> getNewTenantsByOpenDate(List<Tenant> tenants) {
        List<Tenant> newTenants = TenantUtils.getTenantsByCategoryCode(CategoryUtils.CATEGORY_STORE_OPENING, tenants);
        return getSortedTenants(newTenants, new TenantOpenDateDescendingComparator());
    }

    protected static List<Tenant> getFeaturedNewTenantsByOpenDate(List<Tenant> tenants) {
        List<Tenant> featuredNewTenants = StreamSupport.stream(tenants)
                .filter(new Predicate<Tenant>() {
                    @Override
                    public boolean test(Tenant tenant) {
                        return (tenant.getFeaturedPosition()!=null &&
                                tenant.getFeaturedPosition() > 0) &&
                                tenant.isFeaturedOpening() &&
                                (isComingSoon(tenant) || wasOpenedLessThan90DaysAgo(tenant));
                    }

                    private boolean isComingSoon(Tenant tenant) {
                        return tenant.getLeaseStatus() == GGPLeaseStatusComingSoon.P || tenant.getLeaseStatus() == GGPLeaseStatusComingSoon.Q;
                    }

                    private boolean wasOpenedLessThan90DaysAgo(Tenant tenant) {
                        LocalDateTime storeOpenDate = tenant.getStoreOpenDate();
                        return storeOpenDate != null &&
                                storeOpenDate.isAfter(get90DaysFuture()) &&
                                storeOpenDate.isBefore(getTomorrow());
                    }

                    private LocalDateTime getTomorrow() {
                        return getDateWithShift(1);
                    }

                    private LocalDateTime get90DaysFuture() {
                        return getDateWithShift(-91);
                    }

                    private LocalDateTime getDateWithShift(int amount) {
                        Calendar instance = Calendar.getInstance();
                        instance.add(DAY_OF_YEAR, amount);
                        return new LocalDateTime(instance.getTimeInMillis());
                    }
                })
                .collect(Collectors.toList());
        return getSortedTenants(featuredNewTenants, new TenantFeaturedOpeningComparator());
    }

    public static List<Tenant> getFavoriteTenants(List<Tenant> tenants) {
        HashSet<Integer> favorites = MallApplication.getApp().getAccountManager().getCurrentUser().getFavorites();

        return favorites == null || tenants == null ? new ArrayList<>() :
                StreamSupport.stream(tenants)
                    .filter(s -> s != null && s.getPlaceWiseRetailerId() != null && favorites.contains(s.getPlaceWiseRetailerId()))
                    .collect(Collectors.toList());
    }

    public static List<Tenant> getAnchorTenants(List<Tenant> tenants) {
        return tenants == null ? new ArrayList<>() : StreamSupport.stream(tenants).filter(Tenant::isAnchor).collect(Collectors.toList());
    }

    public static List<Tenant> getTenantsByCategoryCode(String categoryCode, List<Tenant> tenants) {
        return tenants == null ? new ArrayList<>() : StreamSupport.stream(tenants)
                .filter(tenant -> CategoryUtils.categoriesContainCode(tenant.getCategories(), categoryCode))
                .collect(Collectors.toList());
    }

    public static List<Tenant> getTenantsByProductTypeCode(String productTypeCode, List<Tenant> tenants) {
        return tenants == null ? new ArrayList<>() : StreamSupport.stream(tenants).filter(tenant -> ProductUtils.tenantMatchesProductTypeCode(productTypeCode, tenant)).collect(Collectors.toList());
    }

    public static List<Tenant> getTenantsWithExclusions(List<Tenant> excludedTenants, List<Tenant> tenants) {
        return tenants == null ? new ArrayList<>() : StreamSupport.stream(tenants)
                .filter(s -> excludedTenants == null || !StreamSupport.stream(excludedTenants).anyMatch(ex -> ex.getId() == s.getId()))
                .collect(Collectors.toList());
    }

    public static List<Tenant> getTenantsByBrand(Brand brand, List<Tenant> tenants) {
        return tenants == null || brand == null ? new ArrayList<>() : StreamSupport.stream(tenants).
                filter(tenant -> tenant.getBrands() != null && tenant.getBrands().contains(brand)).collect(Collectors.toList());
    }

    public static boolean isNewTenant(Tenant tenant) {
        return tenant == null ? false : CategoryUtils.categoriesContainCode(tenant.getCategories(), CategoryUtils.CATEGORY_STORE_OPENING);
    }

    public static boolean isFavoriteTenant(Tenant tenant, Set<Integer> favorites) {
        return tenant != null && favorites != null && favorites.contains(tenant.getPlaceWiseRetailerId());
    }

    public static Tenant getTenantById(int id, List<Tenant> tenants) {
        if (tenants == null) {
            return null;
        }

        Optional<Tenant> tenantOptional = StreamSupport.stream(tenants).filter(s -> s.getId() == id).findFirst();
        return tenantOptional.isPresent() ? tenantOptional.get() : null;
    }

    public static Tenant getTenantByLeaseId(int leaseId, List<Tenant> tenants) {
        if (tenants == null) {
            return null;
        }

        Optional<Tenant> tenant = StreamSupport.stream(tenants).filter(s -> s.getLeaseId() == leaseId).findFirst();
        return tenant.isPresent() ? tenant.get() : null;
    }

    public static Map<String, Integer> getTenantNamesGroupedByOccurrence(List<Tenant> tenants) {
        return tenants == null ? new HashMap<>() : StreamSupport.stream(tenants).collect(Collectors.groupingBy(tenant -> tenant.getName(), Collectors.summingInt(tenant -> 1)));
    }

    public static List<Tenant> getSortedTenants(List<Tenant> tenants, Comparator<Tenant> comparator) {
        if (tenants == null) {
            return new ArrayList<>();
        }
        if (comparator == null) {
            return tenants;
        }

        return StreamSupport.stream(tenants).sorted(comparator).collect(Collectors.toList());
    }

    public static List<Integer> getTenantLeaseIds(List<Tenant> tenants) {
        return tenants == null ? new ArrayList<>() : StreamSupport.stream(tenants).map(t -> t.getLeaseId()).distinct().collect(Collectors.toList());
    }

    public static boolean hasParentTenant(Tenant tenant) {
        return tenant != null && tenant.getParentId() != null;
    }

    public static Tenant getParentTenant(Tenant tenant, List<Tenant> tenants) {
        return tenant == null || tenants == null ? null : getTenantById(tenant.getParentId(), tenants);
    }

    /**
     * Use this to filter a list of tenants so that items with a duplicate property, eg placewiseId, are removed.
     * The property getter is specified by propertyFunction. Also removes instances where this property is null.
     */
    public static <T extends Tenant, S> List<T> filterByDistinctProperty(List<T> tenants, Function<T, S> propertyFunction) {
        if (tenants == null) {
            return new ArrayList<>();
        }

        Set<S> propertySet = new HashSet<>();
        return StreamSupport.stream(tenants).filter(tenant -> {
            S propertyValue = propertyFunction.apply(tenant);
            if(propertyValue == null) {
                return false;
            }
            boolean isDuplicate = propertySet.contains(propertyValue);
            if (!isDuplicate) {
                propertySet.add(propertyValue);
            }
            return !isDuplicate;
        }).collect(Collectors.toList());
    }
}