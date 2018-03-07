package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.DaysOfWeek;
import com.ggp.theclub.model.Hours;
import com.ggp.theclub.model.HoursLineItem;
import com.ggp.theclub.model.OperatingHours;
import com.ggp.theclub.model.OperatingHoursException;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import java8.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HoursUtilsTest extends BaseTest {

    Set<OperatingHours> hoursSet;
    Set<OperatingHoursException> exceptionSet;
    Set<Hours> resultSet;

    final LocalDate mondayDay = new LocalDate(2016,6,20);
    final LocalDate tuesdayDay = new LocalDate(2016,6,21);
    final LocalDate wednesdayDay = new LocalDate(2016,6,22);
    final LocalDate thursdayDay = new LocalDate(2016,6,23);
    final LocalDate fridayDay = new LocalDate(2016,6,24);
    final LocalDate saturdayDay = new LocalDate(2016,6,25);
    final LocalDate sundayDay = new LocalDate(2016,6,26);

    @Before
    public void setup() throws Exception {
        super.setup();
        hoursSet = new HashSet<>();
        exceptionSet = new HashSet<>();
        resultSet = null;

    }

    @Test
    public void testHoursNonSpanningNoExceptions() {
        OperatingHours mondayHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.MON, "8:00", "22:00", true);
        OperatingHours tuesdayHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "9:00", "23:00", true);
        hoursSet.add(mondayHours);
        hoursSet.add(tuesdayHours);

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, mondayDay);
        assertTrue(resultSet.size() == 1);
        Hours resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("8:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("22:00"));

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, tuesdayDay);
        assertTrue(resultSet.size() == 1);
        resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("9:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("23:00"));
    }

    @Test
    public void testOperatingHoursSpanningNoExceptions() {
        OperatingHours mondayHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.TUE, "8:00", "2:00", true);
        OperatingHours tuesdayHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "9:00", "22:00", true);
        hoursSet.add(mondayHours);
        hoursSet.add(tuesdayHours);

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, mondayDay);
        assertTrue(resultSet.size() == 1);
        Hours resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("8:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("2:00"));

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, tuesdayDay);
        assertTrue(resultSet.size() == 1);
        resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("9:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("22:00"));
    }

    @Test
    public void testOperatingHoursSpanningExceptionNonSpanning() {
        OperatingHours mondayHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.MON, "8:00", "21:00", true);
        OperatingHours tuesdayHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "9:00", "22:00", true);
        hoursSet.add(mondayHours);
        hoursSet.add(tuesdayHours);
        OperatingHoursException mondayHoursException = instanceOfOperatingHoursException("06/20", "06/20", null, "10:00", "20:00", true);
        exceptionSet.add(mondayHoursException);

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, mondayDay);
        assertTrue(resultSet.size() == 1);
        Hours resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("10:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("20:00"));

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, tuesdayDay);
        assertTrue(resultSet.size() == 1);
        resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("9:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("22:00"));
    }

    @Test
    public void testOperatingHoursSpanningExceptionSpanning() {
        OperatingHours mondayHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.TUE, "8:00", "2:00", true);
        OperatingHours tuesdayHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "9:00", "22:00", true);
        hoursSet.add(mondayHours);
        hoursSet.add(tuesdayHours);
        OperatingHoursException mondayHoursException = instanceOfOperatingHoursException("06/20", "06/21", null, "10:00", "1:00", true);
        exceptionSet.add(mondayHoursException);

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, mondayDay);
        assertTrue(resultSet.size() == 1);
        Hours resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("10:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("1:00"));

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, tuesdayDay);
        assertTrue(resultSet.size() == 1);
        resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("9:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("22:00"));
    }

    @Test
    public void testOperatingHoursNonSpanningExceptionClosed() {
            OperatingHours mondayHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.MON, "8:00", "22:00", true);
            OperatingHours tuesdayHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "9:00", "23:00", true);
            hoursSet.add(mondayHours);
            hoursSet.add(tuesdayHours);
            OperatingHoursException mondayHoursException = instanceOfOperatingHoursException("06/21", "06/22", null, "0:00", "0:00", false);
            exceptionSet.add(mondayHoursException);

            resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, mondayDay);
            assertTrue(resultSet.size() == 1);
            Hours resultHours = StreamSupport.stream(resultSet).findFirst().get();
            assertEquals(resultHours.getOpenTime(), LocalTime.parse("8:00"));
            assertEquals(resultHours.getCloseTime(), LocalTime.parse("22:00"));

            resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, tuesdayDay);
            assertTrue(resultSet.size() == 1);
            resultHours = StreamSupport.stream(resultSet).findFirst().get();
            assertEquals(resultHours.isOpen(), false);
    }

    @Test
    public void testOperatingHoursSpanningExceptionClosed() {
        OperatingHours mondayHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.TUE, "8:00", "2:00", true);
        OperatingHours tuesdayHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "9:00", "23:00", true);
        hoursSet.add(mondayHours);
        hoursSet.add(tuesdayHours);
        OperatingHoursException mondayHoursException = instanceOfOperatingHoursException("06/21", "06/22", null, "0:00", "0:00", false);
        exceptionSet.add(mondayHoursException);

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, mondayDay);
        assertTrue(resultSet.size() == 1);
        Hours resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.getOpenTime(), LocalTime.parse("8:00"));
        assertEquals(resultHours.getCloseTime(), LocalTime.parse("2:00"));

        resultSet = HoursUtils.getHoursByDate(hoursSet, exceptionSet, tuesdayDay);
        assertTrue(resultSet.size() == 1);
        resultHours = StreamSupport.stream(resultSet).findFirst().get();
        assertEquals(resultHours.isOpen(), false);
    }

    @Test
    public void testGetMultiLineHoursByDate() {
        OperatingHoursException mondayHoursException = instanceOfOperatingHoursException("06/21", "06/22", null, "3:00", "12:00", true);
        mondayHoursException.setHolidayName("Holiday");
        exceptionSet.add(mondayHoursException);

        HoursUtils.FormattedHours formattedHours = HoursUtils.getMultiLineHoursByDate(hoursSet, exceptionSet, new LocalDate(2016,6,21));
        assertEquals(formattedHours.getHoursLabelString(), "Holiday");
        assertEquals(formattedHours.getMultiLineHoursString(), "3:00 AM - 12:00 PM");
    }

    @Test
    public void testGetWeeklyHoursList() {
        OperatingHours mondayHours = instanceOfOperatingHours(DaysOfWeek.MON, DaysOfWeek.MON, "8:00", "22:00", true);
        OperatingHours tuesdayHours = instanceOfOperatingHours(DaysOfWeek.TUE, DaysOfWeek.TUE, "9:00", "23:00", true);
        OperatingHours wednesdayHours = instanceOfOperatingHours(DaysOfWeek.WED, DaysOfWeek.WED, "00:00", "00:00", false);

        hoursSet.add(mondayHours);
        hoursSet.add(tuesdayHours);
        hoursSet.add(wednesdayHours);

        HoursLineItem result = HoursUtils.getWeeklyHoursList(hoursSet, exceptionSet, mondayDay);
        assertEquals(result.getLeftColumn(), "Today's Hours:\nTuesday\nWednesday");
        assertEquals(result.getRightColumn(), "8:00 AM - 10:00 PM\n9:00 AM - 11:00 PM\nClosed");

        result = HoursUtils.getWeeklyHoursList(hoursSet, exceptionSet, thursdayDay);
        assertEquals(result.getLeftColumn(), "Store Hours\nMonday\nTuesday\nWednesday");
        assertEquals(result.getRightColumn(), "\n8:00 AM - 10:00 PM\n9:00 AM - 11:00 PM\nClosed");

        hoursSet = new HashSet<>();
        result = HoursUtils.getWeeklyHoursList(hoursSet, exceptionSet, thursdayDay);
        assertEquals(result.getLeftColumn(), "");
        assertEquals(result.getRightColumn(), "");

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
