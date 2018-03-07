package com.ggp.theclub.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class MapState {
    @Getter @Setter private String UID = UUID.randomUUID().toString();
    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float scale;
    @Getter @Setter private float rotation;
    @Getter @Setter private int mapId;
    @Getter @Setter private boolean selectionEnabled;
    @Getter @Setter private int selection;
    @Getter @Setter private List<Integer> highlights = new ArrayList<>();
    @Getter @Setter private MapAmenityFilter mapAmenityFilter = MapAmenityFilter.NONE;
}