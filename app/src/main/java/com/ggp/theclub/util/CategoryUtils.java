package com.ggp.theclub.util;

import com.ggp.theclub.comparator.CategoryLabelComparator;
import com.ggp.theclub.model.Category;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class CategoryUtils {

    public static final String CATEGORY_ALL_STORES = "ALL_STORES";
    public static final String CATEGORY_ALL_SALES = "ALL_SALES";
    public static final String CATEGORY_CODE_ALL_PREFIX = "ALL_";
    public static final String CATEGORY_LABEL_ALL_PREFIX = "All ";
    public static final String CATEGORY_FOOD = "FOOD";
    public static final String CATEGORY_FASHION = "FASHION";
    public static final String CATEGORY_SPORTS = "SPORTS_FITNESS";
    public static final String CATEGORY_HEALTH_BEAUTY = "HEALTH_BEAUTY";
    public static final String CATEGORY_ELECTRONICS = "ELECTRONICS_ENTERTAINMENT";
    public static final String CATEGORY_HOUSEWARES = "HOME_HOUSEWARES";
    public static final String CATEGORY_SHOES = "SHOES";
    public static final String CATEGORY_SPECIALTY = "SPECIALTY";
    public static final String CATEGORY_DEPARTMENT_STORES = "DEPARTMENT_STORES";
    public static final String CATEGORY_STORE_OPENING = "STORE_OPENING";
    public static final String CATEGORY_MY_FAVORITES = "MY_FAVORITES";
    /*--Campaign Categories--*/
    public static final String CATEGORY_BACK_TO_SCHOOL = "BACK_TO_SCHOOL";
    public static final String CATEGORY_BLACK_FRIDAY = "BLACK_FRIDAY";
    public static final String CATEGORY_EASTER = "EASTER";
    public static final String CATEGORY_FALL_FASHION = "FALL_FASHION";
    public static final String CATEGORY_FATHERS_DAY = "FATHERS_DAY";
    public static final String CATEGORY_GRADUATION = "GRADUATION";
    public static final String CATEGORY_HOLIDAY = "HOLIDAY";
    public static final String CATEGORY_MOTHERS_DAY = "MOTHERS_DAY";
    public static final String CATEGORY_VALENTINES = "VALENTINES";

    public static String getDisplayNameForCategories(List<Category> categories) {
        return categories == null ? "" : StreamSupport.stream(categories)
                .filter(c -> c.getCode() != null && !c.getCode().equals(CATEGORY_STORE_OPENING))
                .sorted(new CategoryLabelComparator())
                .map(Category::getLabel)
                .collect(Collectors.joining(", "));
    }

    public static <T extends Category> List<T> mapChildCategories(List<T> allCategories) {
        if (allCategories == null) {
            return new ArrayList<>();
        }

        List<T> parentCategories = StreamSupport.stream(allCategories)
                .filter(c -> c.getParentId() == 0)
                .collect(Collectors.toList());

        for (T parent : parentCategories) {
            List<T> childCategories = StreamSupport.stream(allCategories)
                    .filter(c -> c.getParentId() == parent.getId())
                    .collect(Collectors.toList());

            parent.setChildCategories(childCategories);
        }

        return parentCategories;
    }

    public static boolean categoriesContainCode(List<Category> categories, String categoryCode) {
        if (categoryCode == null || categoryCode.equals(CATEGORY_ALL_STORES)) {
            return true;
        }

        return categories != null && StreamSupport.stream(categories)
                .map(Category::getCode)
                .collect(Collectors.toList())
                .contains(categoryCode);
    }
}