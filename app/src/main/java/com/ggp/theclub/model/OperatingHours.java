package com.ggp.theclub.model;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java8.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * Normal operating hours
 */
public class OperatingHours extends Hours{
    @Getter @Setter private DaysOfWeek startDay;
    @Getter @Setter private DaysOfWeek endDay;


    @Override
    public boolean isOpenAtTime(LocalDateTime time) {
        if (!isOpen()) {
            return false;
        }
        LocalDateTime startDateTime = time;
        //if start day of week isn't today look backwards until we find it.
        while (DaysOfWeek.lookupFromJodaInt(startDateTime.getDayOfWeek()) != startDay) {
            startDateTime = startDateTime.minusDays(1);
            //if we hit the endDay before we reach startDay, the range must start in the future so return false;
            if (DaysOfWeek.lookupFromJodaInt(startDateTime.getDayOfWeek()) == endDay) {
                return false;
            }
        }

        LocalTime openTime = getOpenTime();
        startDateTime = startDateTime.withTime(openTime.getHourOfDay(), openTime.getMinuteOfHour(), openTime.getSecondOfMinute(), openTime.getMillisOfSecond());

        LocalDateTime endDateTime = time;
        while (DaysOfWeek.lookupFromJodaInt(endDateTime.getDayOfWeek()) != endDay) {
            endDateTime = endDateTime.plusDays(1);
        }
        LocalTime closeTime = getCloseTime();
        endDateTime = endDateTime.withTime(closeTime.getHourOfDay(), closeTime.getMinuteOfHour(), closeTime.getSecondOfMinute(), closeTime.getMillisOfSecond());

        return !time.isBefore(startDateTime) && !time.isAfter(endDateTime);
    }

    @Override
    public Optional<String> getHoursName() {
        return Optional.empty();
    }
}
