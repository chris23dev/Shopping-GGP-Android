package com.ggp.theclub.manager.impl;

import android.app.Activity;
import android.app.AlertDialog;

import com.ggp.theclub.BuildConfig;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.FeedbackActivity;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.manager.FeedbackManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.model.MobileConfig;
import com.ggp.theclub.util.IntentUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackManagerImpl implements FeedbackManager {
    //if feedback count is set to this don't do anything
    private final int DONT_REQUEST_FEEDBACK = -1;
    //after this many feedback events, request feedback.
    private int FEEDBACK_EVENT_REQUEST_LIMIT = 25;

    public FeedbackManagerImpl() {
        fetchMaxFeedbackEventCount();
    }

    public void incrementFeedbackEventCount(Activity activity) {
        if (!haveResetCountThisVersion()) {
            resetCountForThisVersion();
        }
        int feedbackEventCount = PreferencesManager.getInstance().getFeedbackEventCount();
        if (feedbackEventCount != DONT_REQUEST_FEEDBACK) {
            feedbackEventCount++;
            if (feedbackEventCount >= FEEDBACK_EVENT_REQUEST_LIMIT) {
                setFeedbackEventCount(DONT_REQUEST_FEEDBACK);
                showRequestFeedbackDialog(activity);
            } else {
                setFeedbackEventCount(feedbackEventCount);
            }
        }
    }

    private void fetchMaxFeedbackEventCount() {
        MallApiClient.getInstance().getMallApi().getMobileConfig().enqueue(new Callback<MobileConfig>() {
            @Override
            public void onResponse(Call<MobileConfig> call, Response<MobileConfig> response) {
                if (response.isSuccessful() && response.body().getAndroidFeedbackActionCount() > 0) {
                    FEEDBACK_EVENT_REQUEST_LIMIT = response.body().getAndroidFeedbackActionCount();
                }
            }

            @Override
            public void onFailure(Call<MobileConfig> call, Throwable t) {

            }
        });
    }

    private void setFeedbackEventCount(int feedbackEventCount) {
        PreferencesManager.getInstance().setFeedbackEventCount(feedbackEventCount);
    }

    private boolean haveResetCountThisVersion() {
        return (PreferencesManager.getInstance().getFeedbackCountResetVersionCode() == BuildConfig.VERSION_CODE);
    }

    private void resetCountForThisVersion() {
        setFeedbackEventCount(0);
        PreferencesManager.getInstance().setFeedbackCountResetVersionCode(BuildConfig.VERSION_CODE);
    }

    private void showRequestFeedbackDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setCancelable(false);
        builder.setMessage(R.string.feedback_text);
        builder.setNegativeButton(R.string.feedback_no_button, (dialog, which) -> {
            showDislikeDialog(activity);
            dialog.dismiss();
        });
        builder.setPositiveButton(R.string.feedback_yes_button, (dialog, which) -> {
            showLikeDialog(activity);
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showLikeDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setCancelable(false);
        builder.setMessage(R.string.like_app_text);
        builder.setPositiveButton(R.string.feedback_next_no_button, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.like_app_remind_button, (dialog, which) -> {
            setFeedbackEventCount(0);
            dialog.dismiss();
        });
        builder.setNeutralButton(R.string.like_app_yes_button, (dialog, which) -> {
            IntentUtils.showAppInStore(activity);
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showDislikeDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setCancelable(false);
        builder.setMessage(R.string.dislike_app_text);
        builder.setPositiveButton(R.string.feedback_next_no_button, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.dislike_app_yes_button, (dialog, which) -> {
            activity.startActivity(FeedbackActivity.buildIntent(activity));
            dialog.dismiss();
        });
        builder.create().show();
    }
}