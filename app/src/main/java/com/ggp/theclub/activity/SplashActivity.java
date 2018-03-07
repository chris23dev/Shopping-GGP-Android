package com.ggp.theclub.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.api.MallApiClient.VersionCheckCallback;
import com.ggp.theclub.customlocale.LocaleServiceUtils;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.manager.NetworkManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.AlertUtils;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import lombok.Setter;

public class SplashActivity extends CustomResourcesActivity {
    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private MallManager mallManager = MallApplication.getApp().getMallManager();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Bind(R.id.loading_indicator) ProgressBar loadingIndicator;
    @Bind(R.id.welcome_title) TextView welcomeTitle;
    @Bind(R.id.splash_message_1) TextView message1;
    @Bind(R.id.splash_message_2) TextView message2;
    @BindColor(R.color.white) int white;

    @Setter private boolean timerFinished = false;
    @Setter private boolean loadFinished = false;
    private boolean showOnboarding = false;

    private final int MINIMUM_LOAD_TIME = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.bind(this);
        loadingIndicator.getIndeterminateDrawable().setColorFilter(white, PorterDuff.Mode.MULTIPLY);
        welcomeTitle.setText(R.string.splash_header_text);
        message1.setText(R.string.splash_message_1);
        message2.setText(R.string.splash_message_2);
        new Handler().postDelayed(() -> {
            setTimerFinished(true);
            proceedToApp();
        }, MINIMUM_LOAD_TIME);
        checkAppVersion();

        LocaleServiceUtils.checkLocale(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.MALL_SEARCH_SELECT_REQUEST_CODE) {
            setLoadFinished(true);
            if (resultCode == RESULT_OK) {
                showOnboarding = true;
                proceedToApp();
            } else {
                finish();
            }
        }
    }

    private void checkAppVersion() {
        if (NetworkManager.getInstance().isNetworkAvailable()) {
            MallApiClient.getInstance().checkVersion(new VersionCheckCallback() {
                @Override
                public void onAcceptableVersion() {
                    setLoadFinished(true);
                    proceedToApp();
                }

                @Override
                public void onUnacceptableVersion() {
                    setLoadFinished(true);
                    AlertUtils.showAppUpdateDialog(SplashActivity.this);
                }
            });
        } else {
            setLoadFinished(true);
            proceedToApp();
        }
    }

    private void proceedToApp() {
        if(loadFinished && timerFinished) {
            if (mallManager.hasValidActiveMall()) {
                mallRepository.prefetchData();
                preferencesManager.setOnboardingComplete(!showOnboarding);
                startActivity(LoadingActivity.buildIntent(this));
                setLoadFinished(true);
                finish();
            } else {
                mallManager.clearRecentMalls();
                startActivityForResult(MallSearchSelectActivity.buildIntent(this), RequestCode.MALL_SEARCH_SELECT_REQUEST_CODE);
            }
        }
    }
}
