package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.customlocale.LocaleServiceUtils;
import com.ggp.theclub.manager.DeepLinkingManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.MallIds;
import com.ggp.theclub.util.ViewUtils;

import butterknife.Bind;
import butterknife.BindColor;

public class LoadingActivity extends BaseActivity {
    private final int LOADING_TIMEOUT = 2000;
    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private DeepLinkingManager deepLinkingManager = MallApplication.getApp().getDeepLinkingManager();

    @Bind(R.id.loading_indicator) ProgressBar loadingIndicator;
    @Bind(R.id.mall_name) TextView mallNameView;
    @Bind(R.id.loading_text) TextView loading;
    @BindColor(R.color.white) int white;

    public static Intent buildIntent(Context context) {
        return new Intent(context, LoadingActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);
        checkForDeepLink();
        LocaleServiceUtils.checkMallLocale(this);
    }

    @Override
    public void configureView() {
        setBackground();
        ViewUtils.setProgressBarColor(loadingIndicator, white);
        mallNameView.setText(mallManager.getMall().getName());
        loading.setText(getString(R.string.loading_mall));
    }

    private void setBackground() {
        int backgroundResource = R.drawable.diamond_background;
        switch (mallManager.getMall().getId()) {
            case MallIds.GRAND_CANAL: backgroundResource = R.drawable.loading_grand_canal;
                break;
            case MallIds.FASHION_SHOW: backgroundResource = R.drawable.loading_fashion_show;
                break;
            case MallIds.KUHIO: backgroundResource = R.drawable.loading_kuhio;
                break;
        }
        getWindow().setBackgroundDrawableResource(backgroundResource);
    }

    private void checkForDeepLink() {
        if (deepLinkingManager.isDeepLinkLaunch(this)) {
            Integer mallId = deepLinkingManager.getDeepLinkMallId();
            if (mallId != null) {
                mallRepository.queryForMall(mallId, mall -> {
                    mallManager.setMall(mall);
                    startActivity(MainActivity.buildIntent(this));
                });
            }
        } else if(shouldOnboard()) {
            new Handler().postDelayed(() -> startActivity(BenefitsActivity.buildIntent(this)), LOADING_TIMEOUT);
        } else {
            new Handler().postDelayed(() -> startActivity(MainActivity.buildIntent(this)), LOADING_TIMEOUT);
        }
    }

    private boolean shouldOnboard() {
        return !PreferencesManager.getInstance().isOnboardingComplete();
    }
}