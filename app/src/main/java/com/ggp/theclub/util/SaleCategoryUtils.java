package com.ggp.theclub.util;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.comparator.CategoryLabelComparator;
import com.ggp.theclub.comparator.TenantNameComparator;
import com.ggp.theclub.model.Category;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.SaleCategory;
import com.ggp.theclub.model.Tenant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java8.util.Objects;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

public class SaleCategoryUtils {

    private static final int ALL_SALES_POSITION = 0;
    private static final String CATEGORY_ALL_SALES_LABEL = MallApplication.getApp().getString(R.string.all_sales_category_label);

    public static List<SaleCategory> createSaleCategories(List<Sale> sales, List<Category> categories, List<Category> campaigns) {
        List<SaleCategory> saleCategories = mapSalesToCategories(sales, categories);
        saleCategories = CategoryUtils.mapChildCategories(saleCategories);
        removeEmptyCategories(saleCategories);
        addAllSalesSaleCategory(saleCategories, sales);
        addAllChildSaleCategories(saleCategories);
        addCampaignCategories(sales, saleCategories, campaigns);

        return saleCategories;
    }

    private static List<SaleCategory> mapSalesToCategories(List<Sale> sales, List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }

        List<SaleCategory> saleCategories = StreamSupport.stream(categories)
                .map(SaleCategory::new)
                .collect(Collectors.toList());

        if (sales != null) {
            for (SaleCategory saleCategory : saleCategories) {
                List<Sale> salesForCategory = StreamSupport.stream(sales)
                        .filter(s -> CategoryUtils.categoriesContainCode(s.getCategories(), saleCategory.getCode()))
                        .collect(Collectors.toList());

                saleCategory.setSales(salesForCategory);
            }
        }

        return StreamSupport.stream(saleCategories)
                .sorted(new CategoryLabelComparator())
                .collect(Collectors.toList());
    }

    private static List<SaleCategory> mapSalesToCampaignCategories(List<Sale> sales, List<Category> campaigns) {
        if (campaigns == null) {
            return new ArrayList<>();
        }

        List<SaleCategory> saleCategories = StreamSupport.stream(campaigns)
                .map(SaleCategory::new)
                .collect(Collectors.toList());

        if (sales != null) {
            for (SaleCategory saleCategory : saleCategories) {
                List<Sale> salesForCategory = StreamSupport.stream(sales)
                        .filter(s -> CategoryUtils.categoriesContainCode(s.getCampaignCategories(), saleCategory.getCode()))
                        .collect(Collectors.toList());

                saleCategory.setSales(salesForCategory);
            }
        }

        return StreamSupport.stream(saleCategories)
                .sorted(new CategoryLabelComparator())
                .collect(Collectors.toList());
    }

    private static void removeEmptyCategories(List<SaleCategory> saleCategories) {
        for (int i = saleCategories.size() - 1; i >= 0; i--) {
            SaleCategory saleCategory = saleCategories.get(i);
            if (isEmptyParent(saleCategory)) {
                saleCategories.remove(i);
            } else {
                removeEmptyCategories(saleCategory.getChildCategories());
            }
        }
    }

    private static boolean isEmptyParent(SaleCategory saleCategory) {
        if (saleCategory.getSales().size() > 0) {
            return false;
        }

        return saleCategory.getChildCategories().size() == 0 ||
                StreamSupport.stream(saleCategory.getChildCategories()).allMatch(c -> c.getSales().size() == 0);
    }

    private static void addAllSalesSaleCategory(List<SaleCategory> saleCategories, List<Sale> sales) {
        SaleCategory allCategory = new SaleCategory();
        allCategory.setCode(CategoryUtils.CATEGORY_ALL_SALES);
        allCategory.setLabel(CATEGORY_ALL_SALES_LABEL);
        allCategory.setSales(sales);

        saleCategories.add(ALL_SALES_POSITION, allCategory);
    }

    private static void addAllChildSaleCategories(List<SaleCategory> tenantCategories) {
        List<SaleCategory> parentCategoriesWithChildren = StreamSupport.stream(tenantCategories)
                .filter(t -> t.getChildCategories().size() > 0)
                .collect(Collectors.toList());

        for (SaleCategory parentCategory : parentCategoriesWithChildren) {
            SaleCategory allCategory = new SaleCategory();
            allCategory.setCode(CategoryUtils.CATEGORY_CODE_ALL_PREFIX.concat(parentCategory.getCode()));
            String label = parentCategory.getLabel();
            allCategory.setLabel(CategoryUtils.CATEGORY_LABEL_ALL_PREFIX.concat(label == null ? "" : label));
            allCategory.setSales(distinctSales(parentCategory));
            parentCategory.getChildCategories().add(ALL_SALES_POSITION, allCategory);
        }
    }

    private static void addCampaignCategories(List<Sale> sales, List<SaleCategory> saleCategories, List<Category> campaignCategories) {
        List<SaleCategory> campaignSalesCategories = mapSalesToCampaignCategories(sales, campaignCategories);
        campaignSalesCategories = StreamSupport.stream(campaignSalesCategories).filter(saleCategory -> !saleCategory.getSales().isEmpty()).collect(Collectors.toList());
        saleCategories.addAll(campaignSalesCategories);
    }

    private static List<Sale> distinctSales(SaleCategory saleCategory) {
        Set<Sale> distinctSales = new HashSet<>(saleCategory.getSales());

        for (SaleCategory childSaleCategory : saleCategory.getChildCategories()) {
            distinctSales.addAll(childSaleCategory.getSales());
        }

        return StreamSupport.stream(distinctSales).collect(Collectors.toList());
    }

    public static SaleCategory getSaleCategoryByCode(String code, List<SaleCategory> saleCategories) {
        if (saleCategories == null) {
            return null;
        }

        Stream<SaleCategory> parentSaleCategories = StreamSupport.stream(saleCategories);
        Stream<SaleCategory> childSaleCategories =  StreamSupport.stream(saleCategories).flatMap(category -> StreamSupport.stream(category.getChildCategories()));
        Stream<SaleCategory> allSaleCategories = RefStreams.concat(parentSaleCategories, childSaleCategories);

        Optional<SaleCategory> saleCategoryOptional = allSaleCategories.filter(category -> category.getCode().equals(code)).findFirst();

        return saleCategoryOptional.isPresent() ? saleCategoryOptional.get() : null;
    }

    private static List<Sale> getSalesFromSaleCategory(SaleCategory saleCategory) {
        if (saleCategory == null) {
            return new ArrayList<>();
        }

        Stream<Sale> parentSales = StreamSupport.stream(saleCategory.getSales());
        Stream<Sale> childSales =  StreamSupport.stream(saleCategory.getChildCategories()).flatMap(category -> StreamSupport.stream(category.getSales()));
        return RefStreams.concat(parentSales, childSales).collect(Collectors.toList());
    }

    /**
     * Returns a distinct list of tenants pulled from a merge of all parent category sales and child category sales.
     */
    public static List<Tenant> getTenantsFromSaleCategory(SaleCategory saleCategory) {
        List<Tenant> tenants = StreamSupport.stream(getSalesFromSaleCategory(saleCategory))
                .map(Sale::getTenant).filter(Objects::nonNull).sorted(new TenantNameComparator()).collect(Collectors.toList());

        return TenantUtils.filterByDistinctProperty(tenants, Tenant::getId);
    }

    /**
     * Returns a distinct list of tenants matching a list of tenant IDs.
     * The list of matching tenants is pulled from a merge of all parent category sales and child category sales.
     */
    public static List<Tenant> getTenantsFromSaleCategoryWithIds(Set<Integer> tenantIds, SaleCategory saleCategory) {
        if (tenantIds == null) {
            return new ArrayList<>();
        }
        return StreamSupport.stream(getTenantsFromSaleCategory(saleCategory)).
                filter(tenant -> tenantIds.contains(tenant.getId())).collect(Collectors.toList());
    }

    public static List<Sale> getPromotionSalesFromSaleCategory(SaleCategory saleCategory) {
        return StreamSupport.stream(getSalesFromSaleCategory(saleCategory)).filter(Objects::nonNull).filter(Sale::isPromotion).collect(Collectors.toList());
    }

    public static List<Sale> getNewArrivalSalesFromSaleCategory(SaleCategory saleCategory) {
        return StreamSupport.stream(getSalesFromSaleCategory(saleCategory)).filter(Objects::nonNull).filter(Sale::isNewArrival).collect(Collectors.toList());
    }
}