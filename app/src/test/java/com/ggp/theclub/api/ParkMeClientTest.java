package com.ggp.theclub.api;

import com.ggp.theclub.BaseTest;

import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class ParkMeClientTest extends BaseTest {

    @Test
    public void getParkMeLotParameters() throws Exception {
        LocalDateTime time = new LocalDateTime();
        HashMap params = ParkMeClient.getLotParameters(time);

        String date = ISODateTimeFormat.dateHourMinute().print(time);

        assertEquals(date, params.get("entry_time").toString());
    }
}