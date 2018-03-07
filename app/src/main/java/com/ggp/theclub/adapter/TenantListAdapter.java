package com.ggp.theclub.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.activity.TenantActivity;
import com.ggp.theclub.activity.WayfindActivity;
import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.AnimationUtils;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.FastScroller.FastScrollerListener;
import com.ggp.theclub.view.ListItemHeaderViewHolder;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class TenantListAdapter extends TenantFilterAdapter implements StickyHeaderAdapter<ListItemHeaderViewHolder>, FastScrollerListener {

    private static final String TAG = TenantListAdapter.class.getSimpleName();
    @Bind(R.id.directory_favorite)
    TextView directoryFavorite;
    @Bind(R.id.directory_wayfinding)
    TextView directoryWayfinding;

    private Context context;
    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private AccountManager accountManager = MallApplication.getApp().getAccountManager();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MapManager mapManager = MapManager.getInstance();
    private boolean displaysSwipePreview;
    private boolean hasShownPreview;

    private static final int MIN_PREVIEW_POSITION = 1;
    private static final int MAX_PREVIEW_POSITION = 3;

    public TenantListAdapter(Context context) {
        this(context, false);
    }

    public TenantListAdapter(Context context, boolean displaysSwipePreview) {
        super(new ArrayList<>());
        this.context = context;
        this.displaysSwipePreview = displaysSwipePreview;
        hasShownPreview = preferencesManager.getBoolean(PreferencesManager.HAS_PREVIEWED_SWIPE_DIRECTORY);
    }

    @Override
    public TenantListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_item, parent, false);
        ButterKnife.bind(this,view);
        directoryFavorite.setText(context.getString(R.string.directory_favorite));
        directoryWayfinding.setText(context.getString(R.string.directory_wayfinding));

        return new TenantListViewHolder(view);
    }

    @Override
    public long getHeaderId(int position) {
        if (!StringUtils.isEmpty(tenants.get(position).getName())) {
            return StringUtils.getNameForSorting(tenants.get(position).getName()).charAt(0);
        }
        return 0;
    }

    @Override
    public ListItemHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
        return new ListItemHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(ListItemHeaderViewHolder holder, int position) {
        holder.onBind(String.valueOf((char) getHeaderId(position)).toUpperCase());
    }

    @Override
    public String getFastScrollerBubbleText(int position) {
        return String.valueOf((char) getHeaderId(position)).toUpperCase();
    }

    public class TenantListViewHolder extends TenantFilterViewHolder {
        @Bind(R.id.item_view) SwipeLayout swipeLayout;
        @Bind(R.id.image_logo) ImageView logoImageView;
        @Bind(R.id.text_logo) TextView logoNameView;
        @Bind(R.id.tenant_name) TextView nameView;
        @Bind(R.id.tenant_location) TextView locationView;
        @Bind(R.id.favorite_button) View favoritesButton;
        @Bind(R.id.favorite_on) View favoriteOnView;
        @Bind(R.id.navigation_button) View navigationButton;
        @BindString(R.string.tenant_child_location_format) String childTenantLocationFormat;

        private boolean favoriteActive = false;

        public TenantListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBind(Tenant tenant) {
            ImageUtils.setLogo(logoImageView, logoNameView, tenant.getLogoUrl(), tenant.getName());
            nameView.setText(tenant.getName());
            setupTenantLocation(tenant);
            swipeLayout.setSwipeEnabled(false); //will set to true if either button is shown
            setupFavorites(tenant);
            setupWayfinding(tenant);

            if (shouldPreviewSwipe(swipeLayout)) {
                new Handler().postDelayed(() -> swipeLayout.open(true), 300);
                hasShownPreview = true;
                preferencesManager.setBoolean(PreferencesManager.HAS_PREVIEWED_SWIPE_DIRECTORY, true);
            }
        }

        private boolean shouldPreviewSwipe(SwipeLayout swipeLayout) {
            return displaysSwipePreview &&
                    !hasShownPreview &&
                    swipeLayout.isSwipeEnabled() &&
                    getAdapterPosition() >= MIN_PREVIEW_POSITION &&
                    getAdapterPosition() <= MAX_PREVIEW_POSITION;
        }

        private void setupTenantLocation(Tenant tenant) {
            if(TenantUtils.hasParentTenant(tenant)) {
                mallRepository.queryForTenants(stores -> {
                    Tenant parentTenant = TenantUtils.getParentTenant(tenant, stores);
                    if(parentTenant == null){
                        Log.d(TAG, "For tenant " + tenant.getName() + " parent is null");
                    } else {
                        String locationText = String.format(childTenantLocationFormat, parentTenant.getName());
                        setLocationString(locationText);
                    }
                });
            } else {
                setLocationString(MapManager.getInstance().getTenantLocationByLeaseId(tenant.getLeaseId()));
            }
        }

        private void setLocationString(String tenantLocation) {
            locationView.setText(tenantLocation);
            locationView.setVisibility(!StringUtils.isEmpty(tenantLocation) ? View.VISIBLE : View.GONE);
        }

        private void setupFavorites(Tenant tenant) {
            if (!accountManager.isLoggedIn() || tenant.getPlaceWiseRetailerId() == null) {
                favoritesButton.setVisibility(View.GONE);
            } else {
                favoritesButton.setVisibility(View.VISIBLE);
                swipeLayout.setSwipeEnabled(true);
                favoriteActive = accountManager.getCurrentUser().getFavorites().contains(tenant.getPlaceWiseRetailerId());
                favoriteOnView.setVisibility(favoriteActive ? View.VISIBLE : View.INVISIBLE);
            }
        }

        private void setupWayfinding(Tenant tenant) {
            if (mapManager.isDestinationWayfindingEnabled(tenant)) {
                navigationButton.setVisibility(View.VISIBLE);
                swipeLayout.setSwipeEnabled(true);
            } else {
                navigationButton.setVisibility(View.GONE);
            }
        }

        private void trackWayfind(String tenantName) {
            MallApplication.getApp().getAnalyticsManager().trackAction(AnalyticsManager.Actions.TenantWayfinding, null, tenantName.toLowerCase());
        }

        @OnClick(R.id.tenant_section)
        public void onClick() {
            context.startActivity(TenantActivity.buildIntent(context, tenants.get(getAdapterPosition())));
        }

        @OnClick(R.id.favorite_button)
        public void onFavoritesClick() {
            favoriteActive = !favoriteActive;
            Tenant tenant = tenants.get(getAdapterPosition());
            if (favoriteActive) {
                accountManager.addFavoriteTenant(tenant);
                AnimationUtils.enterReveal(favoriteOnView);
            } else {
                accountManager.removeFavoriteTenant(tenant);
                //GONE makes the swipe reset but INVISIBLE doesn't
                AnimationUtils.exitReveal(favoriteOnView, View.INVISIBLE);
            }
        }

        @OnClick(R.id.navigation_button)
        public void onNavigationButtonClick() {
            Tenant tenant = tenants.get(getAdapterPosition());
            trackWayfind(tenant.getName());
            context.startActivity(WayfindActivity.buildIntent(context, tenant));
        }
    }
}
