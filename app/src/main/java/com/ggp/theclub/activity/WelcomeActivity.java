package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.DeepLinkingManager;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.ViewUtils;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;

public class WelcomeActivity extends BaseActivity {
    private final int LOADING_TIMEOUT = 2000;
    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private DeepLinkingManager deepLinkingManager = MallApplication.getApp().getDeepLinkingManager();

    @Bind(R.id.welcome_message) TextView welcomeMessageView;
    @Bind(R.id.mall_hours) TextView mallHoursView;
    @Bind(R.id.loading_indicator) ProgressBar loadingIndicator;
    @BindString(R.string.welcome_message_format) String welcomeMessageFormat;
    @BindString(R.string.more_hours_open_format) String todaysHoursOpenFormat;
    @BindString(R.string.more_hours_closed_format) String todaysHoursClosedFormat;
    @BindColor(R.color.white) int white;

    public static Intent buildIntent(Context context) {
        return new Intent(context, WelcomeActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
    }

    @Override
    public void configureView() {
        ViewUtils.setProgressBarColor(loadingIndicator, white);
        String welcomeMessage = String.format(welcomeMessageFormat, mallManager.getMall().getName(), accountManager.getCurrentUser().getFirstName());
        welcomeMessageView.setText(welcomeMessage);
        HoursUtils.setMallHoursString(mallManager.getMall(), mallHoursView, todaysHoursOpenFormat, todaysHoursClosedFormat);

        new Handler().postDelayed(() -> startActivity(MainActivity.buildIntent(this)), LOADING_TIMEOUT);
    }
}