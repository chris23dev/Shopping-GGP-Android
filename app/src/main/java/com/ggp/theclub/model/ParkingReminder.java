package com.ggp.theclub.model;

import android.location.Location;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;

public class ParkingReminder {
    @Getter @Setter double latitude;
    @Getter @Setter double longitude;
    @Getter @Setter long savedTime;
    @Getter @Setter String note;

    public ParkingReminder(double lat, double lng){
        init(lat, lng, null);
    }

    public ParkingReminder(double lat, double lng, String note){
        init(lat, lng, note);
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    private void init(double lat, double lng, String note){
        this.latitude = lat;
        this.longitude = lng;
        this.savedTime = DateTime.now().getMillis();
        this.note = note;
    }
}
