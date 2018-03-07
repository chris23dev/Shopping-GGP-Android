package com.ggp.theclub.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class AlertUtils {
    public static void showDispositioningMallDialog(Activity activity) {
        String title = MallApplication.getApp().getString(R.string.mall_select_dispositioning_title);
        String message = MallApplication.getApp().getString(R.string.mall_select_dispositioning_message);
        String dismissButtonLabel = MallApplication.getApp().getString(R.string.change_mall_button);
        showGenericDialog(title, message, dismissButtonLabel, activity);
    }

    public static void showGenericDialog(String title, String message, String dismissButtonLabel, Activity activity, Runnable onDismissRunnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (!StringUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!StringUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        builder.setNegativeButton(dismissButtonLabel, (dialog, which) -> {
            dialog.dismiss();
            if (onDismissRunnable != null) {
                onDismissRunnable.run();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showGenericDialog(String title, String message, String dismissButtonLabel, Activity activity) {
        showGenericDialog(title, message, dismissButtonLabel, activity, null);
    }

    public static void showDiscardDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.discard_dialog_title)
                .setMessage(R.string.discard_dialog_message)
                .setPositiveButton(R.string.discard_dialog_positive, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.discard_dialog_negative, (dialog, which) -> activity.finish())
                .create()
                .show();
    }

    public static void showAppUpdateDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.invalid_version_dialog_title)
                .setMessage(R.string.invalid_version_dialog_text)
                .setNegativeButton(R.string.invalid_version_dialog_button, (dialog, which) -> IntentUtils.showAppInStore(activity))
                .setCancelable(false)
                .create()
                .show();
    }

    public static void showSweepstakesSuccessDialog(Activity activity, Runnable runnable) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.sweepstakes_success_title)
                .setMessage(R.string.sweepstakes_registration_complete_message)
                .setNegativeButton(R.string.done_alert, (dialog, which) -> {
                    runnable.run();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * Circles a view in a dismissable dialog.
     * This showcase will only be shown once for a given singleUseKey
     */
    public static void showShowcaseAlert(Activity activity, View target, int titleText, int mainText, int dismissText, String singleUseKey ) {
        new MaterialShowcaseView.Builder(activity)
                .setTarget(target)
                .setDismissText(dismissText)
                .setTitleText(titleText)
                .setContentText(mainText)
                .singleUse(singleUseKey)
                .setDelay(300)
                .setMaskColour(MallApplication.getApp().getColorById(R.color.primary_blue_overlay))
                .setContentTextColor(MallApplication.getApp().getColorById(R.color.white))
                .setTitleTextColor(MallApplication.getApp().getColorById(R.color.white))
                .setTargetTouchable(true)
                .setDismissOnTargetTouch(true)
                .setDismissOnTouch(true)
                .show();
    }
}