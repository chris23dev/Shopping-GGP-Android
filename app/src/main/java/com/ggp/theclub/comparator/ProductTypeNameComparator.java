package com.ggp.theclub.comparator;

import com.ggp.theclub.model.FilterProductType;

import java.util.Comparator;

public class ProductTypeNameComparator implements Comparator<FilterProductType> {
    @Override
    public int compare(FilterProductType p1, FilterProductType p2) {
        if (p1.getDescription() == null) {
            return p2.getDescription() == null ? 0 : -1;
        }
        if (p2.getDescription() == null) {
            return 1;
        }
        return p1.getDescription().compareTo(p2.getDescription());
    }
}