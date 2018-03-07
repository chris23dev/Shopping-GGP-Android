package com.ggp.theclub.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.BaseActivity;
import com.ggp.theclub.activity.ParkAssistResultsActivity;
import com.ggp.theclub.api.ParkAssistClient;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.model.CarLocation;
import com.ggp.theclub.model.ParkingSite;
import com.ggp.theclub.util.ParkingSiteUtils;
import com.ggp.theclub.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

public class ParkAssistSearchFragment extends BaseFragment {
    @BindColor(R.color.dark_gray) int standardColor;
    @BindColor(R.color.red) int errorColor;
    @BindString(R.string.park_assist_disclaimer) String disclaimerText;
    @BindString(R.string.park_assist_error_format) String errorFormat;
    @Bind(R.id.input_view) EditText inputView;
    @Bind(R.id.clear_button) TextView clearButton;
    @Bind(R.id.message_view) TextView messageView;
    @Bind(R.id.park_assist_header) TextView parkAssistHeader;


    private final int INPUT_MIN_LENGTH = 3;

    public static ParkAssistSearchFragment newInstance() {
        return new ParkAssistSearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.park_assist_search_fragment, container);
        parkAssistHeader.setText(getString(R.string.park_assist_header));
        inputView.setHint(getString(R.string.park_assist_hint));
        messageView.setText(getString(R.string.park_assist_error_format));
        return view;
    }

    private Observable<ParkingSite> fetchParkingSite() {
        return Observable.create(subscriber -> {
            mallRepository.queryForParkingSite(parkingSite -> {
                subscriber.onNext(parkingSite);
                subscriber.onCompleted();
            });
        });
    }

    private void fetchCarLocations(ParkingSite site) {
        String licensePlateNumber = inputView.getText().toString();
        HashMap params = ParkAssistClient.getParameters(licensePlateNumber, site.getSiteName(), site.getSecret());

        ParkAssistClient.getInstance().getParkAssistApi().getCarLocations(params).enqueue(new Callback<ArrayList<CarLocation>>() {
            @Override
            public void onResponse(Call<ArrayList<CarLocation>> call, Response<ArrayList<CarLocation>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    handleCarLocations(licensePlateNumber, response.body());
                    trackSearch();
                } else {
                    showErrorState();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<CarLocation>> call, Throwable t) {
                Log.w(getFragmentTag(), t);
                showErrorState();
                showResultsError();
            }
        });
    }

    private void handleCarLocations(String licensePlateNumber, List<CarLocation> carLocations) {
        List<CarLocation> validCarLocations = ParkingSiteUtils.validCarLocations(carLocations);
        if (!validCarLocations.isEmpty()) {
            startActivity(ParkAssistResultsActivity.buildIntent(getActivity(), licensePlateNumber, validCarLocations));
            resetErrorState();
        } else {
            showErrorState();
            showResultsError();
        }
    }

    private void showResultsError() {
        messageView.setText(String.format(errorFormat, inputView.getText().toString()));
    }

    private void showErrorState() {
        inputView.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_IN);
        messageView.setTextColor(errorColor);
    }

    private void resetErrorState() {
        inputView.getBackground().clearColorFilter();
        messageView.setTextColor(standardColor);
        messageView.setText(disclaimerText);
    }

    private void trackSearch() {
        analyticsManager.trackAction(AnalyticsManager.Actions.ParkAssistSearch);
    }

    @OnTextChanged(value = R.id.input_view, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAfterTextChanged(Editable text) {
        for (int i = 0; i < text.toString().length(); i++) {
            if (!Character.isLetterOrDigit(text.charAt(i))) {
                text.replace(i, i + 1, "");
            }
        }
    }

    @OnTextChanged(value = R.id.input_view, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChanged(CharSequence text) {
        clearButton.setVisibility(StringUtils.isEmpty(text) ? View.INVISIBLE : View.VISIBLE);
    }

    @OnEditorAction(R.id.input_view)
    public boolean onEditorAction(int id) {
        if (id == EditorInfo.IME_ACTION_SEARCH) {
            ((BaseActivity) getActivity()).hideKeyboard();
            if (inputView.getText().toString().length() >= INPUT_MIN_LENGTH) {
                fetchParkingSite().subscribe(this::fetchCarLocations);
            } else {
                showErrorState();
            }
            return true;
        }
        return false;
    }

    @OnClick(R.id.clear_button)
    public void onClearButtonClick() {
        inputView.setText(null);
        resetErrorState();
    }
}