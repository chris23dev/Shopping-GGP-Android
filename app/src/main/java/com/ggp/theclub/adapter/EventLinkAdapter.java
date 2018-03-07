package com.ggp.theclub.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.MallEvent;
import com.ggp.theclub.model.MallEvent.EventLink;
import com.ggp.theclub.util.IntentUtils;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter for list of links on an event
 */
public class EventLinkAdapter extends RecyclerView.Adapter {
    private Context context;
    private MallEvent mallEvent;

    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    public EventLinkAdapter(Context context, MallEvent mallEvent) {
        this.context = context;
        this.mallEvent = mallEvent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_link, parent, false);
        return new EventLinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EventLinkViewHolder) holder).onBind(mallEvent.getExternalLinks().get(position), position);
    }

    @Override
    public int getItemCount() {
        return mallEvent.getExternalLinks().size();
    }

    public class EventLinkViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.event_link) Button button;

        @BindColor(R.color.blue) int blue;
        @BindColor(R.color.white) int white;

        EventLink eventLink;

        public EventLinkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * Sets the link text and add a click handler to open link
         */
        public void onBind(EventLink eventLink, int position) {
            styleButton(position == 0);
            button.setText(eventLink.getDisplayText());
            this.eventLink = eventLink;
        }

        private void styleButton(boolean primary) {
            GradientDrawable gradientDrawable = (GradientDrawable) button.getBackground();
            gradientDrawable.setColor(primary ? blue : white);
            button.setTextColor(primary ? white : blue);
        }

        @OnClick(R.id.event_link)
        public void onEventLinkClick() {
            IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(eventLink.getUrl())), (Activity) context);

            HashMap<String, Object> contextData = new HashMap<>();
            contextData.put(AnalyticsManager.ContextDataKeys.EventSaleName, mallEvent.getTitle());
            contextData.put(AnalyticsManager.ContextDataKeys.EventLinkText, eventLink.getDisplayText());
            contextData.put(AnalyticsManager.ContextDataKeys.EventLinkType, getAdapterPosition() == 0 ?
                    AnalyticsManager.ContextDataValues.PrimaryLink : AnalyticsManager.ContextDataValues.SecondaryLink);
            if (mallEvent.getTenant() != null) {
                analyticsManager.trackAction(AnalyticsManager.Actions.EventDetailLinkClick, contextData, mallEvent.getTenant().getName());
            } else {
                analyticsManager.trackAction(AnalyticsManager.Actions.EventDetailLinkClick, contextData);
            }
        }
    }
}
