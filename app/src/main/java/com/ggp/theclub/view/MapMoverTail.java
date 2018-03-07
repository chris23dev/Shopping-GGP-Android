package com.ggp.theclub.view;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.jibestream.jibestreamandroidlibrary.elements.MoverTail;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyle;
import com.jibestream.jibestreamandroidlibrary.styles.RenderStyleIcon;
import com.jibestream.jibestreamandroidlibrary.utils.ColorsMaterialDesign;

public class MapMoverTail extends MoverTail {
    private final int MAX_RIPPLES = 2;
    private final int RIPPLE_WIDTH = 4;
    private final float RIPPLE_MULTIPLIER = 1.5f;
    private boolean rippleActive;
    private int rippleCount;
    private float rippleRadius;
    private float rippleMinRadius;
    private float rippleMaxRadius;
    private Paint ripplePaint = new Paint();
    private float[] matrixArray = new float[9];

    public MapMoverTail() {
        setStyle();
        setSelectable(true);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            rippleActive = true;
            rippleCount = 0;
            rippleMinRadius = getShape() != null ? getShape().getBBox().width() / 2 : 0;
            rippleMaxRadius = rippleMinRadius * RIPPLE_MULTIPLIER;
            rippleRadius = rippleMinRadius;
        }
    }

    @Override
    public void onUpdate(M m, long timeElapsed, long timeTotal, int fps, Camera camera) {
        super.onUpdate(m, timeElapsed, timeTotal, fps, camera);
        if (rippleActive) {
            rippleRadius++;
            if (rippleRadius > rippleMaxRadius) {
                rippleRadius = rippleMinRadius;
                rippleCount++;
                if (rippleCount == MAX_RIPPLES) {
                    rippleActive = false;
                }
            }
        }
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

    @Override
    public void onRender(Canvas canvas, Paint touchPaint) {
        super.onRender(canvas, touchPaint);
        if (rippleActive) {
            canvas.drawCircle(0, 0, rippleRadius, ripplePaint);
        }
    }

    private void setStyle() {
        int color = MallApplication.getApp().getColorById(R.color.green);

        RenderStyleIcon renderStyleIcon = new RenderStyleIcon();
        renderStyleIcon.renderStyleBG = new RenderStyle("BG", Style.FILL);
        renderStyleIcon.renderStyleMG = new RenderStyle("MG", Style.FILL);
        renderStyleIcon.renderStyleFG = new RenderStyle("FG", Style.FILL);
        renderStyleIcon.renderStyleBG.setFillColor(ColorsMaterialDesign.GREY9);
        renderStyleIcon.renderStyleMG.setFillColor(color);
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

        ripplePaint.setColor(color);
        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setStrokeWidth(RIPPLE_WIDTH);
        ripplePaint.setAntiAlias(true);
    }
}