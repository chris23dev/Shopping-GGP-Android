package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.CategoryUtils;
import com.ggp.theclub.util.ImageUtils;
import com.github.clans.fab.FloatingActionMenu;

import butterknife.Bind;
import butterknife.OnClick;

public class PromotionMapActivity extends MapActivity {
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.tenant_sheet) View bottomSheet;
    @Bind(R.id.tenant_name) TextView tenantName;
    @Bind(R.id.tenant_category) TextView categoryView;
    @Bind(R.id.text_logo) TextView textLogo;
    @Bind(R.id.image_logo) ImageView imageLogo;
    @Bind(R.id.filter_fab) FloatingActionMenu filterFab;
    private Tenant selectedTenant;
    private BottomSheetBehavior bottomSheetBehavior;

    public static Intent buildIntent(Context context, Tenant tenant) {
        return buildIntent(context, PromotionMapActivity.class, tenant);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedTenant = getIntentExtra(Tenant.class);
        setContentView(R.layout.promotion_map_activity);
    }

    @Override
    protected void configureView() {
        setTitle(R.string.promotion_wayfinding_title);
        enableBackButton();
        filterFab.setVisibility(View.GONE);
        setupMap();
    }

    @Override
    protected boolean displayLevelSelector() {
        return false;
    }

    private void setupMap() {
        layoutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setupBottomSheet();
                populateBottomSheetTenant();
                mapManager.frameDestination(selectedTenant.getLeaseId());
                mapManager.setSelection(selectedTenant.getLeaseId());
            }
        });
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

        showFab();
    }

    private void populateBottomSheetTenant() {
        bottomSheet.setVisibility(View.VISIBLE);
        tenantName.setText(selectedTenant.getName());
        categoryView.setText(CategoryUtils.getDisplayNameForCategories(selectedTenant.getCategories()));
        ImageUtils.setLogo(imageLogo, textLogo, selectedTenant.getLogoUrl(), selectedTenant.getName());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        showFab();
    }

    private void showFab() {
        if(fab.getVisibility() != View.VISIBLE && mallManager.getMall().getMallConfig().isWayfindingEnabled()) {
            AnimationUtils.enterReveal(fab);
        }
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        startActivity(WayfindActivity.buildIntent(this, selectedTenant));
    }
}