package com.ggp.theclub.model;

import com.ggp.theclub.util.CategoryUtils;

/**
 * holds constants related to featured content url. These special values should not be treated like
 * normal urls
 **/

public class FeaturedContentUrl {
    public static final String PARKING = "screen:PARKING";
    public static final String DIRECTORY = "screen:DIRECTORY";

    public static final String EASTER = "campaign:EASTER";
    public static final String BLACK_FRIDAY = "campaign:BLACK_FRIDAY";
    public static final String VALENTINES = "campaign:VALENTINES";
    public static final String HOLIDAY = "campaign:HOLIDAY";

    public static String getCampaignCategoryCode(String url) {
        switch(url) {
            case EASTER:
                return CategoryUtils.CATEGORY_EASTER;
            case BLACK_FRIDAY:
                return CategoryUtils.CATEGORY_BLACK_FRIDAY;
            case VALENTINES:
                return CategoryUtils.CATEGORY_VALENTINES;
            case HOLIDAY:
                return CategoryUtils.CATEGORY_HOLIDAY;
        }
        return null;
    }
}
