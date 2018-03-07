package com.ggp.theclub.event;

import android.location.Location;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by avishek.das on 1/26/16.
 */
public class LocationChangeEvent {
    @Getter @Setter private Location location;

    public LocationChangeEvent(Location location) {
        this.location = location;
    }
}
