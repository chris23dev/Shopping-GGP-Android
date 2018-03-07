package com.ggp.theclub.util;

import android.graphics.Color;

public class ColorUtils {

    public static int createColor(String colorHex, int alphaPercentage) {
        if (!colorHex.startsWith("#")) {
            colorHex = "#" + colorHex;
        }

        int color = Color.parseColor(colorHex);
        int alpha = Math.round((alphaPercentage/100.0f) * 255.0f);
        return android.support.v4.graphics.ColorUtils.setAlphaComponent(color, alpha);
    }
}
