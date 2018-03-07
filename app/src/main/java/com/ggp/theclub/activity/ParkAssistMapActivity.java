package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.model.CarLocation.CarLocationPosition;
import com.ggp.theclub.util.StringUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class ParkAssistMapActivity extends BaseActivity {
    @Bind(R.id.map_view) ImageViewTouch mapView;
    private String mapUrl;
    private CarLocationPosition carLocationPosition;

    public static Intent buildIntent(Context context, String mapUrl, CarLocationPosition carLocationPosition) {
        return buildIntent(context, ParkAssistMapActivity.class, mapUrl, carLocationPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapUrl = getIntentExtra(String.class);
        carLocationPosition = getIntentExtra(CarLocationPosition.class);
        setContentView(R.layout.park_assist_map_activity);
    }

    @Override
    protected void configureView() {
        enableBackButton();
        setTitle(R.string.park_assist_map_title);

        if(!StringUtils.isEmpty(mapUrl)) {
            Picasso.with(MallApplication.getApp()).load(mapUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Bitmap mapBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Bitmap pinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.park_assist_pin);
                    Canvas canvas = new Canvas(mapBitmap);
                    canvas.drawBitmap(mapBitmap, new Matrix(), null);
                    canvas.drawBitmap(pinBitmap, getPinXPosition(pinBitmap.getWidth()), getPinYPosition(pinBitmap.getHeight()), null);
                    mapView.setImageBitmap(mapBitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            });
        }
    }

    private int getPinXPosition(int pinWidth) {
        return carLocationPosition.getX() - (int)(pinWidth * 0.75);
    }

    private int getPinYPosition(int pinHeight) {
        return carLocationPosition.getY() - pinHeight;
    }
}