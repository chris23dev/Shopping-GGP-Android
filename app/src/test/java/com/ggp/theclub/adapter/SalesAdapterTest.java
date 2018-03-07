package com.ggp.theclub.adapter;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.Sale;
import com.ggp.theclub.model.Tenant;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.support.membermodification.MemberModifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mock;

public class SalesAdapterTest extends BaseTest {

    SalesAdapter adapter;

    @Before
    public void setUp() throws Exception {
        super.setup();
        adapter = mock(SalesAdapter.class);
    }

    @Test
    public void testGetHeaderId() throws Exception {
        Sale sale1 = createSaleWithStoreName("Apple");
        Sale sale2 = createSaleWithStoreName("Best Buy");
        Sale sale3 = createSaleWithStoreName(null);

        List<Sale> sales = Arrays.asList(sale1, sale2, sale3);
        MemberModifier.field(SalesAdapter.class, "filteredSales").set(adapter, sales);
        doCallRealMethod().when(adapter).getHeaderId(anyInt());

        assertEquals('A', adapter.getHeaderId(0));
        assertEquals('B', adapter.getHeaderId(1));
        assertEquals(0, adapter.getHeaderId(2));
    }

    @Test
    public void testGetFastScrollerBubbleText() throws Exception {
        Sale sale1 = createSaleWithStoreName("apple");
        Sale sale2 = createSaleWithStoreName("best Buy");
        Sale sale3 = createSaleWithStoreName(null);

        List<Sale> sales = Arrays.asList(sale1, sale2, sale3);
        MemberModifier.field(SalesAdapter.class, "filteredSales").set(adapter, sales);
        doCallRealMethod().when(adapter).getHeaderId(anyInt());
        doCallRealMethod().when(adapter).getFastScrollerBubbleText(anyInt());

        assertEquals("A", adapter.getFastScrollerBubbleText(0));
        assertEquals("B", adapter.getFastScrollerBubbleText(1));
        assertEquals(String.valueOf((char)0).toUpperCase(), adapter.getFastScrollerBubbleText(2));
    }

    private Sale createSaleWithStoreName(String storeName) {
        Sale sale = new Sale();
        Tenant tenant = new Tenant();
        tenant.setName(storeName);
        sale.setTenant(tenant);
        return sale;
    }
}