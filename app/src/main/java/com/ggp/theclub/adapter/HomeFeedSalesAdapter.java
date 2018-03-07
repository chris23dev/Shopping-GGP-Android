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
import com.ggp.theclub.activity.SaleActivity;
import com.ggp.theclub.activity.TenantActivity;
import com.ggp.theclub.activity.WayfindActivity;
import com.ggp.theclub.adapter.HomeFeedSalesAdapter.HomeSaleViewHolder;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.util.ImageUtils;
import com.ggp.theclub.util.IntentUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFeedSalesAdapter extends RecyclerView.Adapter<HomeSaleViewHolder> {
    private Context context;
    private List<Sale> sales = new ArrayList<>();
    private MallManager mallManager = MallApplication.getApp().getMallManager();
    private AnalyticsManager analyticsManager = MallApplication.getApp().getAnalyticsManager();

    public HomeFeedSalesAdapter(Context context) {
        this.context = context;
    }

    public void setSales(List<Sale> sales) {
        this.sales.clear();
        this.sales.addAll(sales);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    @Override
    public HomeSaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_feed_item, parent, false);
        return new HomeSaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeSaleViewHolder holder, int position) {
        holder.onBind(sales.get(position));
    }

    public class HomeSaleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.promotion_location) TextView tenantNameView;
        @Bind(R.id.image_logo) ImageView logoView;
        @Bind(R.id.text_logo) TextView logoNameView;
        @Bind(R.id.promotion_name) TextView saleNameView;
        @Bind(R.id.promotion_date) TextView dateTextView;
        @Bind(R.id.menu_icon) TextView menuIconView;
        @BindString(R.string.share_subject_format) String shareSubjectFormat;
        @BindString(R.string.sale_share_format) String saleShareFormat;

        private Sale sale;

        public HomeSaleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBind(Sale sale) {
            this.sale = sale;
            tenantNameView.setText(getStoreName());
            ImageUtils.setLogo(logoView, logoNameView, sale.getImageUrl(), getStoreName());
            saleNameView.setText(sale.getTitle());
            dateTextView.setText(sale.getDateRange());
        }

        private String getStoreName() {
            return sale.getTenant() != null ? sale.getTenant().getName() : null;
        }

        private void showPopupMenu() {
            PopupMenu popup = new PopupMenu(context, menuIconView);
            popup.getMenuInflater().inflate(R.menu.menu_promotion_item, popup.getMenu());
            popup.getMenu().findItem(R.id.share).setTitle(context.getString(R.string.popup_share));
            popup.getMenu().findItem(R.id.wayfinding).setTitle(context.getString(R.string.popup_wayfinding));
            popup.getMenu().findItem(R.id.store_details).setTitle(context.getString(R.string.popup_store_details));

            popup.setOnMenuItemClickListener((MenuItem item) -> {
                    switch(item.getItemId()) {
                        case R.id.share:
                            onShareClick();
                            break;
                        case R.id.wayfinding:
                            onWayfindingClick();
                            break;
                        case R.id.store_details:
                            onStoreDetailsClick();
                            break;
                    }
                    return true;
                }
            );
            popup.show();
        }

        private void onShareClick() {
            IntentUtils.share((Activity) context, sale, mallManager, analyticsManager);
        }

        private void onWayfindingClick() {
            context.startActivity(WayfindActivity.buildIntent(context, sale.getTenant()));
        }

        private void onStoreDetailsClick() {
            context.startActivity(TenantActivity.buildIntent(context, sale.getTenant()));
        }

        @OnClick(R.id.item_view)
        public void onItemClick() {
            context.startActivity(SaleActivity.buildIntent(context, sales.get(getAdapterPosition())));
        }

        @OnClick(R.id.menu_button)
        public void onMenuClick() {
            showPopupMenu();
        }

    }
}