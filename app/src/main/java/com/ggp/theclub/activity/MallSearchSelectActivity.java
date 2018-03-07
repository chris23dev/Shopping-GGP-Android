package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ggp.theclub.R;

import butterknife.Bind;
import butterknife.OnClick;

public class MallSearchSelectActivity extends BaseActivity {
    public static int RESULT_SEARCH_BY_NAME = 100;
    public static int RESULT_SEARCH_BY_LOCATION = 101;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, MallSearchSelectActivity.class);
    }

    @Bind(R.id.hello_text) TextView hello;
    @Bind(R.id.select_prompt) TextView selectPrompt;
    @Bind(R.id.malls_latter) TextView mallsLatter;
    @Bind(R.id.or) TextView or;
    @Bind(R.id.search_by_location_title) TextView searchByLocationTitle;
    @Bind(R.id.search_by_name_title) TextView searchByNameTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_search_select_activity);
        hello.setText(getString(R.string.hello));
        selectPrompt.setText(getString(R.string.mall_search_select_prompt));
        mallsLatter.setText(getString(R.string.switch_malls_later));
        or.setText(getString(R.string.or));
        searchByLocationTitle.setText(getString(R.string.search_by_location));
        searchByNameTitle.setText(getString(R.string.search_by_name));

    }

    @Override
    protected void configureView() {}

    @OnClick(R.id.search_by_location)
    public void onSearchByLocationClick() {
        startActivityForResult(MallSearchByLocationActivity.buildIntent(this), RequestCode.MALL_SEARCH_REQUEST_CODE);
    }

    @OnClick(R.id.search_by_name)
    public void onSearchByNameClick() {
        startActivityForResult(MallSearchByNameActivity.buildIntent(this), RequestCode.MALL_SEARCH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.MALL_SEARCH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            } else if (resultCode == RESULT_SEARCH_BY_NAME) {
                onSearchByNameClick();
            } else if (resultCode == RESULT_SEARCH_BY_LOCATION) {
                onSearchByLocationClick();
            }
        }
    }
}