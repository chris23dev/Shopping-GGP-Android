package com.ggp.theclub.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.AccountRegistrationActivity;
import com.ggp.theclub.activity.BaseActivity;
import com.ggp.theclub.activity.FiltersActivity;
import com.ggp.theclub.adapter.ViewPagerAdapter;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.event.TenantsFilterUpdateEvent;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.FeedbackManager;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.CategoryUtils;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.FilterView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnPageChange;
import de.greenrobot.event.EventBus;

public class DirectoryFragment extends ViewPagerFragment {
    @BindString(R.string.list_title) String listTitle;
    @BindString(R.string.map_title) String mapTitle;
    @BindString(R.string.no_favorites_header_unauthenticated) String unauthenticatedFavoritesHeader;
    @BindString(R.string.no_favorites_text_unauthenticated) String unauthenticatedFavoritesBody;
    @BindString(R.string.no_favorites_header_authenticated) String noFavoritesHeader;
    @BindString(R.string.no_favorites_text_authenticated) String noFavoritesBody;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.favorites_message) LinearLayout favoritesMessageLayout;
    @Bind(R.id.favorites_message_header) TextView favoritesMessageHeaderView;
    @Bind(R.id.favorites_message_text) TextView favoritesMessageTextView;
    @Bind(R.id.register_button) Button registerButton;
    @Bind(R.id.filter_view) FilterView filterView;
    private final int LIST_POSITION = 0;
    private static final String FILTER_SHOWCASE_KEY = "FILTER_SHOWCASE";
    private TenantsFilterUpdateEvent favoritesFilterUpdateEvent;
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();
    private FeedbackManager feedbackManager = MallApplication.getApp().getFeedbackManager();

    public static DirectoryFragment newInstance() {
        return new DirectoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        if (mallManager.getMall().getMallConfig().isProductEnabled()) {
            AlertUtils.showShowcaseAlert(getActivity(), ((BaseActivity) getActivity()).getTextActionButton(), R.string.filter_showcase_title,
                    R.string.filter_showcase_text, R.string.filter_showcase_dismiss, FILTER_SHOWCASE_KEY);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.directory_fragment, container);
        registerButton.setText(getString(R.string.register_button_text));
        return view;
    }

    @Override
    protected void configureView() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new DirectoryListFragment(), listTitle);
        viewPagerAdapter.addFragment(new DirectoryMapFragment(), mapTitle);
        viewPager.setSwipeEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onActionButtonClick() {
        startActivity(FiltersActivity.buildIntent(getActivity()));
    }

    private void trackPageChangeAction(int position) {
        String viewType = position == LIST_POSITION ?
                AnalyticsManager.ContextDataValues.DirectoryViewTypeList :
                AnalyticsManager.ContextDataValues.DirectoryViewTypeMap;

        HashMap<String, Object> contextData = new HashMap<String, Object>() {{
            put(AnalyticsManager.ContextDataKeys.DirectoryViewType, viewType);
        }};
        analyticsManager.trackAction(AnalyticsManager.Actions.DirectoryChangeView, contextData);
    }

    private void hideFavoritesMessage() {
        favoritesMessageLayout.setVisibility(View.GONE);
    }

    private void showUnauthenticatedUserMessage() {
        showFavoritesMessage(unauthenticatedFavoritesHeader, unauthenticatedFavoritesBody, false);
    }

    private void showNoFavoritesMessage() {
        showFavoritesMessage(noFavoritesHeader, noFavoritesBody, false);
    }

    private void showFavoritesMessage(String header, String text, boolean showRegister) {
        favoritesMessageHeaderView.setText(header);
        favoritesMessageTextView.setText(text);
//        registerButton.setVisibility(showRegister ? View.VISIBLE : View.GONE);
        favoritesMessageLayout.setVisibility(View.VISIBLE);
    }

    public void onEvent(TenantsFilterUpdateEvent event) {
        if (CategoryUtils.CATEGORY_MY_FAVORITES.equals(event.getCategoryCode()) && !accountManager.isLoggedIn()) {
            favoritesFilterUpdateEvent = event;
            showUnauthenticatedUserMessage();
        } else if (CategoryUtils.CATEGORY_MY_FAVORITES.equals(event.getCategoryCode())) {
            mallRepository.queryForTenants(tenants -> {
                if (TenantUtils.getFavoriteTenants(tenants).isEmpty()) {
                    showNoFavoritesMessage();
                } else {
                    hideFavoritesMessage();
                }
            });
        } else {
            hideFavoritesMessage();
        }
        filterView.updateView(event);
    }

    public void onEvent(AccountLoginEvent event) {
        if (favoritesFilterUpdateEvent != null) {
            EventBus.getDefault().post(favoritesFilterUpdateEvent);
            favoritesFilterUpdateEvent = null;
        }
    }

    @OnClick(R.id.register_button)
    public void onRegisterClick() {
        analyticsManager.trackAction(AnalyticsManager.Actions.DirectoryRegister);
        startActivity(AccountRegistrationActivity.buildIntent(getActivity()));
    }

    @OnPageChange(R.id.view_pager)
    public void onPageChange(int position) {
        super.onPageChange(position);

        if (position != LIST_POSITION) {
            feedbackManager.incrementFeedbackEventCount(getActivity());
        }
        trackPageChangeAction(position);
    }
}