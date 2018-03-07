package com.ggp.theclub.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.ggp.theclub.MallApplication;
import com.jibestream.jibestreamandroidlibrary.elements.Pin;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.shapes.Image;
import com.jibestream.jibestreamandroidlibrary.utils.Ninegrid;

public class MapPin extends Pin {
    private final int NINEGRID_SIZE = 8;
    private final int MATRIX_ARRAY_SIZE = 9;
    private float[] matrixArray = new float[MATRIX_ARRAY_SIZE];

    public MapPin(int width, int height, int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(MallApplication.getApp().getResources(), drawable);
        Image image = new Image(bitmap, width, height, new Ninegrid(NINEGRID_SIZE));
        setShape(image);
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
}