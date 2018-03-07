package com.ggp.theclub.comparator;

import com.ggp.theclub.util.StringUtils;

import java.util.Comparator;

import java8.util.function.Function;

/**
 * Use this to compare something using the app-specific comparison logic, eg ignoring "The"
 */
public class NameSortingComparator<T> implements Comparator<T> {
    Function<T, String> nameFunction;

    public NameSortingComparator(Function<T, String> nameFunction) {
        this.nameFunction = nameFunction;
    }

    @Override
    public int compare(T a, T b) {
        String nameA = nameFunction.apply(a);
        String nameB = nameFunction.apply(b);

        if (nameA == null) {
            return nameB == null ? 0 : -1;
        }
        if (nameB == null) {
            return 1;
        }
        return StringUtils.getNameForSorting(nameA).compareTo(StringUtils.getNameForSorting(nameB));
    }
}
