package com.ggp.theclub.view;

import android.graphics.Matrix;
import android.graphics.Paint.Style;
import android.os.Handler;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.jibestream.jibestreamandroidlibrary.elements.Amenity;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyle;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyleIcon;
import com.jibestream.jibestreamandroidlibrary.utils.ColorsMaterialDesign;

public class MapAmenity extends Amenity {
    private float[] matrixArray = new float[9];

    public MapAmenity() {
        setStyle();
        getTransform().setScale(0.5f);
        setSelectState(false);
    }

    /**
     * Setting selectState in onCreate makes no difference to setting it in the constructor
    @Override
    public void onCreate(Context context, M m, long timeElapsed, long timeTotal, Camera camera) {
        super.onCreate(context, m, timeElapsed, timeTotal, camera);
        setSelectState(false);
    } */

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
        RenderStyleIcon renderStyleIdleIcon = new RenderStyleIcon();
        renderStyleIdleIcon.renderStyleBG = new RenderStyle("BG", Style.FILL);
        renderStyleIdleIcon.renderStyleMG = new RenderStyle("MG", Style.FILL);
        renderStyleIdleIcon.renderStyleFG = new RenderStyle("FG", Style.FILL);
        renderStyleIdleIcon.renderStyleBG.setFillColor(ColorsMaterialDesign.GREY9);
        renderStyleIdleIcon.renderStyleMG.setFillColor(ColorsMaterialDesign.GREY6);
        renderStyleIdleIcon.renderStyleFG.setFillColor(ColorsMaterialDesign.GREY3);
        renderStyleIdleIcon.renderStyleBG.paintFill.setAntiAlias(true);
        renderStyleIdleIcon.renderStyleMG.paintFill.setAntiAlias(true);
        renderStyleIdleIcon.renderStyleFG.paintFill.setAntiAlias(true);

        RenderStyleIcon renderStyleSelectedIcon = new RenderStyleIcon();
        renderStyleSelectedIcon.renderStyleBG = new RenderStyle("BG", Style.FILL);
        renderStyleSelectedIcon.renderStyleMG = new RenderStyle("MG", Style.FILL);
        renderStyleSelectedIcon.renderStyleFG = new RenderStyle("FG", Style.FILL);
        renderStyleSelectedIcon.renderStyleBG.setFillColor(MallApplication.getApp().getColorById(R.color.blue));
        renderStyleSelectedIcon.renderStyleMG.setFillColor(ColorsMaterialDesign.WHITE);
        renderStyleSelectedIcon.renderStyleFG.setFillColor(ColorsMaterialDesign.WHITE);
        renderStyleSelectedIcon.renderStyleBG.paintFill.setAntiAlias(true);
        renderStyleSelectedIcon.renderStyleMG.paintFill.setAntiAlias(true);

        setStyleSelected(renderStyleSelectedIcon);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStyleIdle(renderStyleIdleIcon);
            }
        }, 100);
    }

    public void setScale(float scale) {
        getTransform().setScale(scale);
    }
}