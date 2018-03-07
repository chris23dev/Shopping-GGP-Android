package com.ggp.theclub.model;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java8.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all types of tenant hours
 */
public abstract class Hours {
    @Setter private String openTime;
    @Setter private String closeTime;
    @Getter @Setter private boolean open;

    public LocalTime getOpenTime() {
        return new LocalTime(openTime);
    }

    public LocalTime getCloseTime() {
        return new LocalTime(closeTime);
    }

    public abstract boolean isOpenAtTime(LocalDateTime time);
    public abstract Optional<String> getHoursName();
}