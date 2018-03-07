package com.ggp.theclub.model;

import java.util.List;

import lombok.Getter;

public class ExcludedTenants {
    @Getter private List<Tenant> tenantList;

    public ExcludedTenants(List<Tenant> tenantList) {
        this.tenantList = tenantList;
    }
}
