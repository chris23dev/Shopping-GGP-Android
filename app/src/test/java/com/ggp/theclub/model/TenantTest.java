package com.ggp.theclub.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TenantTest {

    @Test
    public void testIsAnchor() throws Exception {
        Tenant tenant = new Tenant();
        Tenant.Unit mockUnit = mock(Tenant.Unit.class);
        tenant.setUnit(mockUnit);

        when(mockUnit.getType()).thenReturn("ANCHOR");
        assertTrue(tenant.isAnchor());

        when(mockUnit.getType()).thenReturn("NOT_ANCHOR");
        assertFalse(tenant.isAnchor());

        when(mockUnit.getType()).thenReturn(null);
        assertFalse(tenant.isAnchor());

        tenant.setUnit(null);
        assertFalse(tenant.isAnchor());
    }
}
