package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.model.RefineState.RefineSort;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RefineSortActivity extends BaseActivity {
    @Bind(R.id.refine_default_button) LinearLayout refineDefaultButton;
    @Bind(R.id.refine_ascending_button) LinearLayout refineAscendingButton;
    @Bind(R.id.refine_descending_button) LinearLayout refineDescendingButton;
    @Bind(R.id.refine_sort_label) TextView refineSortLabel;

    public static final String REFINE_SORT_RESULT = "REFINE_SORT_RESULT";

    public static Intent buildIntent(Context context) {
        return buildIntent(context, RefineSortActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refine_sort_activity);
        refineSortLabel.setText(getString(R.string.refine_sort_label));
    }

    @Override
    protected void configureView() {
        setTitle(R.string.refine_title);
        enableBackButton();

        ((TextView) ButterKnife.findById(refineDefaultButton, R.id.description_view)).setText(R.string.refine_sort_default);
        ((TextView) ButterKnife.findById(refineAscendingButton, R.id.description_view)).setText(R.string.refine_sort_ascending);
        ((TextView) ButterKnife.findById(refineDescendingButton, R.id.description_view)).setText(R.string.refine_sort_descending);
    }

    private void setActivityResult(RefineSort refineSort) {
        Intent activityResult = new Intent();
        activityResult.putExtra(REFINE_SORT_RESULT, refineSort);
        setResult(RESULT_OK, activityResult);
        finish();
    }

    @OnClick(R.id.refine_default_button)
    public void onRefineDefaultButtonClick() {
        setActivityResult(RefineSort.DEFAULT);
    }

    @OnClick(R.id.refine_ascending_button)
    public void onRefineAscendingButtonClick() {
        setActivityResult(RefineSort.ASCENDING);
    }

    @OnClick(R.id.refine_descending_button)
    public void onRefineDescendingButtonClick() {
        setActivityResult(RefineSort.DESCENDING);
    }
}