package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ggp.theclub.fragment.MallSearchByNameFragment;
import com.ggp.theclub.fragment.MallSearchFragment;

public class MallSearchByNameActivity extends BaseActivity {

    public static Intent buildIntent(Context context) {
        return buildIntent(context, MallSearchByNameActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, MallSearchByNameFragment.newInstance(MallSearchFragment.SearchScreenStyle.ONBOARDING), MallSearchByNameFragment.class.getSimpleName())
                .commit();
    }

    @Override
    protected void configureView() {}
}
