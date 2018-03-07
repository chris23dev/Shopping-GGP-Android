package com.ggp.theclub.event;

import com.ggp.theclub.model.Brand;
import com.ggp.theclub.util.CategoryUtils;

public class TenantsFilterUpdateEvent extends FilterUpdateEvent {
    public TenantsFilterUpdateEvent() {
        this(CategoryUtils.CATEGORY_ALL_STORES, FilterType.CATEGORY, null);
    }

    public TenantsFilterUpdateEvent(String filterCode, FilterType filterType, String filterLabel) {
        super(filterCode, filterType, filterLabel);
    }

    public TenantsFilterUpdateEvent(String filterCode, int filterCount, FilterType filterType, String filterLabel) {
        super(filterCode, filterCount, filterType, filterLabel);
    }

    public TenantsFilterUpdateEvent(Brand brand) {
        super(brand);
    }
}