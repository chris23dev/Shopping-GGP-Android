package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

public class ParkingSite {

    @Getter @SerializedName("key") private String secret;
    @Getter @SerializedName("site") private String siteName;
    @Getter private List<ParkingGarage> garages;
    @Getter private List<ParkingLevel> levels;
}
