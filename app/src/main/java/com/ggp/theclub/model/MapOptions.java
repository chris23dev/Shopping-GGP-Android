package com.ggp.theclub.model;

import lombok.Getter;
import lombok.Setter;

public class MapOptions {
    @Getter @Setter double latitude;
    @Getter @Setter double longitude;
    @Getter @Setter float initialZoomLevel = 16;
    @Getter @Setter float placesZoomLevelThreshold = 15;
    @Getter @Setter boolean indoorsMapEnabled = false;
    @Getter @Setter boolean gesturesEnabled = true;
    @Getter @Setter boolean currentLocationEnabled = false;
    @Getter @Setter boolean anchorStoreMarkersEnabled = true;
    @Getter @Setter boolean mapToolbarEnabled = false;

    public MapOptions(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}