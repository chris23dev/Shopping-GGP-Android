package com.ggp.theclub.view;

import android.graphics.Paint.Style;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.jibestream.jibestreamandroidlibrary.elements.Unit;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyle;

public class MapUnit extends Unit {
    public MapUnit() {
        styleSelected = new RenderStyle(Style.FILL);
        styleSelected.setFillColor(MallApplication.getApp().getColorById(R.color.map_unit_select));
        styleHighlighted.setFillColor(MallApplication.getApp().getColorById(R.color.map_unit_highlight));
    }
}