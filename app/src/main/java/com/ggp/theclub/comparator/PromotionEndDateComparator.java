package com.ggp.theclub.comparator;

import com.ggp.theclub.model.Promotion;

import java.util.Comparator;

public class PromotionEndDateComparator implements Comparator<Promotion> {
    @Override
    public int compare(Promotion p1, Promotion p2) {
        if (p1.getEndDateTime() == null) {
            return p2.getEndDateTime() == null ? 0 : -1;
        }
        return p1.getEndDateTime().compareTo(p2.getEndDateTime());
    }
}