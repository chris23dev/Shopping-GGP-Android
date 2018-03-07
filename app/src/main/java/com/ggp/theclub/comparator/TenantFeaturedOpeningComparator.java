package com.ggp.theclub.comparator;

import com.ggp.theclub.model.Tenant;

import java.util.Comparator;

public class TenantFeaturedOpeningComparator implements Comparator<Tenant> {
    @Override
    public int compare(Tenant tenant1, Tenant tenant2) {
        if(tenant1.getFeaturedPosition() == null){
            return tenant2.getFeaturedPosition() == null ? 0 : -1;
        } else if (tenant2.getFeaturedPosition() == null) {
            return tenant1.getFeaturedPosition() == null ? 0 : 1;
        }

        return tenant1.getFeaturedPosition() - tenant2.getFeaturedPosition();
    }
}