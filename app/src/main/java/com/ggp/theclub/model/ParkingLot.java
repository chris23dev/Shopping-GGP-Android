package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ParkingLot {
    @Getter @Setter @SerializedName("f_id") int parkingLotId;
    @Getter @Setter @SerializedName("ppoly_arr") List<String> encodedPolylines;
    @Getter @Setter @SerializedName("occupancy") List<ParkingLotOccupancy> occupancies;
}