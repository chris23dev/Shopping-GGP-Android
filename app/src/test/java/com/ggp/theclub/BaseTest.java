package com.ggp.theclub;

import android.graphics.Color;

import com.ggp.theclub.manager.AccountManager;
import com.ggp.theclub.manager.mock.AccountManagerMock;
import com.google.gson.Gson;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MallApplication.class)
public abstract class BaseTest {
    protected Gson gson;
    protected AccountManager accountManager;

    public void setup() throws Exception {
        gson = new Gson();
        accountManager = new AccountManagerMock();
        MallApplication app = mock(MallApplication.class);
        mockStatic(MallApplication.class);
        when(MallApplication.getApp()).thenReturn(app);
        when(app.getAccountManager()).thenReturn(accountManager);
        when(app.getCacheDir()).thenReturn(mock(File.class));
        when(app.getString(anyInt())).then((invocation) -> getString((int) invocation.getArguments()[0]));
        when(app.getStringArray(anyInt())).then((invocation) -> getStringArray((int) invocation.getArguments()[0]));
        when(app.getColorById(anyInt())).then((invocation) -> getColor((int) invocation.getArguments()[0]));
    }

    public int getColor(int id) {
        return Color.BLACK;
    }

    public String getString(int id) {
        switch (id) {
            case R.string.now: return "NOW";
            case R.string.ongoing: return "ONGOING";
            case R.string.today: return "TODAY";
            case R.string.tenant_today_hours: return "Today\'s Hours:";
            case R.string.tenant_store_hours: return "Store Hours";
            case R.string.closed: return "Closed";
            case R.string.temporarily_closed_message: return "(Temporarily Closed)";
            case R.string.my_favorites_category_label: return "My Favorites";
            case R.string.all_sales_category_label: return "All Sales";
            case R.string.all_stores_category_label: return "All Stores";
            default: return null;
        }
    }

    public String[] getStringArray(int id) {
        switch (id) {
            default:
                return null;
        }
    }
}