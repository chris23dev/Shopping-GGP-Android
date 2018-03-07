package com.ggp.theclub.comparator;

import com.ggp.theclub.model.Promotion;
import com.ggp.theclub.util.StringUtils;
import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

public class PromotionNameComparator implements Comparator<Promotion> {
    @Override
    public int compare(Promotion p1, Promotion p2) {
        if (p1.getTenant() == null) {
            return p2.getTenant() == null ? 0 : -1;
        }

        return ComparisonChain.start()
                .compare(StringUtils.getNameForSorting(p1.getTenant().getName()), StringUtils.getNameForSorting(p2.getTenant().getName()))
                .compare(p1.getEndDateTime(), p2.getEndDateTime())
                .result();
    }
}