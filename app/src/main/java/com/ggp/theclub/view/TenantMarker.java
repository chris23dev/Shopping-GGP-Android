package com.ggp.theclub.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TenantMarker extends LinearLayout {

    @Bind(R.id.marker_title) TextView titleView;

    public TenantMarker(Context context) {
        super(context);
        init();
    }

    public TenantMarker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TenantMarker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.tenant_marker, this);
        ButterKnife.bind(this);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }
}
