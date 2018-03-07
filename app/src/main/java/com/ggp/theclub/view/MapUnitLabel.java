package com.ggp.theclub.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.util.StringUtils;
import com.jibestream.jibestreamandroidlibrary.elements.UnitLabel;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.utils.ColorsMaterialDesign;

public class MapUnitLabel extends UnitLabel {
    private TextPaint paint = new TextPaint();
    private Rect bounds = new Rect();
    private float textWidth, textHeight, textOffsetY;
    private String ellipsis;
    private String ellipsizedText;

    public MapUnitLabel() {
        setConstantScale(true);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(ColorsMaterialDesign.BLACK);
        paint.setAntiAlias(true);
        ellipsis = MallApplication.getApp().getString(R.string.ellipsis);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        if (!StringUtils.isEmpty(text)) {
            paint.getTextBounds(text, 0, text.length(), bounds);
            textWidth = bounds.width();
            textHeight = bounds.height();
            textOffsetY = bounds.height() * 0.5f;
        }
    }

    @Override
    public void onUpdate(M m, long timeElapsed, long timeTotal, int fps, Camera camera) {
        super.onUpdate(m, timeElapsed, timeTotal, fps, camera);
        float calculatedWidth = width * camera.getZoom();
        float calculatedHeight = height * camera.getZoom();
        if (isTextWithinBounds(calculatedWidth, calculatedHeight)) {
            ellipsizedText = textString;
            setVisible(true);
        } else {
            setEllipsizedText(calculatedWidth);
        }
    }

    @Override
    public void onRender(Canvas canvas, Paint touchPaint) {
        canvas.translate(0, textOffsetY);
        String text = StringUtils.isEmpty(ellipsizedText) ? "" : ellipsizedText;
        canvas.drawText(text, 0, 0, paint);
    }

    private boolean isTextWithinBounds(float calculatedWidth, float calculatedHeight) {
        return textWidth < calculatedWidth && textHeight < calculatedHeight;
    }

    private void setEllipsizedText(float calculatedWidth) {
        int textCharacters = textString.length();
        int maxCharacters = Math.min((int) (textCharacters * calculatedWidth / textWidth), textCharacters) - ellipsis.length();
        if (maxCharacters >= 1) {
            ellipsizedText = textString.substring(0, maxCharacters);
            if (textCharacters > maxCharacters) {
                ellipsizedText = ellipsizedText.concat(ellipsis);
            }
        }
        setVisible(maxCharacters > 0);
    }
}
