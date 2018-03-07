package com.ggp.theclub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.adapter.LanguageArrayRecyclerAdapter;
import com.ggp.theclub.adapter.utils.ItemClickSupport;
import com.ggp.theclub.customlocale.BackStackUtils;
import com.ggp.theclub.customlocale.LocaleService;
import com.ggp.theclub.customlocale.LocaleServiceUtils;
import com.ggp.theclub.customlocale.LocaleUtils;
import com.ggp.theclub.customlocale.gateway.Language;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.jibestream.jibestreamandroidlibrary.main.M;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.ThreadMode;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChooseLanguageActivity extends BaseActivity {
    private static final String TAG = ChooseLanguageActivity.class.getSimpleName();

    @Bind(R.id.language_list)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.loading_local_indicator) ProgressBar loadingIndicator;

    private LanguageArrayRecyclerAdapter mLanguagesAdapter;
    private String mFutureLanguageCode;

    public static Intent buildIntent(Context context) {
        return buildIntent(context, ChooseLanguageActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_language_activity);

        mLanguagesAdapter = new LanguageArrayRecyclerAdapter();
        mRecyclerView.setAdapter(mLanguagesAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> onClick(position)
        );

        mallRepository.queryForLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onSuccess,
                        throwable -> Log.d(TAG, "Can't load languages from server side", throwable)
                );
    }

    private void onClick(int position) {
        String code = mLanguagesAdapter.get(position).code;
        if(LocaleUtils.isCurrentLanguageCode(code)){
            setResult(RESULT_CANCELED);
            finish();
        } else {
            mFutureLanguageCode = code;
            LocaleServiceUtils.checkLocale(ChooseLanguageActivity.this, mFutureLanguageCode);
            showProgress();
        }
    }

    private void onSuccess(List<Language> languages) {

        mLanguagesAdapter.clear();

        if(languages == null || languages.size() == 0){
            return;
        }

        mLanguagesAdapter.addAll(languages);

        String language = getCurrentLanguageCode();

        for (int i = 0; i < languages.size(); i++) {
            Language languageModel = languages.get(i);
            if(language.equals(languageModel.code)){
                    mLanguagesAdapter.setChecked(i);
            }
        }
    }

    @Override
    protected void configureView() {
        setTitle(getString(R.string.language_settings_title));
    }

    public void onEventMainThread(LocaleService.LocaleUpdatedEvent updatedEvent) {
        doFullResetData(updatedEvent.mLanguageCode);
    }

    public void onEventMainThread(LocaleService.ErrorLocaleUpdatedEvent errorEvent) {
        hideProgress();
    }

    public void onEventMainThread(LocaleService.LocaleUpdatedSkipEvent updatedSkipEvent) {
        doFullResetData(getCurrentLanguageCode());
    }

    private void showProgress() {
        if(!isVisible()){
            loadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    private boolean isVisible() {
        return loadingIndicator.getVisibility() == View.VISIBLE;
    }

    private void hideProgress() {
        if(isVisible()){
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackButtonClick() {
        if(!isVisible()){
            super.onBackButtonClick();
        }
    }

    private void doFullResetData(String languageCode) {
        LocaleUtils.setCurrentLanguage(languageCode);
        clearMapData();
        hideProgress();
        setResult(RESULT_OK);
        BackStackUtils.fullyRestartApp(this);
    }

    private void clearMapData() {
        M map = MapManager.getInstance().getMap();
        int mallId = MallApplication.getApp().getMallManager().getMall().getId();
        map.onDestroy();
        getDestinationFile(mallId).delete();
    }

    @NonNull
    private File getDestinationFile(int mallId) {
        return new File(getFilesDir(), String.valueOf(mallId));
    }

    private String getCurrentLanguageCode() {
        return LocaleUtils.getCurrentLanguageCode();
    }
}