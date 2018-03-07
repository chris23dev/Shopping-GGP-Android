package com.ggp.theclub.util;

import com.ggp.theclub.comparator.ProductTypeNameComparator;
import com.ggp.theclub.model.FilterProductType;
import com.ggp.theclub.model.ProductType;
import com.ggp.theclub.model.Tenant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import java8.util.Optional;
import java8.util.function.Functions;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class ProductUtils {
    private static final String PRODUCT_TYPE_MAPPING_DELIMITER = "=";
    private static final String PRODUCT_TYPE_CHILD_DELIMITER = "/";
    private static final String PRODUCT_TYPE_FULL_DISPLAY_FORMAT = "%s / %s";
    protected static final String PRODUCT_TYPE_ALL_PREFIX = "All ";
    public static final String PRODUCT_TYPE_ALL_STORES = "all-tenants";


    public static List<FilterProductType> getTenantProductTypeList(Set<ProductType> productTypes) {
        List<FilterProductType> parentFilterProductTypes = new ArrayList<>();
        List<ProductType> parentProductTypes = getParentProductTypes(productTypes);

        StreamSupport.stream(parentProductTypes).forEach(parentProductType -> {
            List<ProductType> childProductTypes = getChildProductTypes(parentProductType, productTypes);
            List<FilterProductType> childFilterProductTypes = getSortedFilterProductTypes(childProductTypes);
            parentFilterProductTypes.add(new FilterProductType(parentProductType.getCode(), parentProductType.getLabel(), childFilterProductTypes));
        });

        List<FilterProductType> sortedParentFilterProductTypes = StreamSupport.stream(parentFilterProductTypes).sorted(new ProductTypeNameComparator()).collect(Collectors.toList());

        return sortedParentFilterProductTypes;
    }

    public static FilterProductType getTenantProductTypeTree(List<Tenant> tenants, String allTenantsProductLabel) {
        Set<ProductType> productTypes = getProductTypesFromTenants(tenants);
        Map<ProductType, Integer> productTypeToTenantCountMap = StreamSupport.stream(productTypes)
                .collect(Collectors.toMap(Functions.identity(), productType -> TenantUtils.getTenantsByProductTypeCode(productType.getCode(), tenants).size()));
        List<ProductType> parentProductTypes = getParentProductTypes(productTypes);
        List<FilterProductType> parentFilterProductTypes = new ArrayList<>();

        /**
         * Creates a new filter product type object for each parent product type
         * containing the product type code, user readable representation of the product type code, as well as children, if they exist.
         * If child product types exist, then a list of new filter product type objects representing the children are created,
         * and a filter product type object representing the parent product type is added to the head of the new list.
         * This allows users to filter on all tenants of a particular type from the expanded list.
         */
        AtomicInteger totalCount = new AtomicInteger(0);
        StreamSupport.stream(parentProductTypes).forEach(parentProductType -> {
            List<ProductType> childProductTypes = getChildProductTypes(parentProductType, productTypes);
            List<FilterProductType> childFilterProductTypes = getSortedFilterProductTypesWithCounts(childProductTypes, productTypeToTenantCountMap);
            int count = productTypeToTenantCountMap.get(parentProductType);
            totalCount.getAndAdd(count);
            if (!childFilterProductTypes.isEmpty()) {
                String allDescription = PRODUCT_TYPE_ALL_PREFIX.concat(parentProductType.getLabel());
                childFilterProductTypes.add(0, new FilterProductType(parentProductType.getCode(), allDescription, count));
            }
            parentFilterProductTypes.add(new FilterProductType(parentProductType.getCode(), parentProductType.getLabel(), count, childFilterProductTypes));
        });

        List<FilterProductType> sortedParentFilterProductTypes = StreamSupport.stream(parentFilterProductTypes).sorted(new ProductTypeNameComparator()).collect(Collectors.toList());

        /**
         * Adds an all-tenants filter product type object to the head of the parent filter product type list.
         * This allows users to filter on all tenants instead of a particular type from the list.
         */
        sortedParentFilterProductTypes.add(0, new FilterProductType(PRODUCT_TYPE_ALL_STORES, allTenantsProductLabel, totalCount.get()));

        return new FilterProductType(PRODUCT_TYPE_ALL_STORES, allTenantsProductLabel, 0, sortedParentFilterProductTypes);
    }

    protected static Set<ProductType> getProductTypesFromTenants(List<Tenant> tenants) {
        Set<ProductType> productTypes = new HashSet<>();
        StreamSupport.stream(tenants).forEach(tenant -> productTypes.addAll(tenant.getProductTypes()));
        return productTypes;
    }

    /**
     * Creates a sorted list of parent product types by filtering the product type set
     * to only return the product types whose product type codes do not include the PRODUCT_TYPE_CHILD_DELIMITER.
     */
    private static List<ProductType> getParentProductTypes(Set<ProductType> productTypes) {
        List<ProductType> parentProductTypes = StreamSupport.stream(productTypes).filter(productType -> !isChildProductType(productType.getCode())).collect(Collectors.toList());
        return parentProductTypes;
    }

    /**
     * Creates a sorted list of child product types of parent product type by filtering the product type set
     * to only return the product types whose product type codes include the PRODUCT_TYPE_CHILD_DELIMITER
     * and where the product type code substring before the PRODUCT_TYPE_CHILD_DELIMITER matches the parent product type code.\
     */
    private static List<ProductType> getChildProductTypes(ProductType parentProductType, Set<ProductType> productTypes) {
        List<ProductType> childProductTypes = StreamSupport.stream(productTypes)
                .filter(productType -> isChildProductType(productType.getCode()) && parentProductType.getCode().equals(getParentProductTypeCode(productType.getCode())))
                .collect(Collectors.toList());
        return childProductTypes;
    }

    private static List<FilterProductType> getSortedFilterProductTypes(List<ProductType> productTypes) {
        return StreamSupport.stream(productTypes)
                .map(productType -> new FilterProductType(productType.getCode(), productType.getLabel()))
                .sorted(new ProductTypeNameComparator()).collect(Collectors.toList());
    }

    private static List<FilterProductType> getSortedFilterProductTypesWithCounts(List<ProductType> productTypes, Map<ProductType, Integer> productTypeToCountMap) {
        return StreamSupport.stream(productTypes)
                .map(productType -> new FilterProductType(productType.getCode(), productType.getLabel(), productTypeToCountMap.get(productType)))
                .sorted(new ProductTypeNameComparator()).collect(Collectors.toList());
    }

    public static boolean tenantMatchesProductTypeCode(String productTypeCode, Tenant tenant) {
        if (StringUtils.isEmpty(productTypeCode) || productTypeCode.equals(PRODUCT_TYPE_ALL_STORES)) {
            return true;
        }
        return StreamSupport.stream(tenant.getProductTypes())
                .filter(productType -> productType.getCode().equals(productTypeCode) || productType.getParentCode().equals(productTypeCode))
                .findFirst()
                .isPresent();
    }

    private static boolean isChildProductType(String productTypeCode) {
        return productTypeCode.contains(PRODUCT_TYPE_CHILD_DELIMITER);
    }

    /**
     * Extracts and returns the parent product type code portion of a parent/child formatted string.
     * This method may return null if no parent product type code match is found.
     */
    private static String getParentProductTypeCode(String productTypeCode) {
        try {
            return productTypeCode.split(PRODUCT_TYPE_CHILD_DELIMITER)[0];
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static String getProductTypeFilterLabel(Optional<FilterProductType> parentFilterProductTypeOptional, FilterProductType filterProductType ) {
        if (parentFilterProductTypeOptional.isPresent() && !parentFilterProductTypeOptional.get().getCode().equals(filterProductType.getCode())) {
            return String.format(PRODUCT_TYPE_FULL_DISPLAY_FORMAT, parentFilterProductTypeOptional.get().getDescription(), filterProductType.getDescription());
        } else {
            return filterProductType.getDescription();
        }
    }
}