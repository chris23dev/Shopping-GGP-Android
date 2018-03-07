package com.ggp.theclub.manager;

import com.ggp.theclub.manager.mock.AnalyticsManagerMock;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnalyticsManagerTest {
    private AnalyticsManager analyticsManager;

    @Before
    public void setup() throws Exception {
        analyticsManager = new AnalyticsManagerMock();
    }

    @Test
    public void testSafePut() throws Exception {
        HashMap<String, Object> contextData = new HashMap<>();
        analyticsManager.safePut("keyNonNull", "value", contextData);
        analyticsManager.safePut("keyNull", null, contextData);
        assertTrue(contextData.containsKey("keyNonNull"));
        assertFalse(contextData.containsKey("keyNull"));
    }
}