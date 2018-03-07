package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class CarLocation {

    @Getter @Setter @SerializedName("zone") private String zoneName;
    @Getter @Setter private String map;
    @Getter @Setter private String uuid;
    @Getter @Setter private CarLocationPosition position;

    public class CarLocationPosition {
        @Getter @Setter int x;
        @Getter @Setter int y;
    }
}
