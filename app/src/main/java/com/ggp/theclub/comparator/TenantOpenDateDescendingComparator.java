package com.ggp.theclub.comparator;

import com.ggp.theclub.model.Tenant;

import java.util.Comparator;

public class TenantOpenDateDescendingComparator implements Comparator<Tenant> {
    @Override
    public int compare(Tenant t1, Tenant t2) {
        if (t2.getStoreOpenDate() == null) {
            return t1.getStoreOpenDate() == null ? 0 : -1;
        } else if (t1.getStoreOpenDate() == null) {
            return t2.getStoreOpenDate() == null ? 0 : 1;
        }
        return t2.getStoreOpenDate().compareTo(t1.getStoreOpenDate());
    }
}