package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.EventLinkAdapter;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.MallIds;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.ViewUtils;

import butterknife.BindString;

public class MallEventActivity extends PromotionActivity {
    @BindString(R.string.share_subject_format) String shareSubjectFormat;
    @BindString(R.string.event_share_format) String eventShareFormat;
    private MallEvent mallEvent;

    public static Intent buildIntent(Context context, MallEvent mallEvent) {
        return buildIntent(context, MallEventActivity.class, mallEvent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mallEvent = getIntentExtra(MallEvent.class);
        setPromotion(mallEvent);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        analyticsManager.trackScreen(AnalyticsManager.Screens.EventDetails);
    }

    @Override
    protected void configureView() {
        super.configureView();
        boolean entertainmentEvent = mallManager.getMall().getId() == MallIds.GRAND_CANAL && tenant == null;
        setTitle(entertainmentEvent ? R.string.entertainment_title : R.string.event_title);
        descriptionHeader.setText(R.string.event_title);
        descriptionHeader.setVisibility(View.VISIBLE);
        ViewUtils.setHtmlText(teaser, mallEvent.getTeaserDescription());
        if (mallEvent.getExternalLinks().size() > 0) {
            externalLinksList.setLayoutManager(new LinearLayoutManager(this));
            externalLinksList.setNestedScrollingEnabled(false);
            externalLinksList.setAdapter(new EventLinkAdapter(this, mallEvent));
            externalLinksList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setupLocation() {
        if(tenant != null && !StringUtils.isEmpty(tenant.getName())) {
            locationView.setText(tenant.getName());
            locationView.setTextColor(blue);
        } else if (!StringUtils.isEmpty(mallEvent.getLocation())) {
            locationView.setText(mallEvent.getLocation());
        } else {
            locationLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActionButtonClick() {
        IntentUtils.share(this, mallEvent, mallManager, analyticsManager);
    }
}