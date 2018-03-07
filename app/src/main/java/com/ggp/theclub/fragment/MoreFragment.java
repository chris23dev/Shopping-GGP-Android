package com.ggp.theclub.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.AccountLandingActivity;
import com.ggp.theclub.activity.AccountLoginActivity;
import com.ggp.theclub.activity.AccountRegistrationActivity;
import com.ggp.theclub.activity.ChangeMallActivity;
import com.ggp.theclub.activity.ChooseLanguageActivity;
import com.ggp.theclub.activity.FeedbackActivity;
import com.ggp.theclub.activity.MallInfoActivity;
import com.ggp.theclub.activity.RequestCode;
import com.ggp.theclub.activity.TermsActivity;
import com.ggp.theclub.activity.WebViewActivity;
import com.ggp.theclub.customlocale.LocaleService;
import com.ggp.theclub.event.HolidayHoursChangedEvent;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.HoursUtils;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.StringUtils;

import org.joda.time.LocalDate;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

//TODO correct animations for links
public class MoreFragment extends BaseFragment {
    private final String LOG_TAG = MoreFragment.class.getSimpleName();

    @Bind(R.id.mall_name_view) TextView mallNameView;
    @Bind(R.id.todays_hours_view) TextView todaysHoursView;

    @Bind(R.id.mall_info_button_label) TextView mallInfoButtonLabel;
    @Bind(R.id.black_friday_hours_button) View blackFridayHoursButton;
    @Bind(R.id.unauthenticated_section) View unauthenticatedSection;
    @Bind(R.id.authenticated_section) View authenticatedSection;

    @Bind(R.id.twitter_button) TextView twitterButton;
    @Bind(R.id.instagram_button) TextView instagramButton;
    @Bind(R.id.facebook_button) TextView facebookButton;
    @Bind(R.id.current_mall) TextView currentMall;
    @Bind(R.id.more_register_button) TextView moreRegistrationBtn;
    @Bind(R.id.black_friday_hours) TextView blackFridayHours;
    @Bind(R.id.more_login_button) TextView moreLoginBtn;
    @Bind(R.id.more_language_button) TextView moreLanguageBtn;
    @Bind(R.id.more_account_button) TextView moreAccountBtn;
    @Bind(R.id.feedback_button) TextView feedbackBtn;
    @Bind(R.id.privacy_terms_button) TextView privacyTermsBtn;
    @Bind(R.id.change_mall_button) Button changeMallBtn;

    @BindString(R.string.more_hours_open_format) String todaysHoursOpenFormat;
    @BindString(R.string.more_hours_closed_format) String todaysHoursClosedFormat;
    
    AccountManager accountManager = MallApplication.getApp().getAccountManager();

    public static MoreFragment newInstance() {
        Bundle args = new Bundle();
        
        MoreFragment fragment = new MoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onFragmentVisible() {
        analyticsManager.trackScreen(AnalyticsManager.Screens.More);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void configureView() {
        mallNameView.setText(mallManager.getMall().getName());
        setupSocialMedia();
        setupMallInfoButtons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.more_fragment, container);
        currentMall.setText(getString(R.string.current_mall));
        moreRegistrationBtn.setText(getString(R.string.more_register_button));
        blackFridayHours.setText(getString(R.string.black_friday_hours));
        moreLoginBtn.setText(getString(R.string.more_login_button));
        moreAccountBtn.setText(getString(R.string.more_account_button));
        moreLanguageBtn.setText(R.string.language_settings_title);
        feedbackBtn.setText(getString(R.string.provide_feedback));
        privacyTermsBtn.setText(getString(R.string.privacy_terms_of_use));
        changeMallBtn.setText(getString(R.string.change_mall_button));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        invalidateLanguageButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupAccountButtons();
        HoursUtils.setMallHoursString(mallManager.getMall(), todaysHoursView, todaysHoursOpenFormat, todaysHoursClosedFormat);
    }

    private void setupSocialMedia() {
        Mall.SocialMedia socialMedia = mallManager.getMall().getSocialMedia();
        if(socialMedia != null) {
            facebookButton.setTag(socialMedia.getFacebookUrl());
            facebookButton.setVisibility(StringUtils.isEmpty(socialMedia.getFacebookUrl()) ? View.GONE : View.VISIBLE);
            twitterButton.setTag(socialMedia.getTwitterUrl());
            twitterButton.setVisibility(StringUtils.isEmpty(socialMedia.getTwitterUrl()) ? View.GONE : View.VISIBLE);
            instagramButton.setTag(socialMedia.getInstagramUrl());
            instagramButton.setVisibility(StringUtils.isEmpty(socialMedia.getInstagramUrl()) ? View.GONE : View.VISIBLE);
        }
    }

    private void setupMallInfoButtons() {
        Mall mall = mallManager.getMall();
        LocalDate now = LocalDate.now();
        mallInfoButtonLabel.setText(DateUtils.isHolidayHoursActive(mall, now) ? R.string.mall_info_and_holiday_hours : R.string.mall_info);
        blackFridayHoursButton.setVisibility(DateUtils.isBlackFridayHoursActive(mall, now) ? View.VISIBLE : View.GONE);
    }

    private void setupAccountButtons() {
        unauthenticatedSection.setVisibility(accountManager.isLoggedIn() ? View.GONE : View.VISIBLE);
        authenticatedSection.setVisibility(accountManager.isLoggedIn() ? View.VISIBLE : View.GONE);
    }

    private void sendSocialAnalytics(int id) {
        String action;
        HashMap<String, Object> contextData = new HashMap<>();

        switch (id) {
            case R.id.facebook_button:
                action = AnalyticsManager.Actions.SocialBadge;
                contextData.put(AnalyticsManager.ContextDataKeys.SocialNetwork, AnalyticsManager.ContextDataValues.SocialNetworkFacebook);
                break;
            case R.id.instagram_button:
                action = AnalyticsManager.Actions.SocialBadge;
                contextData.put(AnalyticsManager.ContextDataKeys.SocialNetwork, AnalyticsManager.ContextDataValues.SocialNetworkInstagram);
                break;
            case R.id.twitter_button:
                action = AnalyticsManager.Actions.SocialBadge;
                contextData.put(AnalyticsManager.ContextDataKeys.SocialNetwork, AnalyticsManager.ContextDataValues.SocialNetworkTwitter);
                break;
            default:
                action = null;
        }

        if(!StringUtils.isEmpty(action)) {
            analyticsManager.trackAction(action, contextData);
        }
    }

    @OnClick({
            R.id.change_mall_button, R.id.mall_info_button, R.id.black_friday_hours_button,
            R.id.login_button, R.id.register_button, R.id.account_landing_button,
            R.id.feedback_button, R.id.privacy_terms_button, R.id.language_button})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.change_mall_button:
                getActivity().startActivityForResult(ChangeMallActivity.buildIntent(getActivity()), RequestCode.MALL_SEARCH_REQUEST_CODE);
                break;
            case R.id.mall_info_button:
                getActivity().startActivityForResult(MallInfoActivity.buildIntent(getActivity()), RequestCode.MALL_CHANGE_REQUEST_CODE);
                break;
            case R.id.black_friday_hours_button:
                String blackFridayUrl = mallManager.getMall().getBlackFridayHoursUrl();
                if (!StringUtils.isEmpty(blackFridayUrl)) {
                    getActivity().startActivity(WebViewActivity.buildIntent(getActivity(), mallManager.getMall().getBlackFridayHoursUrl(), AnalyticsManager.Screens.BlackFridayHours, null));
                }
                break;
            case R.id.login_button:
                startActivity(AccountLoginActivity.buildIntent(getActivity()));
                break;
            case R.id.register_button:
                startActivity(AccountRegistrationActivity.buildIntent(getActivity()));
                break;
            case R.id.language_button:
                startActivity(ChooseLanguageActivity.buildIntent(getActivity()));
                break;
            case R.id.account_landing_button:
                startActivity(AccountLandingActivity.buildIntent(getActivity()));
                break;
            case R.id.privacy_terms_button:
                startActivity(TermsActivity.buildIntent(getActivity()));
                analyticsManager.trackAction(AnalyticsManager.Actions.NavTermsAndConditions);
                break;
            case R.id.feedback_button:
                startActivity(FeedbackActivity.buildIntent(getActivity()));
                break;
        }
    }


    @OnClick({R.id.facebook_button, R.id.twitter_button, R.id.instagram_button})
    public void onSocialMediaButtonClick(View view) {
        IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(view.getTag().toString())), getActivity());
        sendSocialAnalytics(view.getId());
    }

    public void onEvent(HolidayHoursChangedEvent event) {
        setupMallInfoButtons();
    }

    private void invalidateLanguageButton() {
        if(PreferencesManager.getInstance().getBoolean(LocaleService.HAS_LANGUAGE)){
            getView().findViewById(R.id.language_button).setVisibility(View.VISIBLE);
        }
    }
}