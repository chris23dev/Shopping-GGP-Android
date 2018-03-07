package com.ggp.theclub.util;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.comparator.CategoryLabelComparator;
import com.ggp.theclub.comparator.TenantNameComparator;
import com.ggp.theclub.model.Category;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.model.TenantCategory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class TenantCategoryUtils {

    private static final int ALL_STORES_POSITION = 0;
    private static final int MY_FAVORITES_POSITION = 1;
    private static final int STORE_OPENINGS_POSITION = 2;
    private static final String CATEGORY_ALL_STORES_LABEL = MallApplication.getApp().getString(R.string.all_stores_category_label);
    private static final String CATEGORY_MY_FAVORITES_LABEL = MallApplication.getApp().getString(R.string.my_favorites_category_label);

    public static TenantCategory findTenantCategory(List<TenantCategory> tenantCategories, String code) {
        List<TenantCategory> flattened = flattenedTenantCategories(tenantCategories);

        Optional<TenantCategory> tenantCategoryOptional = StreamSupport.stream(flattened)
                .filter(t -> t.getCode().equals(code))
                .findFirst();

        return tenantCategoryOptional.isPresent() ? tenantCategoryOptional.get() : null;
    }

    public static List<TenantCategory> createTenantCategories(List<Tenant> tenants, List<Category> categories) {
        List<TenantCategory> tenantCategories = mapTenantsToCategories(tenants, categories);
        tenantCategories = CategoryUtils.mapChildCategories(tenantCategories);
        removeEmptyCategories(tenantCategories);
        addAllStoresTenantCategory(tenantCategories, tenants);
        addAllChildTenantCategories(tenantCategories);
//        addMyFavoritesTenantCategory(tenantCategories, tenants, MallApplication.getApp().getAccountManager().getCurrentUser().getFavorites());
        adjustStoreOpeningTenantCategoryPosition(tenantCategories);

        return tenantCategories;
    }

    private static List<TenantCategory> mapTenantsToCategories(List<Tenant> tenants, List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }

        List<TenantCategory> tenantCategories = StreamSupport.stream(categories)
                .map(TenantCategory::new)
                .collect(Collectors.toList());

        if (tenants != null) {
            for (TenantCategory tenantCategory : tenantCategories) {
                List<Tenant> tenantsForCategory = StreamSupport.stream(tenants)
                        .filter(t -> CategoryUtils.categoriesContainCode(t.getCategories(), tenantCategory.getCode()))
                        .collect(Collectors.toList());

                tenantCategory.setTenants(tenantsForCategory);
            }
        }

        return StreamSupport.stream(tenantCategories)
                .sorted(new CategoryLabelComparator())
                .collect(Collectors.toList());
    }

    private static void addAllStoresTenantCategory(List<TenantCategory> tenantCategories, List<Tenant> tenants) {
        TenantCategory allCategory = new TenantCategory();
        allCategory.setCode(CategoryUtils.CATEGORY_ALL_STORES);
        allCategory.setLabel(CATEGORY_ALL_STORES_LABEL);
        allCategory.setTenants(tenants);

        tenantCategories.add(ALL_STORES_POSITION, allCategory);
    }

    private static void addAllChildTenantCategories(List<TenantCategory> tenantCategories) {
        List<TenantCategory> parentCategoriesWithChildren = StreamSupport.stream(tenantCategories)
                .filter(t -> t.getChildCategories().size() > 0)
                .collect(Collectors.toList());

        for (TenantCategory parentCategory : parentCategoriesWithChildren) {
            TenantCategory allCategory = new TenantCategory();
            allCategory.setCode(CategoryUtils.CATEGORY_CODE_ALL_PREFIX.concat(parentCategory.getCode()));
            allCategory.setLabel(CategoryUtils.CATEGORY_LABEL_ALL_PREFIX.concat(parentCategory.getLabel()));
            allCategory.setTenants(distinctTenants(parentCategory));
            parentCategory.getChildCategories().add(ALL_STORES_POSITION, allCategory);
        }
    }

    private static void addMyFavoritesTenantCategory(List<TenantCategory> tenantCategories, List<Tenant> tenants, Set<Integer> favoriteRetailerIds) {
        List<Tenant> favoriteTenants = StreamSupport.stream(tenants)
                .filter(t -> favoriteRetailerIds != null && t.getPlaceWiseRetailerId() != null && favoriteRetailerIds.contains(t.getPlaceWiseRetailerId()))
                .collect(Collectors.toList());

        TenantCategory favoritesCategory = new TenantCategory();
        favoritesCategory.setCode(CategoryUtils.CATEGORY_MY_FAVORITES);
        favoritesCategory.setLabel(CATEGORY_MY_FAVORITES_LABEL);
        favoritesCategory.setTenants(favoriteTenants);

        tenantCategories.add(MY_FAVORITES_POSITION, favoritesCategory);
    }

    private static void removeEmptyCategories(List<TenantCategory> tenantCategories) {
        for (int i = tenantCategories.size() - 1; i >= 0; i--) {
            TenantCategory tenantCategory = tenantCategories.get(i);
            if (isEmptyParent(tenantCategory)) {
                tenantCategories.remove(i);
            } else {
                removeEmptyCategories(tenantCategory.getChildCategories());
            }
        }
    }

    private static boolean isEmptyParent(TenantCategory tenantCategory) {
        if (tenantCategory.getTenants().size() > 0) {
            return false;
        }

        return tenantCategory.getChildCategories().size() == 0 ||
                StreamSupport.stream(tenantCategory.getChildCategories()).allMatch(c -> c.getTenants().size() == 0);
    }

    private static void adjustStoreOpeningTenantCategoryPosition(List<TenantCategory> tenantCategories) {
        Optional<TenantCategory> storeOpeningsOptional = StreamSupport.stream(tenantCategories)
                .filter(t -> t.getCode().equals(CategoryUtils.CATEGORY_STORE_OPENING))
                .findFirst();

        if (storeOpeningsOptional.isPresent()) {
            TenantCategory storeOpenings = storeOpeningsOptional.get();
            tenantCategories.remove(storeOpenings);
            tenantCategories.add(STORE_OPENINGS_POSITION, storeOpenings);
        }
    }

    private static List<TenantCategory> flattenedTenantCategories(List<TenantCategory> tenantCategories) {
        List<TenantCategory> flattened = new ArrayList<>();
        for (TenantCategory tenantCategory : tenantCategories) {
            flattened.add(tenantCategory);
            flattened.addAll(tenantCategory.getChildCategories());
        }
        return flattened;
    }

    private static List<Tenant> distinctTenants(TenantCategory tenantCategory) {
        Set<Tenant> distinctTenants = new HashSet<>(tenantCategory.getTenants());

        for (TenantCategory childTenantCategory : tenantCategory.getChildCategories()) {
            distinctTenants.addAll(childTenantCategory.getTenants());
        }

        return StreamSupport.stream(distinctTenants)
                .sorted(new TenantNameComparator())
                .collect(Collectors.toList());
    }
}
