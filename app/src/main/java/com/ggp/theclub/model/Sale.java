package com.ggp.theclub.model;

import com.ggp.theclub.util.StringUtils;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Sale extends Promotion {
    @Getter @Setter private int id;
    @Getter @Setter private String type;
    @Getter @Setter private String title;
    @Getter @Setter private String imageUrl;
    @Getter @Setter private String description;
    @Getter @Setter private Date displayDateTime;
    @Getter @Setter private List<Category> categories;
    @Getter @Setter private List<Category> campaignCategories;
    @Getter @Setter boolean featured;
    @Getter @Setter boolean topRetailer;

    private final String PROMOTION_TYPE = "SALES_AND_PROMOS";
    private final String NEW_ARRIVAL_TYPE = "NEW_ARRIVAL";

    @Override
    public Integer getTenantId() {
        return getTenant() == null ? null : getTenant().getId();
    }

    public boolean isPromotion() {
        return !StringUtils.isEmpty(type) && type.equals(PROMOTION_TYPE);
    }

    public boolean isNewArrival() {
        return !StringUtils.isEmpty(type) && type.equals(NEW_ARRIVAL_TYPE);
    }
}