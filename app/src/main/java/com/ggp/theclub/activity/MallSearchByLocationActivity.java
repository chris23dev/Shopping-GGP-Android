package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ggp.theclub.fragment.MallSearchByLocationFragment;
import com.ggp.theclub.fragment.MallSearchFragment;

public class MallSearchByLocationActivity extends BaseActivity {

    public static Intent buildIntent(Context context) {
        return buildIntent(context, MallSearchByLocationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, MallSearchByLocationFragment.newInstance(MallSearchFragment.SearchScreenStyle.ONBOARDING), MallSearchByLocationFragment.class.getSimpleName())
                .commit();
    }

    @Override
    protected void configureView() {}
}
