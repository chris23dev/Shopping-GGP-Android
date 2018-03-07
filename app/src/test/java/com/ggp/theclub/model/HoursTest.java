package com.ggp.theclub.model;

import com.ggp.theclub.BaseTest;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HoursTest extends BaseTest{

    //now is Monday
    LocalDateTime now = new LocalDateTime(2016, 7, 25, 10, 0, 0);

    @Test
    public void testOperatingHoursIsOpenAtTime() {
        Hours operatingHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.MON, "08:00", "22:00", true);
        operatingHours.setOpen(true);

        assertTrue(operatingHours.isOpenAtTime(now));

        operatingHours.setOpen(false);
        assertFalse(operatingHours.isOpenAtTime(now));

        operatingHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.MON, "11:00", "22:00", true);
        assertFalse(operatingHours.isOpenAtTime(now));

        operatingHours = instanceOfOperatingHours(DaysOfWeek.SUN, DaysOfWeek.MON, "09:00", "22:00", true);
        assertTrue(operatingHours.isOpenAtTime(now));

        operatingHours = instanceOfOperatingHours(DaysOfWeek.SUN, DaysOfWeek.MON, "09:00", "08:00", true);
        assertFalse(operatingHours.isOpenAtTime(now));

        operatingHours = instanceOfOperatingHours(DaysOfWeek.SUN, DaysOfWeek.SUN, "09:00", "22:00", true);
        assertFalse(operatingHours.isOpenAtTime(now));

        operatingHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "09:00", "22:00", true);
        assertFalse(operatingHours.isOpenAtTime(now));

        operatingHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.WED, "09:00", "02:00", true);
        assertTrue(operatingHours.isOpenAtTime(now));
    }

    @Test
    public void testOperatingHoursExceptionIsOpenAtTime() {
        /** TODO figure out issue with mockStatic that prevents this from working
        Hours operatingHoursException = instanceOfOperatingHoursException("07/25", "07/25", null, "0:00", "0:00", false);
        assertFalse(operatingHoursException.isOpenAtTime(now));

        operatingHoursException = instanceOfOperatingHoursException("07/25", "07/25", null, "00:01", "11:00", true);
        assertTrue(operatingHoursException.isOpenAtTime(now));

        operatingHoursException = instanceOfOperatingHoursException("07/26", "07/26", null, "0:00", "10:00", true);
        assertFalse(operatingHoursException.isOpenAtTime(now));
         **/
    }


    public static OperatingHours instanceOfOperatingHours(DaysOfWeek startDay, DaysOfWeek endDay, String openTime, String closeTime, boolean open) {
        OperatingHours operatingHours = new OperatingHours();
        operatingHours.setStartDay(startDay);
        operatingHours.setEndDay(endDay);
        operatingHours.setOpenTime(openTime);
        operatingHours.setCloseTime(closeTime);
        operatingHours.setOpen(open);
        return operatingHours;
    }

    public static OperatingHoursException instanceOfOperatingHoursException(
            String startMonthDay, String endMonthDay, LocalDate validUntilDate, String openTime, String closeTime, boolean open) {
        OperatingHoursException operatingHoursException = new OperatingHoursException();
        operatingHoursException.setStartMonthDay(startMonthDay);
        operatingHoursException.setEndMonthDay(endMonthDay);
        operatingHoursException.setValidUntilDate(validUntilDate == null? null : validUntilDate.toDate());
        operatingHoursException.setOpenTime(openTime);
        operatingHoursException.setCloseTime(closeTime);
        operatingHoursException.setOpen(open);
        return operatingHoursException;
    }
}
