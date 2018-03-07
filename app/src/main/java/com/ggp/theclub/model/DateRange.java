package com.ggp.theclub.model;

import java.util.Date;

import lombok.Getter;

/**
 * represents API DateRange object
 */

public class DateRange {
    public enum Type {FEATURE, CAMPAIGN}
    @Getter Date displayDate;
    @Getter Date startDate;
    @Getter Date endDate;
    @Getter String code;
    @Getter Type type;
    @Getter String url;
}
