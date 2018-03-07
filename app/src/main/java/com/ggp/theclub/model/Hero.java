package com.ggp.theclub.model;

import org.joda.time.LocalDateTime;

import java.util.Date;

import lombok.Getter;

public class Hero {
    @Getter private int id;
    @Getter private String title;
    @Getter private String description;
    @Getter private String imageUrl;
    private Date startDate;
    private Date endDate;
    @Getter private String url;
    @Getter private String urlText;

    public LocalDateTime getStartDate() {
        return startDate == null ? null : new LocalDateTime(startDate);
    }

    public LocalDateTime getEndDate() {
        return endDate == null ? null : new LocalDateTime(endDate);
    }
}