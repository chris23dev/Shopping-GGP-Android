package com.ggp.theclub.model;

import org.joda.time.LocalDateTime;

import java.util.Date;

import lombok.Getter;

/**
 * Created by dustind on 1/24/16.
 */
public class Alert {
    @Getter long id;
    @Getter String message;
    Date effectiveStartDateTime;

    public LocalDateTime getEffectiveStartDateTime() {
        return effectiveStartDateTime == null ? null : new LocalDateTime(effectiveStartDateTime);
    }
}
