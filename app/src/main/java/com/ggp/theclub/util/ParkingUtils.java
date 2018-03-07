package com.ggp.theclub.util;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.model.ParkingTimeRange;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class ParkingUtils {
    private static final int MORNING_HOUR = 10;
    private static final int AFTERNOON_HOUR = 14;
    private static final int EVENING_HOUR = 18;

    public static LocalDateTime getParkingDateTime(ParkingTimeRange timeRange, LocalDate date, String timeZone) {
        LocalDateTime result;
        switch (timeRange) {
            case NOW:
                result = new LocalDateTime(DateTimeZone.forID(timeZone));
                break;
            case MORNING:
                result = new LocalDateTime().withHourOfDay(MORNING_HOUR).withMinuteOfHour(0);
                break;
            case AFTERNOON:
                result = new LocalDateTime().withHourOfDay(AFTERNOON_HOUR).withMinuteOfHour(0);
                break;
            case EVENING:
                result = new LocalDateTime().withHourOfDay(EVENING_HOUR).withMinuteOfHour(0);
                break;
            default: result = new LocalDateTime();
        }
        return result.withDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

    public static String getTimeRangeText(ParkingTimeRange timeRange) {
        int id;
        switch (timeRange) {
            case NOW:
                id = R.string.parking_availability_now;
                break;
            case MORNING:
                id = R.string.parking_availability_morning;
                break;
            case AFTERNOON:
                id = R.string.parking_availability_afternoon;
                break;
            case EVENING:
                id = R.string.parking_availability_evening;
                break;
            default:
                id = R.string.parking_availability_now;
        }

        return MallApplication.getApp().getResources().getString(id);
    }
}