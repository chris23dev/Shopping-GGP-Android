package com.ggp.theclub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class TenantCategory extends Category<TenantCategory> {

    @Getter @Setter private List<Tenant> tenants = new ArrayList<>();

    public TenantCategory() { }

    public TenantCategory(Category category) {
        super(category);
    }
}
