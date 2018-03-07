package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class ParkingGarage {

    @Getter @Setter private int garageId;
    @Getter @SerializedName("name") private String garageName;
    @Getter @SerializedName("description") private String garageDescription;
    @Getter private double latitude;
    @Getter private double longitude;
}
