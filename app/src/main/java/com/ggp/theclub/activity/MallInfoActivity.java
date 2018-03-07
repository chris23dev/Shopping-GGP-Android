package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.MallHoursAdapter;
import com.ggp.theclub.customlocale.LocaleUtils;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.util.DateUtils;
import com.ggp.theclub.util.IntentUtils;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.view.CustomRecyclerView;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

public class MallInfoActivity extends BaseActivity {
    private final int NUM_HOURS_DAYS = 7;

    @BindString(R.string.mall_info) String mallInfoTitle;
    @Bind(R.id.mall_name) TextView mallName;
    @Bind(R.id.address) TextView addressLine1;
    @Bind(R.id.city_state_zip) TextView cityStateZip;
    @Bind(R.id.phone_text) TextView phone;
    @Bind(R.id.hours_list) RecyclerView hoursList;
    @Bind(R.id.holiday_hours_button) View holidayHoursButton;
    @Bind(R.id.holiday_hours_arrow_view) TextView holidayHoursArrow;
    @Bind(R.id.holiday_hours_list) CustomRecyclerView holidayHoursList;
    @Bind(R.id.hours_header) TextView hoursHeader;
    @Bind(R.id.hours_description) TextView hoursDescription;
    @Bind(R.id.holiday_hours_button_title) TextView holidayHoursBtnTitle;
    @Bind(R.id.change_mall_button) Button changeMallBtn;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, MallInfoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_info_activity);
        analyticsManager.trackScreen(AnalyticsManager.Screens.MallInfo);
        hoursHeader.setText(getString(R.string.mall_info_hours_header));
        hoursDescription.setText(getString(R.string.mall_info_hours_description));
        holidayHoursBtnTitle.setText(getString(R.string.holiday_hours_button));
        changeMallBtn.setText(getString(R.string.change_mall_button));
    }

    @Override
    protected void configureView() {
        enableBackButton();
        setTitle(mallInfoTitle);

        Mall mall = mallManager.getMall();
        mallName.setText(mall.getName());
        addressLine1.setText(mall.getAddress().getLine1());
        cityStateZip.setText(mall.getAddress().getCityAndState() + " " + mall.getAddress().getZip());
        phone.setText(StringUtils.prettyPrintRawPhoneNumber(mall.getPhoneNumber()));

        populateMallHours();
        populateHolidayHours();
    }

    private void populateMallHours() {
        List<LocalDate> dates = DateUtils.populateDateList(LocalDate.now(), LocalDate.now().plusDays(NUM_HOURS_DAYS));
        hoursList.setLayoutManager(new LinearLayoutManager(this));
        hoursList.setAdapter(new MallHoursAdapter(dates, false, true, shouldShowDayLabel()));
    }

    private void populateHolidayHours() {
        Mall mall = mallManager.getMall();
        if (DateUtils.isHolidayHoursActive(mall, LocalDate.now())) {
            //don't display holiday dates twice, take max of next display date or holiday start date
            LocalDate startDate = DateUtils.max(mall.getHolidayHoursStartDate(), LocalDate.now().plusDays(NUM_HOURS_DAYS + 1));
            if (startDate.isBefore(mall.getHolidayHoursEndDate())) {
                holidayHoursButton.setVisibility(View.VISIBLE);
                List<LocalDate> dates = DateUtils.populateDateList(startDate, mall.getHolidayHoursEndDate());

                holidayHoursList.setAdapter(new MallHoursAdapter(dates, true, true, shouldShowDayLabel()));
            }
        }
    }

    @OnClick(R.id.change_mall_button)
    public void changeMallClicked() {
        setResult(BaseActivity.RESULT_OK);
        finish();
    }

    @OnClick(R.id.phone_text)
    public void onPhoneClick() {
        Mall mall = mallManager.getMall();
        IntentUtils.startPhoneNumberIntent(mall.getPhoneNumber(), this);
        HashMap<String, Object> contextData = new HashMap<>();
        contextData.put(AnalyticsManager.ContextDataKeys.MallPhoneNumber, mall.getPhoneNumber());
        analyticsManager.trackAction(AnalyticsManager.Actions.NavCall, contextData);
    }

    @OnClick(R.id.location_layout)
    public void onAddressButtonClick() {
        Mall mall = mallManager.getMall();
        String geoUri = "geo:0,0?q=" + StringUtils.formatAddress(mall.getAddress());
        IntentUtils.startIntentIfSupported(new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)), this);
        analyticsManager.trackAction(AnalyticsManager.Actions.NavDirections);
    }

    @OnClick(R.id.holiday_hours_button)
    public void onHolidayHoursClick(){
        if (holidayHoursList.isShown()) {
            holidayHoursList.setVisibility(View.GONE);
            holidayHoursArrow.setText(R.string.expand_icon);
        } else {
            holidayHoursList.setVisibility(View.VISIBLE);
            holidayHoursArrow.setText(R.string.contract_icon);
        }
    }

    private boolean shouldShowDayLabel() {
        return LocaleUtils.isNowNextLanguageCode("en");
    }

}