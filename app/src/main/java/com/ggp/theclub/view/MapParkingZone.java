package com.ggp.theclub.view;

import com.jibestream.jibestreamandroidlibrary.elements.ElementCustom;
import com.jibestream.jibestreamandroidlibrary.utils.ColorsMaterialDesign;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MapParkingZone extends ElementCustom {
    @Getter @Setter private List<Integer> parkingZoneLevels = new ArrayList<>();
    @Getter @Setter private List<Integer> parkingZoneIds = new ArrayList<>();

    public MapParkingZone() {
        resetColor();
        setHighlightable(true);
    }

    public void setColor(int color) {
        styleHighlighted.setFillColor(color);
    }

    public void resetColor() {
        styleHighlighted.setFillColor(ColorsMaterialDesign.TRANSPARENT);
    }

    public boolean isHighlighted() {
        return styleHighlighted.paintFill.getColor() != ColorsMaterialDesign.TRANSPARENT;
    }
}