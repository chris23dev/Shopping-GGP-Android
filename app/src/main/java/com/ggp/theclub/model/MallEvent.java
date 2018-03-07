package com.ggp.theclub.model;

import com.ggp.theclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MallEvent extends Promotion {
    @Getter @Setter private int id;
    @Getter @Setter private String name;
    @Getter @Setter private String location;
    @Getter @Setter private String description;
    @Getter @Setter private String teaserDescription;
    @Setter private String imageUrl;
    @Getter @Setter private List<EventLink> externalLinks;
    private List<Mappings> mappings;

    public List<Integer> getTenantIds() {
        boolean hasStoreId = mappings != null && mappings.size() > 0 && mappings.get(0).getStoreIds() != null;
        return hasStoreId ? mappings.get(0).getStoreIds() : new ArrayList<>();
    }

    @Override
    public Integer getTenantId() {
        List<Integer> storeIds = getTenantIds();
        return storeIds.size() > 0 ? storeIds.get(0) : null;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getImageUrl() {
        if (!StringUtils.isEmpty(imageUrl)) {
            return imageUrl;
        } else if (tenant != null && !StringUtils.isEmpty(tenant.getLogoUrl())) {
            return tenant.getLogoUrl();
        }
        return null;
    }

    public class Mappings {
        @Getter @Setter List<Integer> storeIds;
    }

    public class EventLink {
        @Getter @Setter String displayText;
        @Getter @Setter String url;
    }
}