package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class ParkingLevel {

    @Getter @Setter private int levelId;
    @Getter @Setter private int garageId;
    @Getter @Setter @SerializedName("name") private String levelName;
    @Getter @Setter @SerializedName("zone") private String zoneName;
    @Getter @Setter @SerializedName("description") private String levelDescription;
    @Getter @Setter private int sort;
}
