package com.ggp.theclub.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.jibestream.jibestreamandroidlibrary.elements.YouAreHere;
import com.jibestream.jibestreamandroidlibrary.main.Camera;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.shapes.Image;
import com.jibestream.jibestreamandroidlibrary.utils.Ninegrid;

public class MapStartPin extends YouAreHere {
    private final int IMAGE_WIDTH = 60;
    private final int IMAGE_HEIGHT = 90;
    private final int NINEGRID_SIZE = 8;
    private final int MATRIX_ARRAY_SIZE = 9;
    private float[] matrixArray = new float[MATRIX_ARRAY_SIZE];

    public MapStartPin() {
        Bitmap bitmap = BitmapFactory.decodeResource(MallApplication.getApp().getResources(), R.drawable.start_pin);
        Image image = new Image(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, new Ninegrid(NINEGRID_SIZE));
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