package com.ggp.theclub.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.ggp.theclub.R;
import com.ggp.theclub.util.AnimationUtils;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class ValidationIndicator extends FrameLayout {
    @BindColor(R.color.black) int defaultColor;
    @BindColor(R.color.red) int errorColor;
    @Bind(R.id.message_view) AppCompatTextView messageView;
    @Bind(R.id.ok_icon) AppCompatTextView okIcon;
    @Bind(R.id.error_icon) AppCompatTextView errorIcon;

    public ValidationIndicator(Context context) {
        super(context);
        init();
    }

    public ValidationIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public ValidationIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.validation_indicator, this);
        ButterKnife.bind(this);
    }

    public void setDefaultColor(int color) {
        defaultColor = color;
        messageView.setTextColor(defaultColor);
    }

    public void setDefaultMessage(String message) {
        messageView.setText(message);
        messageView.setTextColor(defaultColor);
        AnimationUtils.enterReveal(messageView);
    }

    public void setMessage(String message, boolean isValid) {
        messageView.setText(message);
        messageView.setTextColor(isValid ? defaultColor : errorColor);
        AnimationUtils.enterReveal(messageView);
        showIcon(isValid);
    }

    public void showIcon(boolean valid) {
        View viewToDisplay = valid ? okIcon : errorIcon;
        View viewToHide = valid ? errorIcon : okIcon;
        if (viewToDisplay.getVisibility() != View.VISIBLE && viewToHide.getVisibility() != View.VISIBLE) {
            AnimationUtils.enterReveal(viewToDisplay);
        } else if (viewToDisplay.getVisibility() != View.VISIBLE) {
            AnimationUtils.rotateTransform(viewToHide, viewToDisplay);
        }
    }

    public boolean isIconVisible() {
        boolean okVisible = okIcon.getVisibility() == VISIBLE;
        boolean errorVisible = errorIcon.getVisibility() == VISIBLE;
        return okVisible || errorVisible;
    }

    public void reset() {
        AnimationUtils.exitReveal(okIcon);
        AnimationUtils.exitReveal(errorIcon);
        AnimationUtils.exitReveal(messageView, INVISIBLE);
        messageView.setText(null);
    }
}
