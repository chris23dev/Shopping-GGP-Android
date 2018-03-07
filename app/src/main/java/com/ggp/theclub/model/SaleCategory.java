package com.ggp.theclub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SaleCategory extends Category<SaleCategory> {

    @Getter @Setter private List<Sale> sales = new ArrayList<>();

    public SaleCategory() {}
    
    public SaleCategory(Category category) {
        super(category);
    }
}
