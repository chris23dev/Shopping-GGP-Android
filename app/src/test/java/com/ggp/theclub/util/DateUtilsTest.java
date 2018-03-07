package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest extends BaseTest {

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void testCreateDateRange() {
        LocalDate today = new LocalDate(2016, 5, 7);

        String NOW = "NOW";
        String ONGOING= "ONGOING";
        String TODAY = "TODAY";

        Object[][] testData = new Object[][]{
                {new LocalDateTime(2016, 5, 7, 0, 0), new LocalDateTime(2016, 5, 7, 0, 0), TODAY},
                {new LocalDateTime(2016, 5, 9, 0, 0), new LocalDateTime(2016, 5, 9, 0, 0), "Mon, May 9"},
                {new LocalDateTime(2016, 5, 7, 10, 0), new LocalDateTime(2016, 5, 7, 12, 0), TODAY + ": 10:00 AM - 12:00 PM"},
                {new LocalDateTime(2016, 5, 9, 10, 0), new LocalDateTime(2016, 5, 9, 12, 0), "Mon, May 9: 10:00 AM - 12:00 PM"},
                {new LocalDateTime(2016, 5, 6, 0, 0), new LocalDateTime(2016, 5, 10, 0, 0), NOW + " - May 10"},
                {new LocalDateTime(2016, 5, 7, 0, 0), new LocalDateTime(2016, 5, 10, 0, 0), NOW + " - May 10"},
                {new LocalDateTime(2016, 5, 8, 0, 0), new LocalDateTime(2016, 5, 10, 0, 0), "May 8 - May 10"},
                {new LocalDateTime(2016, 5, 6, 0, 0), new LocalDateTime(2016, 9, 30, 0, 0), ONGOING},
                {new LocalDateTime(2016, 5, 7, 0, 0), new LocalDateTime(2016, 9, 30, 0, 0), ONGOING},
                {new LocalDateTime(2016, 5, 8, 0, 0), new LocalDateTime(2016, 9, 30, 0, 0), "May 8 - Sep 30"},
                {new LocalDateTime(2016, 5, 8, 0, 0), new LocalDateTime(2016, 9, 30, 23, 59), "May 8 - Sep 30"},
                {new LocalDateTime(2016, 5, 6, 13, 0), new LocalDateTime(2016, 5, 10, 23, 59), NOW + " - Tue, May 10: 1:00 PM - 12:00 AM"},
                {new LocalDateTime(2016, 5, 6, 10, 0), new LocalDateTime(2016, 5, 10, 12, 0), NOW + " - Tue, May 10: 10:00 AM - 12:00 PM"},
                {new LocalDateTime(2016, 5, 7, 10, 0), new LocalDateTime(2016, 5, 10, 12, 0), NOW + " - Tue, May 10: 10:00 AM - 12:00 PM"},
                {new LocalDateTime(2016, 5, 8, 10, 0), new LocalDateTime(2016, 5, 10, 12, 0), "Sun, May 8 - Tue, May 10: 10:00 AM - 12:00 PM"},
                {new LocalDateTime(2016, 5, 6, 10, 0), new LocalDateTime(2016, 9, 30, 12, 0), ONGOING + ": 10:00 AM - 12:00 PM"},
                {new LocalDateTime(2016, 5, 7, 10, 0), new LocalDateTime(2016, 9, 30, 12, 0), ONGOING + ": 10:00 AM - 12:00 PM"},
                {new LocalDateTime(2016, 5, 8, 10, 0), new LocalDateTime(2016, 9, 30, 12, 0), "Sun, May 8 - Fri, Sep 30: 10:00 AM - 12:00 PM"}};

        for(Object[] testScenario : testData) {
            assertEquals(testScenario[2],
                    DateUtils.getPromotionDateTimeRange((LocalDateTime) testScenario[0], (LocalDateTime) testScenario[1], today));
        }
    }

    @Test
    public void testPopulateDateList() {
        LocalDate startDate = new LocalDate(2016, 5, 7);
        LocalDate endDate = new LocalDate(2016, 5, 11);

        List<LocalDate> dates = DateUtils.populateDateList(startDate, endDate);

        assertEquals(dates.get(0), startDate);
        assertEquals(dates.get(1), new LocalDate(2016, 5, 8));
        assertEquals(dates.get(2), new LocalDate(2016, 5, 9));
        assertEquals(dates.get(3), new LocalDate(2016, 5, 10));
        assertEquals(dates.get(4), endDate);
    }
}