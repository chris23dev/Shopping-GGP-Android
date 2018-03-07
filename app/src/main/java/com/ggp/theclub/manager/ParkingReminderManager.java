package com.ggp.theclub.manager;

import com.ggp.theclub.event.ParkingReminderUpdateEvent;
import com.ggp.theclub.model.ParkingReminder;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

public class ParkingReminderManager {

    //expiration time in seconds for reminder 172800 s = 48 hours
    private final int REMINDER_EXPIRATION_TIME = 172800;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    public boolean hasParkingReminder() {
        return getParkingReminder() != null;
    }

    public void setParkingReminder(ParkingReminder parkingReminder) {
        preferencesManager.saveObject(PreferencesManager.PARKING_REMINDER, parkingReminder);
        EventBus.getDefault().post(new ParkingReminderUpdateEvent());
    }

    public ParkingReminder getParkingReminder() {
        ParkingReminder reminder = preferencesManager.getObject(PreferencesManager.PARKING_REMINDER, ParkingReminder.class);
        return reminder != null && !isReminderLocationExpired(reminder) ? reminder : null;
    }

    private boolean isReminderLocationExpired(ParkingReminder reminder) {
        return reminder == null || DateTime.now().getMillis() - reminder.getSavedTime() > REMINDER_EXPIRATION_TIME*1000;
    }

}
