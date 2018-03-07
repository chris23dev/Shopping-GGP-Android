package com.ggp.theclub.model;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.util.StringUtils;
import com.google.common.net.InternetDomainName;
import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDateTime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(includeFieldNames = false, of = {"name"})
public class Tenant {

    private final String ANCHOR_TYPE = "ANCHOR";

    @Getter @Setter private int id;
    @Getter @Setter private int leaseId;
    @Getter @Setter private Integer placeWiseRetailerId;
    @Getter @Setter private Integer parentId;
    @Getter @Setter private List<Integer> childIds = new ArrayList<>();
    @Getter @Setter private String phoneNumber;
    @Getter @Setter private String websiteUrl;
    @SerializedName("nonSvgLogoUrl")
    @Getter @Setter private String logoUrl;
    @Getter @Setter private String description;
    @Setter private List<Category> categories;
    @Getter @Setter private Set<OperatingHours> operatingHours;
    @Getter @Setter private Set<OperatingHoursException> operatingHoursExceptions;
    @Getter @Setter private String locationDescription;
    @Getter @Setter private String openTableId;
    @Getter @Setter private Unit unit;
    @Getter @Setter private boolean temporarilyClosed;
    @Getter @Setter private boolean featuredOpening;
    @Getter @Setter private String comingSoonLabel;
    @Getter @Setter private Integer featuredPosition;
    @Getter @Setter private GGPLeaseStatusComingSoon leaseStatus;
    @Getter @Setter private double latitude;
    @Getter @Setter private double longitude;
    @Setter private Date storeOpenDate;
    @Setter private String name;
    private ProductData productData;

    public String getShortWebsiteUrl() {
        try {
            return InternetDomainName.from(new URL(websiteUrl).getHost()).topPrivateDomain().toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public String getWebsiteUrl(){
        return websiteUrl;
    }

    public LocalDateTime getStoreOpenDate() {
        return storeOpenDate == null ? null : new LocalDateTime(storeOpenDate);
    }

    public String getName() {
        String closedMessage = " " + StringUtils.getNonWrappingString(MallApplication.getApp().getString(R.string.temporarily_closed_message));
        return isTemporarilyClosed() ? name + closedMessage : name;
    }

    public boolean isAnchor() {
        return unit != null && unit.getType() != null && unit.getType().equals(ANCHOR_TYPE);
    }

    public class Unit {
        @Getter @Setter String type;
    }

    //TODO remove this when tenant categories are in all environments
    public List<Category> getCategories() {
        return categories != null ? categories : new ArrayList<>();
    }

    public Set<Brand> getBrands() {
        return productData == null ? new HashSet<>(0) : productData.getBrands();
    }

    public Set<ProductType> getProductTypes() {
        return productData == null ? new HashSet<>(0) : productData.getProductTypes();
    }
}