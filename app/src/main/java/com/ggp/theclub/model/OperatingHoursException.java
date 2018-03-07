package com.ggp.theclub.model;

import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.StringUtils;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Date;

import java8.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * Special operating hours for a tenant. Takes effect over a specific date range
 */
public class OperatingHoursException extends Hours{
    //monthDays in the format "MM/dd"
    @Getter @Setter String startMonthDay;
    @Getter @Setter String endMonthDay;
    @Setter private Date validUntilDate;
    @Setter String holidayName;

    public LocalDate getValidUntilDate() {
        return DateUtils.nullsafeConvertToLocalDate(validUntilDate);
    }

    @Override
    public boolean isOpenAtTime(LocalDateTime time) {
        if (!isOpen()) {
            return false;
        }
        LocalDate startDate = HoursUtils.monthAndDayWithYear(startMonthDay, time.getYear());
        LocalDateTime startDateTime = startDate.toLocalDateTime(getOpenTime());

        LocalDate endDate = HoursUtils.monthAndDayWithYear(endMonthDay, time.getYear());
        LocalDateTime endDateTime = endDate.toLocalDateTime(getCloseTime());

        //if endDateTime is before startDateTime, the year must be wrong.
        if (endDateTime.isBefore(startDateTime)) {
            endDateTime = endDateTime.plusYears(1);
        }

        return !time.isBefore(startDateTime) && !time.isAfter(endDateTime);
    }

    @Override
    public Optional<String> getHoursName() {
        return StringUtils.isEmpty(holidayName) ? Optional.empty() : Optional.of(holidayName);
    }
}