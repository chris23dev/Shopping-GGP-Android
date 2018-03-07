package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.event.ConnectivityChangeEvent;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.manager.PermissionsManager;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.widget.CustomLocalisationResources;
import com.google.gson.Gson;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import java8.util.Objects;
import java8.util.stream.StreamSupport;
import lombok.Getter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends CustomResourcesActivity {
    @Bind(R.id.layout_view) CoordinatorLayout layoutView;
    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
    @Nullable @Bind(R.id.back_button) TextView backButton;
    @Nullable @Bind(R.id.title_view) TextView titleView;
    @Getter @Nullable @Bind(R.id.text_action_button) TextView textActionButton;
    @Nullable @Bind(R.id.icon_action_button) TextView iconActionButton;
    private Snackbar snackbar;

    public final String LOG_TAG = getClass().getSimpleName();
    protected static final Gson gson = new Gson();
    protected MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    protected MallManager mallManager = MallApplication.getApp().getMallManager();
    protected AccountManager accountManager = MallApplication.getApp().getAccountManager();
    protected AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();
    private Resources mResources;

    public static Intent buildIntent(Context context, Class activity) {
        return new Intent(context, activity);
    }

    public static Intent buildIntent(Context context, Class activity, Object... data) {
        Intent intent = buildIntent(context, activity);
        StreamSupport.stream(Arrays.asList(data)).filter(Objects::nonNull).forEach(item -> intent.putExtra(item.getClass().getSimpleName(), gson.toJson(item)));
        return intent;
    }

    protected <T> T getIntentExtra(Class<T> type) {
        return getIntentExtra(type.getSimpleName(), type);
    }

    protected <T> T getIntentExtra(String name, Class<T> type) {
        return gson.fromJson(getIntent().getStringExtra(name), type);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        configureView();
    }

    protected abstract void configureView();

    @Override
    public void setTitle(int titleId) {
        titleView.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    protected void enableBackButton() {
        backButton.setVisibility(View.VISIBLE);
    }

    protected void setTextActionButton(int textId) {
        setTextActionButton(getString(textId));
    }

    protected void setTextActionButton(String text) {
        textActionButton.setText(text);
        textActionButton.setVisibility(View.VISIBLE);
    }

    protected void setIconActionButton(int iconId) {
        iconActionButton.setText(iconId);
        iconActionButton.setVisibility(View.VISIBLE);
    }

    public void showKeyboard(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }

    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void checkConnection() {
        if (!MallApplication.getApp().isNetworkAvailable()) {
            showErrorSnackbarTransparent(R.string.connection_error);
        } else if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsManager.startTrackingLifecycleData(this);
        checkConnection();
    }

    @Override
    protected void onPause() {
        analyticsManager.stopTrackingLifecycleData();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RequestCode.FINISH_REQUEST_CODE) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (accountManager.shouldHandlePermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        PermissionsManager.getInstance().onRequestPermissionsResult(requestCode, grantResults);
    }

    public void onEvent(ConnectivityChangeEvent event) {
        checkConnection();
    }

    @Nullable @OnClick(R.id.back_button)
    public void onBackButtonClick() {
        onBackPressed();
    }

    @Nullable @OnClick(R.id.action_button)
    public void onActionButtonClick() {}

    protected void showErrorSnackbar() {
        showErrorSnackbar(R.string.generic_error);
    }

    protected void showErrorSnackbar(int errorMessage) {
        try {
            String error = getString(errorMessage);
            Snackbar.make(layoutView, error, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    protected void showErrorSnackbarTransparent(int connection_error) {
        try {
            snackbar = Snackbar.make(layoutView, getString(connection_error), Snackbar.LENGTH_INDEFINITE);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.translucent_snackbar));
            snackbar.show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public Resources getResources() {
        if (CustomLocalisationResources.shouldBeUsed()) {
            if (mResources == null) {
                mResources = new CustomLocalisationResources(super.getResources());
            }
        }
        return mResources == null ? super.getResources() : mResources;
    }
}