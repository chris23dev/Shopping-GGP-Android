package com.ggp.theclub.comparator;

import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.StringUtils;

import java.text.Collator;
import java.util.Comparator;

public class TenantNameComparator implements Comparator<Tenant> {

    public static final TenantNameComparator INSTANCE = new TenantNameComparator();

    @Override
    public int compare(Tenant t1, Tenant t2) {
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);
        return collator.compare(StringUtils.getNameForSorting(t1.getName()), StringUtils.getNameForSorting(t2.getName()));
    }
}