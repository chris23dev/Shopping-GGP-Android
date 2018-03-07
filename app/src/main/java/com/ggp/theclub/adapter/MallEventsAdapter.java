package com.ggp.theclub.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.MallEventActivity;
import com.ggp.theclub.adapter.MallEventsAdapter.MallEventViewHolder;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MallEventsAdapter extends RecyclerView.Adapter<MallEventViewHolder> {
    private Context context;
    private List<MallEvent> mallEvents = new ArrayList<>();
    private MallManager mallManager = MallApplication.getApp().getMallManager();
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    public MallEventsAdapter(Context context) {
        this.context = context;
    }

    public void setMallEvents(List<MallEvent> mallEvents) {
        this.mallEvents.clear();
        this.mallEvents.addAll(mallEvents);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mallEvents.size();
    }

    @Override
    public MallEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_feed_item, parent, false);
        return new MallEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MallEventViewHolder holder, int position) {
        holder.onBind(mallEvents.get(position));
    }

    public class MallEventViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.promotion_location) TextView promotionLocationView;
        @Bind(R.id.logo_layout) View logoLayout;
        @Bind(R.id.image_logo) ImageView imageLogoView;
        @Bind(R.id.text_logo) TextView textLogoView;
        @Bind(R.id.promotion_name) TextView nameView;
        @Bind(R.id.promotion_date) TextView dateView;
        @Bind(R.id.menu_icon) TextView menuIconView;
        @BindString(R.string.share_subject_format) String shareSubjectFormat;
        @BindString(R.string.event_share_format) String eventShareFormat;

        MallEvent mallEvent;

        public MallEventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(MallEvent mallEvent) {
            this.mallEvent = mallEvent;
            Tenant tenant = mallEvent.getTenant();
            if(tenant != null && !StringUtils.isEmpty(tenant.getName())) {
                promotionLocationView.setText(tenant.getName());
            } else if (!StringUtils.isEmpty(mallEvent.getLocation())) {
                promotionLocationView.setText(mallEvent.getLocation());
            }

            if (!StringUtils.isEmpty(mallEvent.getImageUrl())) {
                ImageUtils.loadImage(mallEvent.getImageUrl(), imageLogoView);
            } else {
                logoLayout.setVisibility(View.GONE);
            }

            nameView.setText(mallEvent.getName());
            dateView.setText(DateUtils.getPromotionDateTimeRange(mallEvent.getStartDateTime(), mallEvent.getEndDateTime()));
        }

        private void trackAnalytics(MallEvent mallEvent) {
            HashMap<String, Object> contextData = new HashMap<>();
            contextData.put(AnalyticsManager.ContextDataKeys.TileName, mallEvent.getName().toLowerCase());
            contextData.put(AnalyticsManager.ContextDataKeys.TileType, AnalyticsManager.ContextDataValues.HomeViewTypeMallEvent);
            contextData.put(AnalyticsManager.ContextDataKeys.TilePosition, getLayoutPosition());
            analyticsManager.trackAction(AnalyticsManager.Actions.TileTap, contextData);
        }

        private void onShareClick() {
            IntentUtils.share((Activity) context, mallEvent, mallManager, analyticsManager);
        }

        private void showPopupMenu() {
            PopupMenu popup = new PopupMenu(context, menuIconView);
            popup.getMenuInflater().inflate(R.menu.menu_event_item, popup.getMenu());
            popup.getMenu().findItem(R.id.share).setTitle(context.getString(R.string.popup_share));
            popup.setOnMenuItemClickListener((MenuItem item) -> {
                        switch(item.getItemId()) {
                            case R.id.share:
                                onShareClick();
                                break;
                        }
                        return true;
                    }
            );
            popup.show();
        }

        @OnClick(R.id.item_view)
        public void onMallEventClick() {
            MallEvent mallEvent = mallEvents.get(getAdapterPosition());
            trackAnalytics(mallEvent);
            context.startActivity(MallEventActivity.buildIntent(context, mallEvent));
        }

        @OnClick(R.id.menu_button)
        public void onMenuClick() {
            showPopupMenu();
        }
    }
}