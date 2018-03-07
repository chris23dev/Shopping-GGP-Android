package com.ggp.theclub.model;

import com.ggp.theclub.util.StringUtils;

public enum MapMoverType {
    Stairs("Stair Case"),
    Escalator("Escalator"),
    Elevator("Elevator"),
    Pin("Pin");

    private final String moverDescription;

    MapMoverType(String moverDescription) {
        this.moverDescription = moverDescription;
    }

    public static MapMoverType fromString(String moverDescription) {
        if (StringUtils.isEmpty(moverDescription)) {
            moverDescription = Pin.name();
        }
        for (MapMoverType mapMoverType : MapMoverType.values()) {
            if (moverDescription.equalsIgnoreCase(mapMoverType.moverDescription)) {
                return mapMoverType;
            }
        }
        return null;
    }
}