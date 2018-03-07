package com.ggp.theclub.model;

import lombok.Getter;
import lombok.Setter;

public class MapLevel {
    @Getter @Setter int level;
    @Getter @Setter String description;

    public MapLevel(int level, String description) {
        this.level = level;
        this.description = description;
    }
}