package com.ggp.theclub.view;

import android.graphics.Matrix;
import android.graphics.Paint.Style;
import android.os.Handler;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.jibestream.jibestreamandroidlibrary.elements.MoverHead;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyle;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyleIcon;
import com.jibestream.jibestreamandroidlibrary.utils.ColorsMaterialDesign;

public class MapMoverHead extends MoverHead {
    private float[] matrixArray = new float[9];

    public MapMoverHead() {
        setStyle();
        setSelectable(true);
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
        renderStyleIcon.renderStyleMG.setFillColor(MallApplication.getApp().getColorById(R.color.green));
        renderStyleIcon.renderStyleFG.setFillColor(ColorsMaterialDesign.GREY3);
        renderStyleIcon.renderStyleBG.paintFill.setAntiAlias(true);
        renderStyleIcon.renderStyleMG.paintFill.setAntiAlias(true);
        renderStyleIcon.renderStyleFG.paintFill.setAntiAlias(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStyleIdle(renderStyleIcon);
            }
        }, 100);
    }
}