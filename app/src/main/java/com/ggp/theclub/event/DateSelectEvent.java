package com.ggp.theclub.event;

import org.joda.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by avishek.das on 1/26/16.
 */
public class DateSelectEvent {
    @Getter @Setter private LocalDate date;

    public DateSelectEvent(LocalDate date) {
        this.date = date;
    }
}
