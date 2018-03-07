package com.ggp.theclub.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.adapter.NavigationPagerAdapter;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.api.MallApiClient.VersionCheckCallback;
import com.ggp.theclub.controller.FragmentDataUpdateController;
import com.ggp.theclub.customlocale.LocaleServiceUtils;
import com.ggp.theclub.event.AccountRegistrationEvent;
import com.ggp.theclub.fragment.BaseFragment;
import com.ggp.theclub.manager.FeedbackManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.manager.NetworkManager;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.view.CustomViewPager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnPageChange;
import lombok.Getter;

public class MainActivity extends BaseActivity {
    @BindColor(R.color.active_toolbar_icon) int activeTabColor;
    @BindColor(R.color.inactive_toolbar_icon) int inactiveTabColor;
    @BindColor(R.color.home_color) int homeColor;
    @BindColor(R.color.directory_color) int directoryColor;
    @BindColor(R.color.sales_color) int salesColor;
    @BindColor(R.color.parking_color) int parkingColor;
    @BindColor(R.color.more_color) int moreColor;
    @BindString(R.string.name_greeting_format) String registrationSuccessTitleFormat;
    @Bind(R.id.logo_view) ImageView logoView;
    @Bind(R.id.navigation_toolbar) TabLayout navigationToolbar;
    @Bind(R.id.navigation_view_pager) CustomViewPager navigationViewPager;
    @Bind(R.id.tv_edit)
    View mEdit;
    @Bind(R.id.logo_layout)
    View mLogoLayout;
    protected NavigationPagerAdapter navigationPagerAdapter;
    private FeedbackManager feedbackManager = MallApplication.getApp().getFeedbackManager();
    private List<Integer> activeTabIcons = Arrays.asList(R.string.home_icon, R.string.directory_icon,
            R.string.sales_icon, R.string.parking_circle_icon, R.string.more_icon);
    private List<Integer> inactiveTabIcons = Arrays.asList(R.string.home_unselected_icon, R.string.directory_unselected_icon,
            R.string.sales_unselected_icon, R.string.parking_unselected_icon, R.string.more_unselected_icon);
    private boolean mallContextChange;
    private FragmentDataUpdateController fragmentDataUpdateController;

    public enum Tab{
        HOME(0), DIRECTORY(1), SALES(2), PARKING(3), MORE(4);

        @Getter int tabNumber;
        private static Map<Integer, Tab> reverseLookup = new HashMap();

        static {
            for (Tab tab : Tab.values()) {
                reverseLookup.put(tab.getTabNumber(), tab);
            }
        }

        Tab(int tabNumber) {
            this.tabNumber = tabNumber;
        }

        public static Tab getTabFromNumber(int tabNumber) {
            return reverseLookup.get(tabNumber);
        }
    };


    public static Intent buildIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
        accountManager.fetchAccountInfo(() -> Log.d(LOG_TAG, "Account info fetched"));

        BaseFragment currentFragment = navigationViewPager.getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.onFragmentVisible();
        }

        if (fragmentDataUpdateController == null) {
            fragmentDataUpdateController = new FragmentDataUpdateController(mallRepository, mallManager);
        } else {
            fragmentDataUpdateController.determineIfDataUpdated();
        }
    }

    @Override
    protected void onPause() {
        BaseFragment currentFragment = navigationViewPager.getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.onFragmentInvisible();
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (!mallContextChange) {
            MapManager.getInstance().destroy();
        }
        super.onDestroy();
    }

    public void switchToTab(Tab tab) {
        navigationToolbar.getTabAt(tab.getTabNumber()).select();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.MALL_SEARCH_REQUEST_CODE:
                    startActivity(LoadingActivity.buildIntent(this));
                    LocaleServiceUtils.checkMallLocale(this);
                    mallContextChange = true;
                    finish();
                    break;
                case RequestCode.MALL_CHANGE_REQUEST_CODE:
                    startActivityForResult(ChangeMallActivity.buildIntent(this), RequestCode.MALL_SEARCH_REQUEST_CODE);
                    break;
            }
        }
    }

    @Override
    protected void configureView() {
        ImageUtils.loadImage(mallManager.getMall().getInverseNonSvgLogoUrl(), logoView);
        setupNavigationToolbar();
        setupNavigationToolbarIcons();
    }

    private void checkVersion() {
        if (NetworkManager.getInstance().isNetworkAvailable()) {
            MallApiClient.getInstance().checkVersion(new VersionCheckCallback() {
                @Override
                public void onAcceptableVersion() {}

                @Override
                public void onUnacceptableVersion() {
                    AlertUtils.showAppUpdateDialog(MainActivity.this);
                }
            });
        }
    }

    private void setupNavigationToolbar() {
        navigationPagerAdapter = new NavigationPagerAdapter(getFragmentManager());
        navigationViewPager.setSwipeEnabled(false);
        navigationViewPager.setAdapter(navigationPagerAdapter);
        navigationToolbar.setupWithViewPager(navigationViewPager);
    }

    private void setupNavigationToolbarIcons() {
        for (int i = 0; i < navigationToolbar.getTabCount(); i++) {
            TabLayout.Tab tab = navigationToolbar.getTabAt(i);
            tab.setCustomView(R.layout.navigation_toolbar_tab);
            TextView textView = (TextView) tab.getCustomView();
            textView.setTextColor(getTabIconColor(i));
        }
        setupNavigationIconText();
    }

    private void setupNavigationIconText() {
        for (int i = 0; i < navigationToolbar.getTabCount(); i++) {
            TabLayout.Tab tab = navigationToolbar.getTabAt(i);
            TextView textView = (TextView) tab.getCustomView();
            boolean isSelected = navigationToolbar.getSelectedTabPosition() == i;
            if(isSelected) {
                textView.setText(activeTabIcons.get(i));
            } else {
                textView.setText(inactiveTabIcons.get(i));
            }
        }
    }

    private int getTabIconColor(int index) {
        switch (index) {
            case 0: return homeColor;
            case 1: return directoryColor;
            case 2: return salesColor;
            case 3: return parkingColor;
            case 4: return moreColor;
            default: return activeTabColor;
        }
    }

    private void updateToolbar(int position) {
        logoView.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        titleView.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        setTitle(navigationPagerAdapter.getPageTitle(position).toString());
        int actionButtonTextResourceId = navigationPagerAdapter.getActionButtonText(position);
        if (actionButtonTextResourceId > 0) {
            setTextActionButton(actionButtonTextResourceId);
        }

        int editVisibility = (position == 0)? View.VISIBLE:View.GONE;
        mEdit.setVisibility(editVisibility);

        textActionButton.setVisibility(actionButtonTextResourceId > 0 ? View.VISIBLE : View.GONE);
    }

    public void onEvent(AccountRegistrationEvent event) {
        if (event.isSweetpstakesEntry()) {
            AlertUtils.showSweepstakesSuccessDialog(this, () -> {});
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(String.format(registrationSuccessTitleFormat, accountManager.getCurrentUser().getFirstName()))
                    .setMessage(R.string.registration_complete_message)
                    .setNegativeButton(R.string.done_alert, (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        }
        feedbackManager.incrementFeedbackEventCount(this);
    }

    @Override
    public void onActionButtonClick() {
        navigationPagerAdapter.onActionButtonClick(navigationViewPager.getCurrentItem());
    }

    @OnPageChange(R.id.navigation_view_pager)
    void onPageChange(int position) {
        setupNavigationIconText();
        updateToolbar(position);
        navigationViewPager.pageChanged();
    }

    @OnClick(R.id.logo_view)
    public void onMallLogoClick() {
        startActivityForResult(ChangeMallActivity.buildIntent(this), RequestCode.MALL_SEARCH_REQUEST_CODE);
    }

    @OnClick(R.id.tv_edit)
    public void onEditClick() {
        startActivityForResult(ChangeMallActivity.buildIntent(this), RequestCode.MALL_SEARCH_REQUEST_CODE);
    }
}
