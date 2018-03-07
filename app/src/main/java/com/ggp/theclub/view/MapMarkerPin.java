package com.ggp.theclub.view;

import com.ggp.theclub.R;

public class MapMarkerPin extends MapPin {
    private static final int IMAGE_WIDTH = 80;
    private static final int IMAGE_HEIGHT = 118;

    public MapMarkerPin() {
        super(IMAGE_WIDTH, IMAGE_HEIGHT, R.drawable.marker_pin);
    }
}