package com.ggp.theclub.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.adapter.LayoutPagerAdapter;
import com.ggp.theclub.api.MallApiClient;
import com.ggp.theclub.event.AccountLoginEvent;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.model.MallConfig;
import com.ggp.theclub.model.Sweepstakes;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.SweepstakesUtils;
import com.merhold.extensiblepageindicator.ExtensiblePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnPageChange;
import java8.util.stream.StreamSupport;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BenefitsActivity extends BaseActivity {
    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private List<Integer> backgroundImages;
    private List<Sweepstakes> sweepstakesList = new ArrayList<>();
    private LayoutPagerAdapter pagerAdapter = new LayoutPagerAdapter(this);
    private final int NUM_STATIC_PAGES = 4;
    private MallConfig mallConfig = mallManager.getMall().getMallConfig();

    @Bind(R.id.mall_logo) ImageView mallLogo;
    @Bind(R.id.benefits_view_pager) ViewPager benefitsViewPager;
    @Bind(R.id.pager_indicator) ExtensiblePageIndicator pagerIndicator;
    @Bind(R.id.background_image) ImageView backgroundImage;
    @Bind(R.id.skip_button) TextView skip;
    @Bind(R.id.create_account_button) TextView createAccount;
    @Bind(R.id.already_a_member) TextView alreadyAMember;
    @Bind(R.id.login_link) TextView logIn;

    public static Intent buildIntent(Context context) {
        return new Intent(context, BenefitsActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.benefits_activity);
        skip.setText(getString(R.string.benefits_skip_button));
        createAccount.setText(getString(R.string.benefits_create_account_button));
        alreadyAMember.setText(getString(R.string.benefits_already_member));
        logIn.setText(getString(R.string.benefits_login));
    }

    @Override
    public void configureView() {
        initializeBackgroundImages();
        ImageUtils.loadImage(mallManager.getMall().getInverseNonSvgLogoUrl(), mallLogo);
        fetchSweepstakes();
    }

    @OnPageChange(R.id.benefits_view_pager)
    public void viewChanged(int page) {
        if(isSweepstakesPage(page)) {
            updateImageSweepstakes(getSweepstakeByPage(page));
        } else {
            updateImage(getBackgroundImage(page));
        }
    }

    @OnClick(R.id.skip_button)
    public void skipClicked() {
        preferencesManager.setOnboardingComplete(true);
        startActivity(MainActivity.buildIntent(this));
    }

    @OnClick(R.id.create_account_button)
    public void createAccountClicked() {
        preferencesManager.setOnboardingComplete(true);
        startActivity(OnboardingRegistrationActivity.buildIntent(this));
    }

    @OnClick(R.id.login_link)
    public void loginClicked() {
        preferencesManager.setOnboardingComplete(true);
        startActivity(OnboardingLoginActivity.buildIntent(this));
    }

    public void onEvent(AccountLoginEvent event) {
        startActivity(WelcomeActivity.buildIntent(this));
        finish();
    }

    private Sweepstakes getSweepstakeByPage(int page) {
        return sweepstakesList.get(page);
    }

    private boolean isSweepstakesPage(int page) {
        return sweepstakesList != null && sweepstakesList.size() > 0 && sweepstakesList.size() - page > 0;
    }

    private List<View> addSwipeableViews() {
        List<View> views = new ArrayList<>();

        addBenefitLayoutAndFillText(R.layout.benefits_shopping,
                R.id.benefits_shopping_title, R.id.benefits_shopping_description,
                R.string.benefits_title_shopping, R.string.benefits_description_shopping);

        addBenefitLayoutAndFillText(R.layout.benefits_parking,
                R.id.benefits_parking_title, R.id.benefits_parking_description,
                R.string.benefits_title_parking, R.string.benefits_description_parking);

        if(mallConfig.isWayfindingEnabled()) {
            addBenefitLayoutAndFillText(R.layout.benefits_wayfinding,
                    R.id.benefits_wayfinding_title, R.id.benefits_wayfinding_description,
                    R.string.benefits_title_wayfinding, R.string.benefits_description_wayfinding);
        }

        return views;
    }

    private void addBenefitLayoutAndFillText(@LayoutRes int benefitLayoutId,
                                             @IdRes int benefits_title_id,
                                             @IdRes int benefits_description_id,
                                             @StringRes int benefits_title_string_id,
                                             @StringRes int benefits_description_string_id) {
        View view = addBenefitLayout(benefitLayoutId);

        TextView title = (TextView) view.findViewById(benefits_title_id);
        TextView description = (TextView) view.findViewById(benefits_description_id);

        title.setText(benefits_title_string_id);
        description.setText(benefits_description_string_id);
    }

    private void initializeBackgroundImages() {
        backgroundImages = new ArrayList<>();
        backgroundImages.add(R.drawable.benefit_background_shopping);
        backgroundImages.add(R.drawable.benefit_background_parking);
        if(mallConfig.isWayfindingEnabled()) {
            backgroundImages.add(R.drawable.benefit_background_wayfinding);
        }
        backgroundImages.add(R.drawable.benefit_background_jfy);
    }

    private void initializePager() {
        pagerAdapter = new LayoutPagerAdapter(this);
        benefitsViewPager.setAdapter(pagerAdapter);
        pagerIndicator.initViewPager(benefitsViewPager);
    }

    private Drawable getBackgroundImage(int page) {
        return getResources().getDrawable(backgroundImages.get(page - sweepstakesList.size()));
    }

    private void updateImage(Drawable image) {
        AnimationUtils.fadeUpdate(backgroundImage, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backgroundImage.setImageDrawable(image);
            }
        });
    }

    private void updateImage(String imageUrl, com.squareup.picasso.Callback callback) {
        AnimationUtils.fadeUpdate(backgroundImage, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ImageUtils.loadImage(imageUrl, backgroundImage, callback);
            }
        });
    }

    private void updateImageSweepstakes(Sweepstakes sweepstakes) {
        if(!StringUtils.isEmpty(sweepstakes.getImageUrl())) {
            updateImage(sweepstakes.getImageUrl(), new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    updateImage(getResources().getDrawable(R.drawable.benefit_background_sweepstakes));
                }
            });
        } else {
            updateImage(getResources().getDrawable(R.drawable.benefit_background_sweepstakes));
        }
    }

    private void fetchSweepstakes() {
        MallApiClient.getInstance().getMallApi().getSweepstakes().enqueue(new Callback<List<Sweepstakes>>() {
            @Override
            public void onResponse(Call<List<Sweepstakes>> call, Response<List<Sweepstakes>> response) {
                configureSwipeableViews(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<List<Sweepstakes>> call, Throwable t) {
                configureSwipeableViews(null);
                Log.w(LOG_TAG, t);
            }
        });
    }

    private void configureSwipeableViews(List<Sweepstakes> sweepstakesResponse) {
        initializePager();
        if (sweepstakesResponse != null) {
            sweepstakesList = new ArrayList<>();
            StreamSupport.stream(sweepstakesResponse).forEach(sweep -> {
                if (SweepstakesUtils.isSweepstakesActive(sweep)) {
                    sweepstakesList.add(sweep);
                    ImageUtils.fetchImage(sweep.getImageUrl());
                    addSweepstakesView(sweep);
                }
            });
        }
        addSwipeableViews();
        viewChanged(0);
    }

    private void addSweepstakesView(Sweepstakes sweepstakes) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.benefits_sweepstakes, null);
        TextView title = (TextView) view.findViewById(R.id.title_view);
        TextView description = (TextView) view.findViewById(R.id.description_view);
        title.setText(sweepstakes.getTitle());
        description.setText(sweepstakes.getDescription());
        addBenefitView(view);
    }

    private View addBenefitLayout(Integer layout) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(layout, null);
        addBenefitView(view);
        return view;
    }

    private void addBenefitView(View view) {
        pagerAdapter.addView(view);
        pagerIndicator.initViewPager(benefitsViewPager);
    }

}