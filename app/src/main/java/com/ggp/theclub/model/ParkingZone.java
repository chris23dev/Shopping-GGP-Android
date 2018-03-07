package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class ParkingZone {

    @Getter @Setter @SerializedName("name") private String zoneName = "";
    @Getter private ParkingZoneCounts counts = new ParkingZoneCounts();

    public class ParkingZoneCounts {
        @Getter @Setter private int available;
        @Getter @Setter private int occupied;
    }
}
