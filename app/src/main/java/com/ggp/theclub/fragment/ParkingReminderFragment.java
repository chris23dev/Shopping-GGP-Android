package com.ggp.theclub.fragment;

import android.app.AlertDialog;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.activity.BaseActivity;
import com.ggp.theclub.event.LocationChangeEvent;
import com.ggp.theclub.manager.AnalyticsManager;
import com.ggp.theclub.manager.GoogleMapManager;
import com.ggp.theclub.manager.LocationServicesManager;
import com.ggp.theclub.manager.ParkingReminderManager;
import com.ggp.theclub.manager.PermissionsManager;
import com.ggp.theclub.manager.PreferencesManager;
import com.ggp.theclub.model.MapOptions;
import com.ggp.theclub.model.ParkingReminder;
import com.ggp.theclub.util.IntentUtils;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import de.greenrobot.event.EventBus;

public class ParkingReminderFragment extends BaseFragment {
    @Bind(R.id.bottom_sheet_saved) LinearLayout bottomSheetSavedView;
    @Bind(R.id.bottom_sheet_unsaved) LinearLayout bottomSheetPlacePinView;
    @Bind(R.id.parking_reminder_header) TextView parkingReminderHeader;
    @Bind(R.id.move_marker_instructions) TextView moveMarkerInstructions;
    @Bind(R.id.note_view) EditText noteView;
    @Bind(R.id.ghost_pin) LinearLayout ghostPinView;
    @Bind(R.id.ghost_pin_icon) ImageView ghostPinIcon;
    @Bind(R.id.clear_button) AppCompatButton clearBtn;
    @Bind(R.id.place_pin_button) AppCompatButton placePinBtn;
    @Bind(R.id.parking_reminder_save_spot_header)  TextView parkingReminderSaveSpotHeader;
    @Bind(R.id.parking_reminder_save_spot_message) TextView parkingReminderSaveSpotMessage;

    private final int ZOOM_LEVEL = 17;
    private final int SAVE_PIN_DELAY = 1300;

    private ParkingReminderManager parkingReminderManager = new ParkingReminderManager();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private PermissionsManager permissionsManager = PermissionsManager.getInstance();

    private LocationServicesManager locationServicesManager = LocationServicesManager.getInstance();
    private GoogleMapManager googleMapManager;
    private Location currentUserLocation;
    //was the current savedReminder loaded from sharedPreferences
    private boolean reminderAlreadySaved = false;
    private ParkingReminder savedReminder;
    private BottomSheetBehavior bottomSheetSaved;
    private BottomSheetBehavior bottomSheetPlacePin;

    public static ParkingReminderFragment newInstance() {
        return new ParkingReminderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, R.layout.parking_reminder_fragment, container);
        noteView.setHint(getString(R.string.parking_reminder_add_note));
        clearBtn.setText(getString(R.string.parking_reminder_clear));
        placePinBtn.setText(getString(R.string.parking_reminder_place_pin_button));
        parkingReminderHeader.setText(getString(R.string.parking_reminder_saved_message));
        parkingReminderSaveSpotHeader.setText(getString(R.string.parking_reminder_save_spot_header));
        parkingReminderSaveSpotMessage.setText(getString(R.string.parking_reminder_save_spot_message));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onFragmentVisible() {
        checkLocationState();
        initialize();
        googleMapManager.insertMap(this, R.id.map_container);
        analyticsManager.trackScreen(AnalyticsManager.Screens.ParkingReminder);
    }

    @Override
    public void onFragmentInvisible() {
        locationServicesManager.stopLocationTracking();
        if(isMapInitialized()) {
            googleMapManager.removeMap(this);
        }
    }

    @Override
    protected void configureView() {
        configureBottomSheets();
    }

    public void onEvent(LocationChangeEvent event) {
        currentUserLocation = event.getLocation();
        if(savedReminder == null) {
            if (!isMapInitialized()) {
                initializeMap(currentUserLocation);
                googleMapManager.setCameraPosition(currentUserLocation);
                delayedInitialization(null);
            }
        }
    }

    @OnClick(R.id.clear_button)
    public void clearClicked() {
        noteView.setText(null);
        googleMapManager.clearDroppedPins();
        //reminderAlreadySaved should be set before hiding the bottomSheet to ensure the text updates correctly
        reminderAlreadySaved = false;
        clearSavedReminders();
        bottomSheetPlacePin.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetSaved.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.place_pin_button)
    public void placePinClicked() {
        Location location = getPinDropPoint();
        saveReminder(new ParkingReminder(location.getLatitude(), location.getLongitude()));
        hideGhostPin();
        setPin(location);
        bottomSheetPlacePin.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetSaved.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnFocusChange(R.id.note_view)
    public void onFocusChanged(boolean focused) {
        if (!focused) {
            saveParkingNote(noteView.getText().toString());
        }
        int textColor = getResources().getColor(focused ? R.color.black : R.color.dark_gray);
        noteView.setTextColor(textColor);
    }

    @OnEditorAction(R.id.note_view)
    public boolean onEditorAction(int id) {
        if (id == EditorInfo.IME_ACTION_DONE) {
            noteView.clearFocus();
            ((BaseActivity) getActivity()).hideKeyboard();
            return true;
        }
        return false;
    }

    private void initialize() {
        fetchSavedReminder();

        if (reminderAlreadySaved) {
            setupSavedReminder();
        } else if(!isMapInitialized()) {
            Location location = mallManager.getMall().getLocation();
            initializeMap(location);
            delayedInitialization(null);
        }
    }

    private void setupSavedReminder() {
        initializeMap(savedReminder.getLocation());
        setupReminderText();
        initializeReminder(savedReminder.getLocation());
    }

    /**
     * Be careful to call this only when bottom sheet hide animation has finished, or it will be very ugly.
     */
    private void setupReminderText() {
        parkingReminderHeader.setText(reminderAlreadySaved ? R.string.saved_reminder_header : R.string.parking_reminder_saved_message);
        moveMarkerInstructions.setVisibility(reminderAlreadySaved ? View.GONE : View.VISIBLE);
        noteView.setText(reminderAlreadySaved ? savedReminder.getNote() : null);
    }

    private void initializeMap(Location location) {
        if(location != null) {
            MapOptions mapOptions = new MapOptions(location.getLatitude(), location.getLongitude());
            mapOptions.setInitialZoomLevel(ZOOM_LEVEL);
            mapOptions.setCurrentLocationEnabled(true);
            googleMapManager = new GoogleMapManager(mapOptions);

            googleMapManager.setPinDragListener(new GoogleMapManager.PinDragListener() {
                @Override
                public void onPinDrag(double latitude, double longitude) {
                    saveReminder(new ParkingReminder(latitude, longitude));
                }
            });
        }
    }

    private void initializeReminder(Location reminderLocation) {
        layoutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                delayedInitialization(reminderLocation);
            }
        });
    }

    private void delayedInitialization(Location reminderLocation) {
        new Handler().postDelayed(() -> {
            if (reminderLocation != null) {
                setPin(reminderLocation);
            }
            hideBottomSheet();
            showBottomSheet();
        }, SAVE_PIN_DELAY);
    }

    private Location getPinDropPoint() {
        return getMapCenter();
    }

    private Location getMapCenter() {
        Point ghostPin = new Point((int) ghostPinIcon.getX() + ghostPinIcon.getWidth()/2, (int) ghostPinIcon.getY() + ghostPinIcon.getHeight());
        return googleMapManager.getLocationFromPoint(ghostPin);
    }

    private boolean isLocationEnabled() {
        return permissionsManager.hasLocationPermission() && locationServicesManager.isDeviceLocationEnabled();
    }

    private void configureBottomSheets() {
        bottomSheetSaved = BottomSheetBehavior.from(bottomSheetSavedView);
        bottomSheetPlacePin = BottomSheetBehavior.from(bottomSheetPlacePinView);
        setSheetCallback(bottomSheetSaved, false);
        setSheetCallback(bottomSheetPlacePin, true);
    }

    private void hideBottomSheet() {
        hideGhostPin();
        bottomSheetSaved.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetPlacePin.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void showBottomSheet() {
        BottomSheetBehavior sheetToDisplay = getDisplaySheet();
        sheetToDisplay.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /*
     * Called when a sheet needs to drop out of view and reappear to update its contents
     */
    private void updateBottomSheet() {
        BottomSheetBehavior sheetToDisplay = getDisplaySheet();
        setSheetUpdateCallback(sheetToDisplay);
        hideBottomSheet();
    }

    private void setSheetUpdateCallback(BottomSheetBehavior sheetToDisplay) {
        sheetToDisplay.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    sheetToDisplay.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    setupReminderText();
                    configureBottomSheets();
                    sheetToDisplay.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
    }

    private void setSheetCallback(BottomSheetBehavior sheet, boolean showGhostPin) {
        sheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch(newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        sheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if(showGhostPin) {
                            showGhostPin();
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //This ensures the text doesn't change until the hide animation is done
                        setupReminderText();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

    }

    private BottomSheetBehavior getDisplaySheet() {
        return savedReminder != null ? bottomSheetSaved : bottomSheetPlacePin;
    }

    private void setPin(Location location) {
        if(location != null && isMapInitialized()) {
            googleMapManager.dropPin(R.drawable.parking_pin, location.getLatitude(), location.getLongitude(), !reminderAlreadySaved);
            analyticsManager.trackAction(AnalyticsManager.Actions.ParkingReminderSave, null);
        }
    }

    private boolean isMapInitialized() {
        return googleMapManager != null;
    }

    private void saveParkingNote(String reminderText) {
        savedReminder.setNote(reminderText);
        saveReminder(savedReminder);
        analyticsManager.trackAction(AnalyticsManager.Actions.ParkingReminderSave, null);
        analyticsManager.trackAction(AnalyticsManager.Actions.ParkingReminderAddNote, null);
    }

    private void saveReminder(ParkingReminder reminder) {
        savedReminder = reminder;
        parkingReminderManager.setParkingReminder(reminder);
    }

    private void clearSavedReminders() {
        savedReminder = null;
        parkingReminderManager.setParkingReminder(null);
    }

    private void fetchSavedReminder() {
        ParkingReminder reminder = parkingReminderManager.getParkingReminder();
        if (reminder != null) {
            this.savedReminder = reminder;
            reminderAlreadySaved = true;
        }
    }

    private void checkLocationState() {
        if(isLocationEnabled()) {
            locationServicesManager.startLocationTracking(getActivity());
        } else if (!preferencesManager.hasShownSettingsDialog()) {
            permissionsManager.checkLocationPermission(getActivity(), granted -> {
                //We need to check isLocationEnabled even if granted == true, since gps may be off
                if (isLocationEnabled()) {
                    locationServicesManager.startLocationTracking(getActivity());
                } else if (!preferencesManager.hasShownSettingsDialog()) {
                    preferencesManager.setShownSettingsDialog(true);
                    showRequestLocationDialog();
                }
            });
        }
    }

    private void showGhostPin() {
        ghostPinView.setPadding(0, 0, 0, bottomSheetPlacePinView.getHeight());
        ghostPinView.setVisibility(View.VISIBLE);
    }

    private void hideGhostPin() {
        ghostPinView.setVisibility(View.INVISIBLE);
    }

    private void showRequestLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.location_disabled_dialog_text);
        builder.setNegativeButton(R.string.location_disabled_dialog_no, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton(R.string.location_disabled_dialog_yes, (dialog, which) -> {
            dialog.dismiss();
            if (!permissionsManager.hasLocationPermission()) {
                IntentUtils.showAppSettings(getActivity());
            } else {
                IntentUtils.showGpsSettings(getActivity());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
