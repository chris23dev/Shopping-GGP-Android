package com.ggp.theclub.model;

import java.util.List;

import lombok.Getter;

public class MallConfig {

    @Getter boolean parkAssistEnabled;
    @Getter boolean parkingAvailabilityEnabled;
    @Getter boolean parkingAvailable;
    @Getter private boolean productEnabled;
    @Getter private boolean wayfindingEnabled;
    @Getter private int wayfindingMinDistance;
    @Getter private List<ParkingLotThreshold> lotThresholds;
}
