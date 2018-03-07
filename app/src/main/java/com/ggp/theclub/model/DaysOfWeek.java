package com.ggp.theclub.model;

import org.joda.time.DateTimeConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for Days of Week. Each enum also has an integer value representing its day number.
 */
public enum DaysOfWeek {
    SUN, MON, TUE, WED, THU, FRI, SAT;

    private static Map<Integer, DaysOfWeek> jodaLookupMap = new HashMap(){{
        put(DateTimeConstants.SUNDAY, SUN);
        put(DateTimeConstants.MONDAY, MON);
        put(DateTimeConstants.TUESDAY, TUE);
        put(DateTimeConstants.WEDNESDAY, WED);
        put(DateTimeConstants.THURSDAY, THU);
        put(DateTimeConstants.FRIDAY, FRI);
        put(DateTimeConstants.SATURDAY, SAT);
    }};

    public static DaysOfWeek lookupFromJodaInt(int jodaDayOfWeek) {
        return jodaLookupMap.get(jodaDayOfWeek);
    }


}