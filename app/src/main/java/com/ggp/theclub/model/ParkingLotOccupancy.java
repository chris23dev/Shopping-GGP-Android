package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class ParkingLotOccupancy {

    @Getter @Setter @SerializedName("occ_pct") int occupancyPercentage;
}
