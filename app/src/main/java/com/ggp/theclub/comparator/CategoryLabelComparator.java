package com.ggp.theclub.comparator;

import com.ggp.theclub.model.Category;

import java.util.Comparator;

public class CategoryLabelComparator implements Comparator<Category> {
    @Override
    public int compare(Category c1, Category c2) {
        if (c1.getLabel() == null) {
            return c2.getLabel() == null ? 0 : -1;
        }
        return c1.getLabel().compareTo(c2.getLabel());
    }
}