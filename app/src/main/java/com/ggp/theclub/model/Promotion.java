package com.ggp.theclub.model;

import com.ggp.theclub.util.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDateTime;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public abstract class Promotion {
    @Setter Date startDateTime;
    @Setter Date endDateTime;
    @Getter @Setter @SerializedName("store") Tenant tenant;

    public abstract String getTitle();
    public abstract String getImageUrl();
    public abstract String getDescription();
    public abstract Integer getTenantId();

    public String getDateRange() {
        return DateUtils.getPromotionDateTimeRange(getStartDateTime(), getEndDateTime());
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime == null ? null : new LocalDateTime(startDateTime);
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime == null ? null : new LocalDateTime(endDateTime);
    }
}