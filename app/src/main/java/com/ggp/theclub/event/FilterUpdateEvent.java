package com.ggp.theclub.event;

import com.ggp.theclub.model.Brand;

import lombok.Getter;
import lombok.Setter;

public abstract class FilterUpdateEvent {
    @Getter @Setter private FilterType filterType;
    @Getter @Setter private String filterLabel;
    @Getter @Setter private int filterCount;
    @Getter private String categoryCode;
    @Getter private String productTypeCode;
    @Getter private Brand brand;

    public enum FilterType { CATEGORY, PRODUCT_TYPE, BRAND, AMENITY }

    public FilterUpdateEvent(String filterCode, FilterType filterType, String filterLabel) {
        this(filterCode, 0, filterType, filterLabel);
    }

    public FilterUpdateEvent(String filterCode, int filterCount, FilterType filterType, String filterLabel) {
        this.filterType = filterType;
        this.filterCount = filterCount;
        switch (filterType) {
            case CATEGORY:
                this.categoryCode = filterCode;
                break;
            case PRODUCT_TYPE:
                this.productTypeCode = filterCode;
                break;
        }
        this.filterLabel = filterLabel;
    }

    public FilterUpdateEvent(Brand brand) {
        filterType = FilterType.BRAND;
        this.brand = brand;
        this.filterLabel = brand.getName();
    }
}
