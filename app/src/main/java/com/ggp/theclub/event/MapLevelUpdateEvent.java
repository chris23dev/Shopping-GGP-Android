package com.ggp.theclub.event;

import com.ggp.theclub.model.MapMoverType;

import lombok.Getter;
import lombok.Setter;

public class MapLevelUpdateEvent {
    @Getter @Setter private int level;
    @Getter @Setter private boolean wayfindLevel;
    @Getter @Setter private boolean startWayfindLevel;
    @Getter @Setter private boolean endWayfindLevel;
    @Getter @Setter private MapMoverType mapMoverType;
    @Getter @Setter private String textDirection;

    public MapLevelUpdateEvent(int level) {
        setLevel(level);
    }
}