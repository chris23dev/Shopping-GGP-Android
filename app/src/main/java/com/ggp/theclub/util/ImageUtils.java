package com.ggp.theclub.util;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.view.ImageCircle;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageUtils {
    private static final int MAX_WIDTH = 500;

    public static void loadImage(String imageUrl, ImageView imageView) {
        loadImage(imageUrl, imageView, null);
    }

    public static void loadImage(String imageUrl, ImageView imageView, Callback callback) {
        if(!StringUtils.isEmpty(imageUrl)) {
            Picasso.with(MallApplication.getApp()).load(imageUrl).resize(MAX_WIDTH, 0).onlyScaleDown().into(imageView, callback);
        }
    }

    public static void loadImage(File file, ImageView imageView, boolean resize) {
        if (resize) {
            Picasso.with(MallApplication.getApp()).load(file).memoryPolicy(MemoryPolicy.NO_CACHE).resize(MAX_WIDTH, 0).onlyScaleDown().into(imageView);
        } else {
            Picasso.with(MallApplication.getApp()).load(file).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        }
    }

    public static void fetchImage(String imageUrl) {
        if(!StringUtils.isEmpty(imageUrl)) {
            Picasso.with(MallApplication.getApp()).load(imageUrl).resize(MAX_WIDTH, 0).onlyScaleDown().fetch();
        }
    }

    public static void setLogo(ImageView logoImageView, TextView logoNameView, String logoUrl, String name) {
        if(!StringUtils.isEmpty(logoUrl)) {
            logoNameView.setVisibility(View.GONE);
            logoImageView.setVisibility(View.VISIBLE);
            loadImage(logoUrl, logoImageView);
        } else {
            logoNameView.setVisibility(View.VISIBLE);
            logoImageView.setVisibility(View.GONE);
            if (!StringUtils.isEmpty(name)) {
                logoNameView.setText(name.toUpperCase());
            }
        }
    }

    public static void setCircularLogo(ImageView logoImageView, TextView logoNameView, String logoUrl, String name) {
        if(!StringUtils.isEmpty(logoUrl)) {
            logoImageView.setVisibility(View.VISIBLE);
            Picasso.with(MallApplication.getApp()).load(logoUrl).fit().transform(new ImageCircle()).into(logoImageView);
        } else {
            logoImageView.setVisibility(View.GONE);
            if (!StringUtils.isEmpty(name)) {
                logoNameView.setText(name.substring(0, 1).toUpperCase());
            }
        }
    }

    public static int findMatchingColor(ImageView imageView, int defaultColor) {
        if(imageView.getVisibility() == View.VISIBLE && imageView.getDrawable() != null) {
            Palette p = Palette.from(((BitmapDrawable) imageView.getDrawable()).getBitmap()).generate();

            int vibrantColor = p.getVibrantColor(defaultColor);
            int darkColor = p.getDarkMutedColor(defaultColor);
            int mutedColor = p.getMutedColor(defaultColor);

            if(vibrantColor != defaultColor) {
                return vibrantColor;
            } else if (darkColor != defaultColor) {
                return darkColor;
            } else if (mutedColor != defaultColor){
                return mutedColor;
            }
        }

        return defaultColor;
    }
}