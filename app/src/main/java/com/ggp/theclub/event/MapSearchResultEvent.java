package com.ggp.theclub.event;

import com.ggp.theclub.model.MapsAddress;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by avishek.das on 1/14/16.
 */
public class MapSearchResultEvent {
    @Getter @Setter private MapsAddress address;
    @Getter private String search;

    public MapSearchResultEvent(MapsAddress address, String search) {
        this.address = address;
        this.search = search;
    }
}
