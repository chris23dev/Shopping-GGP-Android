package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ParkingLotsResponse {
    @Getter @Setter @SerializedName("Facilities") List<ParkingLot> parkingLots;
}