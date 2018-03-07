package com.ggp.theclub.util;

import android.graphics.PorterDuff;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;

public class ViewUtils {

    public interface OnClickListener {
        public void onClick();
    }

    public static void setPasswordTextVisibility(EditText inputView, boolean isVisible) {
        //keeps current cursor position after transforming
        int selectionStart = inputView.getSelectionStart();
        int selectionEnd = inputView.getSelectionEnd();
        inputView.setTransformationMethod(isVisible ? null : new PasswordTransformationMethod());
        inputView.setSelection(selectionStart, selectionEnd);
    }

    public static void setHtmlText(TextView view, String text) {
        if(!StringUtils.isEmpty(text)) {
            view.setText(Html.fromHtml(text));
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * Sets text in textview with a clickable portion
     * @param textView - textview to make clickable
     * @param beforeClickableText - may be null
     * @param clickableText
     * @param afterClickableText - may be null
     * @param onClickListener - has onClick method for when clickable part is clicked
     */
    public static void setClickableSpan(TextView textView, String beforeClickableText, String clickableText, String afterClickableText, OnClickListener onClickListener) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }

            //remove underline
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(MallApplication.getApp().getColorById(R.color.blue));
            }
        };
        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(beforeClickableText)) {
            builder.append(beforeClickableText);
        }
        //clickable part should not wrap
        builder.append(StringUtils.getNonWrappingString(clickableText));
        if (!StringUtils.isEmpty(afterClickableText)) {
            builder.append(afterClickableText);
        }

        String noWrapText = builder.toString();
        SpannableString spannableString = new SpannableString(noWrapText);

        int clickableStartIndex = StringUtils.isEmpty(beforeClickableText) ? 0 : beforeClickableText.length();
        int clickableEndIndex = clickableStartIndex + clickableText.length();
        spannableString.setSpan(clickableSpan, clickableStartIndex, clickableEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setProgressBarColor(ProgressBar progressBar, int color) {
        progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }
}