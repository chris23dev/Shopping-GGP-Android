package com.ggp.theclub.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.AccountRegistrationActivity;
import com.ggp.theclub.adapter.MallEventsAdapter;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.util.PromotionUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

public class EventsFragment extends BaseFragment {

    @BindString(R.string.no_events) String noEventsMallName;
    @BindString(R.string.please_check_back) String noEventsAuth;
    @BindString(R.string.join_ggp_event) String noEventsNoAuth;
    @Bind(R.id.events_view) CustomRecyclerView eventsFeed;
    @Bind(R.id.events_empty) View emptyView;
    @Bind(R.id.empty_event_text1) TextView emptyText;
    @Bind(R.id.empty_event_text2) TextView emptyText2;
//    @Bind(R.id.register_button) Button createAccountBtn;
    MallEventsAdapter eventsAdapter;

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onFragmentVisible() {
        analyticsManager.trackScreen(AnalyticsManager.Screens.Events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.events_fragment, container);
//        createAccountBtn.setText(getString(R.string.create_account));
        emptyText2.setText(getString(R.string.please_check_back));
        return view;
    }

    @Override
    protected void configureView() {
        super.configureView();

        MallApplication app = MallApplication.getApp();
        boolean isAuth = app.getAccountManager().isLoggedIn();

        eventsAdapter = new MallEventsAdapter(getActivity());
        eventsFeed.setAdapter(eventsAdapter);

        emptyText.setText(String.format(noEventsMallName, app.getMallManager().getMall().getName()));
        emptyText2.setText(isAuth ? noEventsAuth : noEventsNoAuth);
//        createAccountBtn.setVisibility(isAuth ? View.GONE : View.VISIBLE);

        fetchMallEvents();
    }

    private void fetchMallEvents() {
        mallRepository.queryForMallEvents(mallEvents -> {
            if (!mallEvents.isEmpty()) {
                List<MallEvent> sortedMallEvents = PromotionUtils.getSortedMallEvents(mallEvents);
                eventsAdapter.setMallEvents(sortedMallEvents);
                eventsFeed.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                eventsFeed.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            eventsFeed.setDataLoaded(true);
        });
    }

    @OnClick(R.id.register_button)
    public void createAccount() {
        startActivity(AccountRegistrationActivity.buildIntent(getActivity()));
    }
}
