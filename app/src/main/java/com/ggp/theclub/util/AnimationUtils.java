package com.ggp.theclub.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avishek.das on 3/8/16.
 */
public class AnimationUtils {
    private static int SPLASH_DURATION = 500;
    private static long ROTATION_DURATION = 50;
    private static int FADE_DURATION = 300;
    //speed in dp/ms
    private static float SCROLL_WHILE_EXPANDING_SPEED = 2;

    public static List<Integer> getRevealCoordinates(View view) {
        List<Integer> locations = new ArrayList<>();

        int[] viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);
        int x = viewLocation[0] + view.getWidth()/2;
        int y = viewLocation[1] + view.getHeight()/2;

        locations.add(x);
        locations.add(y);
        return locations;
    }

    public static void splashReveal(Activity activity, View view, int startX, int startY, Animator.AnimatorListener animatorListener) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int finalRadius = Math.max(width, height);
        view.setVisibility(View.VISIBLE);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, startX, startY, 0, finalRadius);
            anim.setDuration(SPLASH_DURATION);
            anim.setInterpolator(new AccelerateInterpolator());
            anim.addListener(animatorListener);
            anim.start();
        }
    }

    public static void enterReveal(View view, boolean shouldForceReveal) {
        if(view.getVisibility() != View.VISIBLE || shouldForceReveal) {
            // get the center for the clipping circle
            int cx = view.getMeasuredWidth() / 2;
            int cy = view.getMeasuredHeight() / 2;

            int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;
            view.setVisibility(View.VISIBLE);

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && view.isAttachedToWindow()) {
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
                anim.start();
            }
        }
    }
    public static void enterReveal(View view) {
        enterReveal(view, false);
    }

    public static void exitReveal(View view, int finalState) {
        // get the center for the clipping circle
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        int initialRadius = view.getWidth() / 2;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && view.isAttachedToWindow()) {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(finalState);
                }
            });

            anim.start();
        } else {
            view.setVisibility(finalState);
        }
    }

    public static void exitReveal(View view) {
        exitReveal(view, View.GONE);
    }

    public static void rotateTransform(View startView, View endView) {
        float rotateFrom = 0.0f;
        float rotateTo = 10.0f * 360.0f;
        float centerValue = 0.5f;
        RotateAnimation startRotateAnimation = new RotateAnimation(rotateFrom, rotateTo, Animation.RELATIVE_TO_SELF, centerValue, Animation.RELATIVE_TO_SELF, centerValue);
        startRotateAnimation.setDuration(ROTATION_DURATION);
        startRotateAnimation.setRepeatCount(0);
        startRotateAnimation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {}
            @Override
            public void onAnimationRepeat(Animation arg0) {}
            @Override
            public void onAnimationEnd(Animation arg0) {
                startView.setVisibility(View.INVISIBLE);
                endView.setVisibility(View.VISIBLE);

                RotateAnimation endRotateAnimation = new RotateAnimation(rotateFrom, rotateTo, Animation.RELATIVE_TO_SELF, centerValue, Animation.RELATIVE_TO_SELF, centerValue);
                endRotateAnimation.setDuration(ROTATION_DURATION);
                endRotateAnimation.setRepeatCount(0);
                endView.startAnimation(endRotateAnimation);
            }
        });

        startView.startAnimation(startRotateAnimation);
    }

    public static void fadeUpdate(View view, AnimatorListenerAdapter updateAdapter) {
        view.animate()
        .alpha(0f)
        .setDuration(FADE_DURATION/2)
        .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                updateAdapter.onAnimationEnd(animation);
                view.animate()
                        .alpha(1f)
                        .setDuration(FADE_DURATION*2)
                        .setListener(null);
            }
        });
    }

    /**
     *Expands view while staying scrolled to bottom of NestedScrollView regardless of height.
     * This should probably only be used for expanding something at the bottom of a screen.
     * @param scrollLimit - how many dp to scroll down, null for no limit
     */
    public static void expandWhileScrollingDown(final View v, NestedScrollView nestedScrollView, Integer scrollLimit) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        final int initialScrollY = nestedScrollView.getScrollY();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = (int)(targetHeight * interpolatedTime);
                v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT : newHeight;
                v.requestLayout();

                if (scrollLimit == null || newHeight < scrollLimit) {
                    nestedScrollView.setScrollY(newHeight + initialScrollY);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) ((targetHeight / v.getContext().getResources().getDisplayMetrics().density) * SCROLL_WHILE_EXPANDING_SPEED));
        v.startAnimation(a);
    }
}