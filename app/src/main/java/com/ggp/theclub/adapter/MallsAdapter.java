package com.ggp.theclub.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.BaseActivity;
import com.ggp.theclub.adapter.MallsAdapter.MallViewHolder;
import com.ggp.theclub.fragment.MallSearchFragment.SearchScreenStyle;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.AlertUtils;
import com.ggp.theclub.util.IntentUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import lombok.Setter;

public class MallsAdapter extends RecyclerView.Adapter<MallViewHolder> {
    private final String LOG_TAG = getClass().getSimpleName();
    private final String COORDINATES_FORMAT = "%.1f:%.1f";
    private final String DISTANCE_FORMAT = "%.1f";
    private BaseActivity activity;
    @Getter @Setter private boolean completeMalls;
    @Getter @Setter private boolean locationSearch;
    @Getter private List<Mall> malls = new ArrayList<>();
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();
    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private MallManager mallManager = MallApplication.getApp().getMallManager();
    private SearchScreenStyle searchScreenStyle;

    public MallsAdapter(BaseActivity activity, SearchScreenStyle searchScreenStyle) {
        this.activity = activity;
        this.searchScreenStyle = searchScreenStyle;
    }

    public void setMalls(List<Mall> mallsList, boolean completeMalls) {
        malls.clear();
        malls.addAll(mallsList);
        setCompleteMalls(completeMalls);
        notifyDataSetChanged();
    }

    public void clearMalls() {
        malls.clear();
        notifyDataSetChanged();
    }

    private void showLegacyMallDialog(Mall mall) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.mall_select_legacy_title)
                .setMessage(String.format(MallApplication.getApp().getString(R.string.mall_select_legacy_message), mall.getName()))
                .setNegativeButton(R.string.change_mall_button, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.mall_select_legacy_right_button, (dialog, which) ->
                        IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(mall.getWebsiteUrl())), activity))
                .create()
                .show();
    }

    @Override
    public int getItemCount() {
        return malls.size();
    }

    @Override
    public MallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mall_search_item, parent, false);
        return new MallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MallViewHolder holder, int position) {
        holder.onBind(malls.get(position));
    }

    public class MallViewHolder extends RecyclerView.ViewHolder {
        @BindString(R.string.mall_distance_units) String mallDistanceUnitText;
        @Bind(R.id.name_view) TextView nameView;
        @Bind(R.id.location_view) TextView locationView;
        @Bind(R.id.distance_view) TextView distanceView;

        @BindColor(R.color.blue) int blue;
        @BindColor(R.color.gray) int gray;

        public MallViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(searchScreenStyle == SearchScreenStyle.CHANGE_MALL) {
                styleChangeMall();
            }
        }

        public void onBind(Mall mall) {
            nameView.setText(mall.getName());
            if (mall.getAddress() != null) {
                locationView.setText(mall.getAddress().getCityAndState());
            }
            String distance = String.format(DISTANCE_FORMAT, mall.getDistanceFromSearchLocation());
            distanceView.setText(distance + " " + mallDistanceUnitText);
            distanceView.setVisibility(locationSearch ? View.VISIBLE : View.GONE);
        }

        private void styleChangeMall() {
            nameView.setTextColor(blue);
            locationView.setTextColor(gray);
            distanceView.setTextColor(gray);
        }

        @OnClick(R.id.item_view)
        public void onMallClick() {
            activity.hideKeyboard();
            Mall mall = malls.get(getAdapterPosition());

            if(completeMalls) {
                selectMall(mall);
            } else {
                itemView.setClickable(false);
                mallRepository.queryForMall(mall.getId(), fullMall -> selectMall(fullMall));
            }
        }

        private void selectMall(Mall mall) {
            if (mall.getStatus() == Mall.MallStatus.LEGACY_PLATFORM) {
                showLegacyMallDialog(mall);
            } else if (mall.getStatus() == Mall.MallStatus.DISPOSITIONING || mall.getStatus() == Mall.MallStatus.DISPOSITIONED) {
                AlertUtils.showDispositioningMallDialog(activity);
            } else {
                mallManager.setMall(mall);
                trackMallSelection(mall);
                activity.setResult(BaseActivity.RESULT_OK);
                activity.finish();
            }
        }

        private void trackMallSelection(Mall mall) {
            String coordinates = String.format(COORDINATES_FORMAT, mall.getLatitude(), mall.getLongitude());
            HashMap<String, Object> contextData = new HashMap<String, Object>() {{
                put(AnalyticsManager.ContextDataKeys.SelectedMallCoordinates, coordinates);
            }};
            if (mall.getDistanceFromSearchLocation() > 0) {
                String distance = String.format(DISTANCE_FORMAT, mall.getDistanceFromSearchLocation());
                contextData.put(AnalyticsManager.ContextDataKeys.SelectedMallDistance, distance);
            }
            analyticsManager.trackAction(AnalyticsManager.Actions.SelectedMall, contextData);
        }
    }
}