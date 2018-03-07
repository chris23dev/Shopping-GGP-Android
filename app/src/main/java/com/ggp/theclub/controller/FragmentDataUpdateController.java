package com.ggp.theclub.controller;

import com.ggp.theclub.event.HolidayHoursChangedEvent;
import com.ggp.theclub.event.ParkingConfigChangedEvent;
import com.ggp.theclub.event.TheaterCountChangedEvent;
import com.ggp.theclub.manager.MallManager;
import com.ggp.theclub.model.Mall;
import com.ggp.theclub.model.MallConfig;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.DateUtils;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.greenrobot.event.EventBus;

public class FragmentDataUpdateController {

    private MallRepository mallRepository;
    private MallManager mallManager;
    protected LocalDateTime lastCheckTime;
    private boolean holidayHoursActive;
    private boolean blackFridayHoursActive;
    private boolean parkAssistEnabled;
    private boolean parkingAvailabilityEnabled;
    private int theaterCount;
    private final int MINUTES_BETWEEN_CHECK = 6;

    public FragmentDataUpdateController(MallRepository mallRepository, MallManager mallManager) {
        this.mallRepository = mallRepository;
        this.mallManager = mallManager;
        initializeMallFlags();
        initializeTheaterCount();
    }

    public void determineIfDataUpdated() {
        if (!shouldDetermineIfDataChanged(LocalDateTime.now())) {
            return;
        }

        checkForMallDataChanges();
        checkForMovieTheaterDataChanges();

        lastCheckTime = LocalDateTime.now();
    }

    private void initializeMallFlags() {
        Mall mall = mallManager.getMall();
        if (mall != null && mall.isValid()) {
            parkAssistEnabled = mall.getMallConfig().isParkAssistEnabled();
            parkingAvailabilityEnabled = mall.getMallConfig().isParkingAvailabilityEnabled();
            holidayHoursActive = DateUtils.isHolidayHoursActive(mall, LocalDate.now());
            blackFridayHoursActive = DateUtils.isBlackFridayHoursActive(mall, LocalDate.now());
        }
    }

    private void initializeTheaterCount() {
        mallRepository.queryForTheaters(theaters -> {
            if (theaters != null) {
                theaterCount = theaters.size();
            }
        });
    }

    protected boolean shouldDetermineIfDataChanged(LocalDateTime now) {
        return lastCheckTime == null || lastCheckTime.isBefore(now.minusMinutes(MINUTES_BETWEEN_CHECK));
    }

    private void checkForMallDataChanges() {
        mallRepository.queryForMall(mallManager.getMall().getId(), mall -> {
            if (mall != null && mall.isValid()) {
                mallManager.addRecentMall(mall);

                handleParkingConfigChange(mall.getMallConfig());
                handleHoursChange(mall);
            }
        });
    }

    private void checkForMovieTheaterDataChanges() {
        mallRepository.queryForTheaters(theaters -> {
            if (theaters != null) {
                handleTheaterCountChange(theaters.size());
            }
        });
    }

    private void handleParkingConfigChange(MallConfig mallConfig) {
        boolean didChange = parkAssistEnabled != mallConfig.isParkAssistEnabled() ||
                            parkingAvailabilityEnabled != mallConfig.isParkingAvailabilityEnabled();

        if (didChange) {
            EventBus.getDefault().post(new ParkingConfigChangedEvent());
            parkAssistEnabled = mallConfig.isParkAssistEnabled();
            parkingAvailabilityEnabled = mallConfig.isParkingAvailabilityEnabled();
        }
    }

    private void handleHoursChange(Mall mall) {
        boolean newIsHolidayHoursActive = DateUtils.isHolidayHoursActive(mall, LocalDate.now());
        boolean newIsBlackFridayHoursActive = DateUtils.isBlackFridayHoursActive(mall, LocalDate.now());

        boolean didChange = holidayHoursActive != newIsHolidayHoursActive ||
                            blackFridayHoursActive != newIsBlackFridayHoursActive;

        if (didChange) {
            EventBus.getDefault().post(new HolidayHoursChangedEvent());
            holidayHoursActive = newIsHolidayHoursActive;
            blackFridayHoursActive = newIsBlackFridayHoursActive;
        }
    }

    private void handleTheaterCountChange(int currentTheaterCount) {
        if (theaterCount != currentTheaterCount) {
            EventBus.getDefault().post(new TheaterCountChangedEvent());
            theaterCount = currentTheaterCount;
        }
    }
}
