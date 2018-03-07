package com.ggp.theclub.model.view;

import com.ggp.theclub.model.Alert;
import com.ggp.theclub.model.Hero;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class HomeFeedViewModel {
    @Getter @Setter Alert mallAlert;
    @Getter @Setter Hero featuredContent;
    @Getter @Setter List<Tenant> tenantOpenings;
    @Getter @Setter List<Sale> sales;
}
