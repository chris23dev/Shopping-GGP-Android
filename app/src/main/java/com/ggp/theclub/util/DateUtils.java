package com.ggp.theclub.util;

import android.support.annotation.NonNull;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.customlocale.LocaleUtils;
import com.ggp.theclub.model.Mall;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;

public class DateUtils {
    //number of days ahead for which a promotion must be active to be considered ongoing.
    private static final int ONGOING_PROMOTION_THRESHOLD = 90;

    private static final DateTimeFormatter rangeFormatter = DateTimeFormat.forPattern("MMM d");
    private static final DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("EEE, MMM d");
    private static final DateTimeFormatter dayWithYearFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h:mm a");
    private static final DateTimeFormatter militaryTimeFormatter = DateTimeFormat.forPattern("kk:mm");


    public static final String DEFAULT_DATE_TIME_TEMPLATE = "%1$s: %2$s";

    public static String todayDateOnly() {
        return dayWithYearFormatter.print(new LocalDate());
    }

    public static String getParkingDateString(LocalDateTime dateTime) {
        return dayFormatter.print(dateTime);
    }

    /**
     * Overloaded method which uses system date for "today"
     */
    public static String getPromotionDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDate today = new LocalDate();
        return getPromotionDateTimeRange(startDateTime, endDateTime, today);
    }

    /**
     * Overloaded method which uses system date for "today"
     */
    public static DateTimeStrings getPromotionDateTimeStrings(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDate today = new LocalDate();
        return getPromotionDateTimeStrings(startDateTime, endDateTime, today);
    }

    public static String getPromotionDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate today) {
        DateTimeStrings strings = getPromotionDateTimeStrings(startDateTime, endDateTime, today);
        return StringUtils.isEmpty(strings.getTimeString()) ? strings.getDateString() : String.format(DEFAULT_DATE_TIME_TEMPLATE, strings.getDateString(), strings.getTimeString());
    }

    private static DateTimeStrings getPromotionDateTimeStrings(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate today) {
        String timeRange = getPromotionTimeRange(startDateTime, endDateTime);
        //The range should include day of week if there is also a time range
        boolean includeDayOfWeek = !StringUtils.isEmpty(timeRange);
        String dateRange = getPromotionDateRange(startDateTime, endDateTime, today, includeDayOfWeek);

        return new DateTimeStrings(dateRange, timeRange);
    }

    public static List<LocalDate> createDateList(int numDays) {
        List<LocalDate> dateList = new ArrayList<>();
        for(int i = 0; i < numDays; i++) {
            LocalDate date = new LocalDate().plusDays(i);
            dateList.add(date);
        }

        return dateList;
    }

    /**
     * This method will always include day of week if the date range is a single day,
     * but otherwise may include day of week depending on the value of includeDayOfWeek
     */
    private static String getPromotionDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate today, boolean includeDayOfWeek) {
        if (startDateTime == null || endDateTime == null ||
                startDateTime.dayOfMonth().roundFloorCopy().equals(endDateTime.dayOfMonth().roundFloorCopy())) {
            return getSingleDayDateRange(startDateTime, endDateTime, today);
        }
        return getMultiDayDateRange(startDateTime, endDateTime, today, includeDayOfWeek);
    }

    /**
     * Returns null if startDateTime and startEndTime are null
     * Returns R.string.today if today is the single day,
     * Returns a single day with format "EEE, MMM d" otherwise
     */
    private static String getSingleDayDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate today) {
        if (startDateTime == null && endDateTime == null) { return null; }
        //at least one of startDate, endDate is not null, so find the non-null one
        LocalDateTime singleDayDateTime = (startDateTime == null) ? endDateTime : startDateTime;
        return singleDayDateTime.toLocalDate().equals(today) ? MallApplication.getApp().getString(R.string.today) : dayFormatter.print(singleDayDateTime);
    }

    /**
     * Returns date range as MMM d - MMM d
     * if includeDayOfWeek is true returns date range in format "EEE, MMM d", otherwise uses format "MMM d"
     * if the end date is ONGOING_PROMOTION_THRESHOLD number of days in the future, returns R.string.ongoing instead
     * if the start date is in the past, start date is R.string.now
     */
    private static String getMultiDayDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate today, boolean includeDayOfWeek) {
        DateTimeFormatter dateFormatter = includeDayOfWeek ? dayFormatter : rangeFormatter;
        String start;

        if (!startDateTime.toLocalDate().isAfter(today)) {
            if (endDateTime.toLocalDate().minusDays(ONGOING_PROMOTION_THRESHOLD).isAfter(today)) {
                return MallApplication.getApp().getString(R.string.ongoing);
            }
            start = MallApplication.getApp().getString(R.string.now);
        } else {
            start = dateFormatter.print(startDateTime);
        }
        String end = dateFormatter.print(endDateTime);
        return (start + " - " + end);
    }

    /**
     * Returns time range in format h:mm a - h:mm a, unless one of the times is null or midnight.
     */
    private static String getPromotionTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = HoursUtils.roundCutoffToMidnight(endDateTime.toLocalTime());
        return isFullDayEvent(startTime, endTime) ? null : (chooseTimeFormatter().print(startTime) + " - " + chooseTimeFormatter().print(endTime));
    }

    private static boolean isFullDayEvent(LocalTime startTime, LocalTime endTime) {
        return startTime.equals(LocalTime.MIDNIGHT) && endTime.equals(LocalTime.MIDNIGHT);
    }

    public static boolean isTodayInDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime today = new LocalDateTime();
        return !today.isBefore(startDateTime) && !today.isAfter(endDateTime);
    }

    /**
     * populates a list of all dates in a range inclusive
     */
    public static List<LocalDate> populateDateList(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> result = new ArrayList<>();
        for (LocalDate date = startDate ; !date.isAfter(endDate); date = date.plusDays(1)) {
            result.add(date);
        }
        return result;
    }

    public static boolean isHolidayHoursActive(Mall mall, LocalDate date) {
        return mall.getHolidayHoursDisplayDate() != null && !date.isBefore(mall.getHolidayHoursDisplayDate()) &&
                (mall.getHolidayHoursEndDate() == null || !date.isAfter(mall.getHolidayHoursEndDate()));
    }

    public static boolean isBlackFridayHoursActive(Mall mall, LocalDate date) {
        return mall.getBlackFridayHoursDisplayDate() != null && !date.isBefore(mall.getBlackFridayHoursDisplayDate()) &&
                (mall.getBlackFridayHoursEndDate() == null || !date.isAfter(mall.getBlackFridayHoursEndDate()));
    }

    public static class DateTimeStrings {
        @Getter String dateString;
        @Getter String timeString;

        public DateTimeStrings(String dateString, String timeString) {
            this.dateString = dateString;
            this.timeString = timeString;
        }
    }

    public static LocalDate nullsafeConvertToLocalDate(Date date) {
        return date == null ? null : LocalDate.fromDateFields(date);
    }

    public static LocalDate max(LocalDate a, LocalDate b) {
        return a.compareTo(b) > 0 ? a: b;
    }

    @NonNull
    private static DateTimeFormatter chooseTimeFormatter() {
        if(LocaleUtils.isNowNextLanguageCode("en")){
            return timeFormatter;
        } else {
            return militaryTimeFormatter;
        }
    }

}
