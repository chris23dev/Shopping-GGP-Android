package com.ggp.theclub.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by avishek.das on 1/15/16.
 */
public class MapsAddress {
    @Getter @Setter Geometry geometry;

    public class Geometry {
        @Getter @Setter MapsLocation location;
    }

    public class MapsLocation {
        @Getter @Setter double lat;
        @Getter @Setter double lng;
    }
}
