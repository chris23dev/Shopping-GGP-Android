package com.ggp.theclub.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.jibestream.jibestreamandroidlibrary.elements.Route;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.shapes.JPath;

public class MapRoute extends Route {
    private final float ZOOM_THRESHOLD = 0.5f;
    private final float CIRCLE_SIZE_MULTIPLIER = 5f;
    private MapPath mapPath = new MapPath();

    public MapRoute() {
        setShape(mapPath);
    }

    @Override
    public void onUpdate(M m, long timeElapsed, long timeTotal, int fps, Camera camera) {
        super.onUpdate(m, timeElapsed, timeTotal, fps, camera);
        float cameraScale = camera.getScale();
        float cameraRec = 1f / cameraScale;
        if (cameraRec > ZOOM_THRESHOLD) {
            mapPath.setCircleSize(camera.getScale() * CIRCLE_SIZE_MULTIPLIER);
        }
    }

    public class MapPath extends JPath {
        private final float PATH_STROKE_MULTIPLIER = 2.5f;
        private final float CIRCLE_ADVANCE_MULTIPLIER = 10f;
        private float circleSize;
        private float circleAdvance;
        private float circlePhase;
        private float pathLength;
        private PathMeasure pathMeasure = new PathMeasure();
        private Paint pathPaint = new Paint();
        private Paint circlePaint = new Paint();

        public MapPath() {
            pathPaint.setStyle(Paint.Style.STROKE);
            pathPaint.setColor(MallApplication.getApp().getColorById(R.color.green));
            pathPaint.setAntiAlias(true);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setColor(MallApplication.getApp().getColorById(R.color.white));
            circlePaint.setAntiAlias(true);
        }

        private void calc() {
            circleAdvance = circleSize * CIRCLE_ADVANCE_MULTIPLIER;
            pathPaint.setStrokeWidth(circleSize * PATH_STROKE_MULTIPLIER);
        }

        public void setCircleSize(float circleSize) {
            if (this.circleSize != circleSize) {
                this.circleSize = circleSize;
                calc();
            }
        }

        @Override
        public synchronized void setPath(Path path) {
            super.setPath(path);
            pathMeasure.setPath(getPath(), false);
            pathLength = pathMeasure.getLength();
            circlePhase = pathLength;
            calc();
        }

        @Override
        public void onDraw(Canvas canvas, Paint paint) {
            canvas.drawPath(getPath(), pathPaint);
            Path circlePath = new Path();
            circlePath.addCircle(0, 0, circleSize, Path.Direction.CW);
            circlePath.close();
            circlePaint.setPathEffect(new PathDashPathEffect(circlePath, circleAdvance, circlePhase, PathDashPathEffect.Style.ROTATE));
            canvas.drawPath(getPath(), circlePaint);
            if (circlePhase == 0) {
                circlePhase = pathLength;
            } else {
                circlePhase = circlePhase - circleSize;
            }
        }
    }
}