package com.ggp.theclub.util;

import com.ggp.theclub.model.DateRange;
import com.ggp.theclub.model.Mall;

import java.util.List;

import java8.util.stream.StreamSupport;

public class ApiUtils {
    private static final String BLACK_FRIDAY_HOURS_CODE = "BLACK_FRIDAY_HOURS";
    private static final String HOLIDAY_HOURS_CODE = "HOLIDAY_HOURS";

    public static void populateDateRanges(Mall mall, List<DateRange> dateRanges) {
        StreamSupport.stream(dateRanges).filter(dateRange -> DateRange.Type.FEATURE == dateRange.getType()).forEach(dateRange ->  {
            if (BLACK_FRIDAY_HOURS_CODE.equals(dateRange.getCode())) {
                mall.setBlackFridayHoursDisplayDate(dateRange.getDisplayDate());
                mall.setBlackFridayHoursEndDate(dateRange.getEndDate());
                mall.setBlackFridayHoursUrlPath(dateRange.getUrl());
            } else if (HOLIDAY_HOURS_CODE.equals(dateRange.getCode())) {
                mall.setHolidayHoursDisplayDate(dateRange.getDisplayDate());
                mall.setHolidayHoursStartDate(dateRange.getStartDate());
                mall.setHolidayHoursEndDate(dateRange.getEndDate());
            }
        });
    }
}
