package com.ggp.theclub.view;

import android.graphics.Matrix;
import android.graphics.Paint.Style;

import com.jibestream.jibestreamandroidlibrary.elements.ElementIcon;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.dataObjects.Waypoint;
import com.jibestream.jibestreamandroidlibrary.shapes.IconShape;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyle;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyleIcon;
import com.jibestream.jibestreamandroidlibrary.utils.ColorsMaterialDesign;

public class MapMover extends ElementIcon {
    private float[] matrixArray = new float[9];

    public MapMover(Waypoint waypoint, IconShape iconShape, int level) {
        setStyle();
        setShape(iconShape);
        setLevel(level);
        setSelectable(true);
        getTransform().setTranslationX((float) waypoint.x);
        getTransform().setTranslationY((float) waypoint.y);
        getTransform().setScale(0.5f);
    }

    @Override
    public void onPreRender(M m, long timeElapsed, long timeTotal, int fps, Camera camera) {
        super.onPreRender(m, timeElapsed, timeTotal, fps, camera);
        float cameraScale = camera.getScale();
        float cameraRec = 1f / cameraScale;
        float zoomThreshold = 2f;
        if (cameraRec > zoomThreshold) {
            getTransformation().getValues(matrixArray);
            float x = matrixArray[Matrix.MTRANS_X];
            float y = matrixArray[Matrix.MTRANS_Y];
            getTransformation().postScale(cameraScale, cameraScale, x, y);
            getTransformation().postScale(zoomThreshold, zoomThreshold, x, y);
        }
    }

    private void setStyle() {
        RenderStyleIcon renderStyleIcon = new RenderStyleIcon();
        renderStyleIcon.renderStyleBG = new RenderStyle("BG", Style.FILL);
        renderStyleIcon.renderStyleMG = new RenderStyle("MG", Style.FILL);
        renderStyleIcon.renderStyleFG = new RenderStyle("FG", Style.FILL);
        renderStyleIcon.renderStyleBG.setFillColor(ColorsMaterialDesign.GREY9);
        renderStyleIcon.renderStyleMG.setFillColor(ColorsMaterialDesign.GREY6);
        renderStyleIcon.renderStyleFG.setFillColor(ColorsMaterialDesign.GREY3);
        renderStyleIcon.renderStyleBG.paintFill.setAntiAlias(true);
        renderStyleIcon.renderStyleMG.paintFill.setAntiAlias(true);
        renderStyleIcon.renderStyleFG.paintFill.setAntiAlias(true);
        setStyleIdle(renderStyleIcon);
    }
}