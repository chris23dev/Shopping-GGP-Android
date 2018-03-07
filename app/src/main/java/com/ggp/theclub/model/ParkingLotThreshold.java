package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/*
    Based on occupancy percentage, we'll display parking lots as a specific color.
    This class should be used to determine what the color will be.
 */
public class ParkingLotThreshold {
    @Getter @Setter @SerializedName("min") int minPercentage;
    @Getter @Setter @SerializedName("max") int maxPercentage;
    @Getter @Setter @SerializedName("alpha") int alphaPercentage;
    @Getter @Setter String colorHex;
}