package com.ggp.theclub.event;

import lombok.Getter;
import lombok.Setter;

public class MapReadyEvent {
    @Getter @Setter private boolean mapReady;

    public MapReadyEvent(boolean mapReady) {
        setMapReady(mapReady);
    }
}