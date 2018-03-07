package com.ggp.theclub.util;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.customlocale.LocaleUtils;
import com.ggp.theclub.model.DaysOfWeek;
import com.ggp.theclub.model.Hours;
import com.ggp.theclub.model.HoursLineItem;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.model.OperatingHours;
import com.ggp.theclub.model.OperatingHoursException;
import com.google.common.base.Joiner;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import lombok.Getter;
import lombok.Setter;

public class HoursUtils {
    private static final String LOG_TAG = HoursUtils.class.getSimpleName();
    private static final DateTimeFormatter monthDayYearSlashFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");
    public static final String openCloseTimePrettyFormat = "h:mm a";
    public static final String openCloseTimeMilitaryFormat = "kk:mm";
    private static final String weekdayPrettyFormat = "EEEE";
    private static final String TODAY_HOURS = MallApplication.getApp().getString(R.string.tenant_today_hours);
    private static final String STORE_HOURS = MallApplication.getApp().getString(R.string.tenant_store_hours);
    private static final String CLOSED = MallApplication.getApp().getString(R.string.closed);
    //how many days to show when showing upcoming hours
    private static final int SHOW_HOURS_FOR_DAYS = 7;
    //if end time is after this time then it's considered midnight when determining whether to display time
    private static LocalTime TIME_CUTOFF = new LocalTime(23, 59, 00);

    /**
     * Takes a set of operating hours and operating hours exceptions and returns the relevant hours
     * for the given currentDate. Returns an emtpy set if no match was found
     */
    public static Set<Hours> getHoursByDate(Set<OperatingHours> operatingHoursSet, Set<OperatingHoursException> operatingHoursExceptionSet, LocalDate currentDate) {
        //Look for an exception to normal hours first
        Set<OperatingHoursException> matchingHoursExceptions = findMatchingOperatingHoursException(operatingHoursExceptionSet, currentDate);

        if (matchingHoursExceptions != null && !matchingHoursExceptions.isEmpty()) {
            return new HashSet<>(matchingHoursExceptions);
        }

        //look for normally-scheduled hours
        Set<OperatingHours> matchingNormalHours = findMatchingRegularHours(operatingHoursSet, currentDate);
        if (matchingNormalHours != null && !matchingNormalHours.isEmpty()) {
            return new HashSet<>(matchingNormalHours);
        }

        return new HashSet<>();
    }

    /**
     * Find all OperatingHoursException that are active for the given date
     */
    private static Set<OperatingHoursException> findMatchingOperatingHoursException(Set<OperatingHoursException> operatingHoursExceptionSet, LocalDate currentDate) {
        if (operatingHoursExceptionSet == null || operatingHoursExceptionSet.size() == 0) {
            return null;
        }
        Set<OperatingHoursException> matchingHoursExceptions =
                StreamSupport.stream(operatingHoursExceptionSet)
                        .filter(h -> isHoursExceptionActive(h, currentDate))
                        .collect(Collectors.toSet());
        return matchingHoursExceptions;
    }

    /**
     * An exception is active if the start date is today and it isn't expired
     */
    private static boolean isHoursExceptionActive(OperatingHoursException hoursException, LocalDate currentDate ) {
        if (hoursException.getValidUntilDate() == null || !currentDate.isAfter(hoursException.getValidUntilDate())) {
            int currentYear = currentDate.getYear();
            LocalDate startDate = monthAndDayWithYear(hoursException.getStartMonthDay(), currentYear);
            //return true if the currentDate is the start date
            return currentDate.equals(startDate);
        }
        return false;
    }

    /**
     * Find all OperatingHours that are active for the given date. Searches by day of week
     */
    private static Set<OperatingHours> findMatchingRegularHours(Set<OperatingHours> operatingHoursSet, LocalDate currentDate) {
        if (operatingHoursSet == null) {
            return null;
        }
        //Look for normal operating hours for the day of the week
        Set<OperatingHours> matchingNormalHours = StreamSupport.stream(operatingHoursSet).
                filter(h -> {
                    return h.getStartDay() == DaysOfWeek.lookupFromJodaInt(currentDate.getDayOfWeek());
                }).collect(Collectors.toSet());
        return matchingNormalHours;
    }

    /**
     * Returns a Date with the month and day the string monthDay, and an integer for the year
     */
    public static LocalDate monthAndDayWithYear(String monthDay, int year) {
        return monthDayYearSlashFormatter.parseLocalDate(monthDay + '/' + year);
    }

    /**
     * Compiles hours based on the operatingHours and operatingHoursExceptions passed in.
     * @return HoursLineItem
     *          Contains a leftColumn string, usually with the next seven days on separate lines. Duplicate days have blank lines.
     *          Contains an rightColumn string with, usually with hours for each of the next 7 days on separate lines.
     */
    public static HoursLineItem getWeeklyHoursList(Set<OperatingHours> operatingHours, Set<OperatingHoursException> operatingHoursExceptions, LocalDate startDate) {
        List<HoursLineItem> lineItems = new ArrayList<>(SHOW_HOURS_FOR_DAYS);

        LocalDate date = startDate;

        for (int i = 0; i < SHOW_HOURS_FOR_DAYS; i++) {
            boolean isToday = i == 0;
            String weekDay = isToday ? TODAY_HOURS : date.toString(weekdayPrettyFormat);
            Set<Hours> hoursSet = getHoursByDate(operatingHours, operatingHoursExceptions, date);
            if(hoursSet != null && !hoursSet.isEmpty()) {
                if (lineItems.isEmpty() && !isToday) {
                    //we have the first hours result but they aren't for today so put a STORE_HOURS header
                    lineItems.add(new HoursLineItem(STORE_HOURS, ""));
                }
                lineItems.addAll(daysLineItems(hoursSet, weekDay));
            }
            date = date.plusDays(1);
        }
        return formatWeeklyHoursLineItems(lineItems);
    }

    /**
     * check if there is at least one open Hours. If so format the open ones. If not return a CLOSED
     */
    private static List<HoursLineItem> daysLineItems(Set<Hours> hoursSet, String weekDay) {
        List<HoursLineItem> result = new ArrayList<>();
        Set<Hours> openSet = StreamSupport.stream(hoursSet).filter(hours -> hours.isOpen()).collect(Collectors.toSet());
        if (!openSet.isEmpty()) {
            List<String> dayHours = formatDayHours(openSet, chooseTimeFormat(), " - ");
            StreamSupport.stream(dayHours).forEach(dayHour -> result.add(new HoursLineItem(weekDay, dayHour)));
        } else {
            result.add(new HoursLineItem(weekDay, CLOSED));
        }
        return result;
    }

    /**
     * combines a list of HoursLineItem into a single, multi-line item.
     */
    private static HoursLineItem formatWeeklyHoursLineItems(List<HoursLineItem> hoursLineItemList) {
        List<String> daysList = new ArrayList<>();
        List<String> hoursList = new ArrayList<>();

        String lastDay = null;
        for(int i = 0; i < hoursLineItemList.size(); i++) {
            String dayOfWeek = hoursLineItemList.get(i).getLeftColumn();
            String hour = hoursLineItemList.get(i).getRightColumn();
            if(dayOfWeek.equals(lastDay)) {
                dayOfWeek = "";
            } else {
                lastDay = dayOfWeek;
            }

            daysList.add(dayOfWeek);
            hoursList.add(hour);
        }

        String daysString = Joiner.on('\n').join(daysList);
        String hoursString = Joiner.on('\n').join(hoursList);
        HoursLineItem result = new HoursLineItem(daysString, hoursString);

        return result;
    }

    /**
     *
     * @param hours
     * @param hoursException
     * @param date
     * @return String with operating hours, with each time range on a different line
     */
    public static FormattedHours getMultiLineHoursByDate(Set<OperatingHours> hours, Set<OperatingHoursException> hoursException, LocalDate date) {
        Set<Hours> hoursForDate = HoursUtils.getHoursByDate(hours, hoursException, date);
        String hoursString = Joiner.on('\n')
                .join(formatDayHours(hoursForDate, chooseTimeFormat(), " - "));
        List<String> hoursLabels = StreamSupport.stream(hoursForDate)
                .filter(h -> h.getHoursName().isPresent())
                .map(h -> h.getHoursName().get())
                .collect(Collectors.toList());
        String hoursLabel = Joiner.on('\n').join(hoursLabels);

        return new FormattedHours(hoursString, hoursLabel);
    }

    @NonNull
    private static String chooseTimeFormat() {
        String timeFormat;
        if(LocaleUtils.isNowNextLanguageCode("en")){
            timeFormat = HoursUtils.openCloseTimePrettyFormat;
        } else {
            timeFormat = HoursUtils.openCloseTimeMilitaryFormat;
        }
        return timeFormat;
    }

    /**
     * returns a list of hours ranges, empty if closed
     * hours are sorted by start time
     * @param daysHours - hours
     * @param hoursFormat - format of start and times
     * @param hoursDelimiter - separates start and end time
     */
    public static List<String> formatDayHours(Set<Hours> daysHours, String hoursFormat, String hoursDelimiter) {
        List<String> resultList = StreamSupport.stream(daysHours).filter(hours -> hours.isOpen()).sorted((a,b) -> a.getOpenTime().
                compareTo(b.getOpenTime()))
                .map(hours -> hours.getOpenTime().toString(hoursFormat) + hoursDelimiter + roundCutoffToMidnight(hours.getCloseTime()).toString(hoursFormat))
                .collect(Collectors.toList());
        return resultList;
    }


    /**
     * If after the cutoff round to midnight
     * @return
     */
    public static LocalTime roundCutoffToMidnight(LocalTime time) {
        return !time.isBefore(TIME_CUTOFF) ? LocalTime.MIDNIGHT : time;
    }

    public static boolean isOpenAtTime(Collection<? extends Hours> daysHours, LocalDateTime time ) {
        for ( Hours hours : daysHours) {
            if (hours.isOpenAtTime(time)) {
                return true;
            };
        }
        return false;
    }

    public static boolean isClosedAllDay(Collection<? extends Hours> daysHours) {
        return StreamSupport.stream(daysHours).filter(hours -> hours.isOpen()).count() == 0;
    }

    public static LocalDateTime getDateTimeForMall(Mall mall) {
        LocalDateTime result;
        try {
            DateTimeZone timeZone = DateTimeZone.forID(mall.getTimeZone());
            result = new LocalDateTime(timeZone);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            result = LocalDateTime.now();
        }
        return result;
    }

    /**
     * @param mallHoursOpenFromat - template with one string parameter used if malll is open.
     * @param mallHoursClosedFormat - template with one string parameter used if malll is closed.
     */
    public static void setMallHoursString(Mall mall, TextView mallHoursView, String mallHoursOpenFromat, String mallHoursClosedFormat) {
        LocalDateTime now = getDateTimeForMall(mall);
        Set<Hours> todaysHours = HoursUtils.getHoursByDate(mall.getOperatingHours(), mall.getOperatingHoursExceptions(), now.toLocalDate());
        if (HoursUtils.isClosedAllDay(todaysHours)) {
            mallHoursView.setText(R.string.more_hours_closed_all_day);
        } else {
            //show different messages depending on if mall is presently open or closed today
            String format = (HoursUtils.isOpenAtTime(todaysHours, now) ? mallHoursOpenFromat : mallHoursClosedFormat);
            List<String> hoursStrings = HoursUtils.formatDayHours(todaysHours, chooseTimeFormat(), " - ");
            //It's really unlikely there will be multiple sets of hours but if there are make them not wrap and comma separate them
            hoursStrings = StreamSupport.stream(hoursStrings).map(hoursString -> StringUtils.getNonWrappingString(hoursString)).collect(Collectors.toList());
            String hoursString = Joiner.on(",\n").join(hoursStrings);
            mallHoursView.setText(String.format(format, hoursString));
        }
    }

    public static class FormattedHours {
        @Getter @Setter  String multiLineHoursString;
        @Getter @Setter String hoursLabelString;

        public FormattedHours(String multiLineHoursString, String hoursLabelString) {
            this.multiLineHoursString = multiLineHoursString;
            this.hoursLabelString = hoursLabelString;
        }
    }
}