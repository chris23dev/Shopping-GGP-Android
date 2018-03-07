package com.ggp.theclub.controller;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FragmentDataUpdateControllerTest {
    private FragmentDataUpdateController fragmentDataUpdateController;

    @Before
    public void setUp() throws Exception {
        fragmentDataUpdateController = new FragmentDataUpdateController(null, null);
    }

    @Test
    public void testShouldDetermineIfDataChanged() {
        LocalDateTime now = new LocalDateTime(2016, 1, 1, 11, 45);

        fragmentDataUpdateController.lastCheckTime = null;
        assertTrue(fragmentDataUpdateController.shouldDetermineIfDataChanged(now));

        fragmentDataUpdateController.lastCheckTime = now.minusMinutes(16);
        assertTrue(fragmentDataUpdateController.shouldDetermineIfDataChanged(now));

        fragmentDataUpdateController.lastCheckTime = now.minusMinutes(14);
        assertFalse(fragmentDataUpdateController.shouldDetermineIfDataChanged(now));
    }
}