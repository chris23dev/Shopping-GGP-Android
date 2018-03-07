package com.ggp.theclub.model;

import lombok.Getter;

public enum MapAmenityFilter {
    RESTROOM ("Restroom"),
    ATM ("ATM"),
    KIOSK ("Kiosk"),
    MANAGEMENT ("Management"),
    NONE ("None");

    @Getter private final String amenityFilter;

    MapAmenityFilter(String amenityFilter) {
        this.amenityFilter = amenityFilter;
    }
}