package com.ggp.theclub.model;

import lombok.Getter;
import lombok.Setter;

public class HoursLineItem {
    @Getter @Setter private String leftColumn;
    @Getter @Setter private String rightColumn;

    public HoursLineItem(String leftColumn, String rightColumn) {
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }
}
