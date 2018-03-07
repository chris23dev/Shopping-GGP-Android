package com.ggp.theclub.customlocale.gateway;

import android.util.Log;

import com.ggp.theclub.customlocale.gateway.rest.LocaleApiClient;
import com.ggp.theclub.customlocale.gateway.rest.ApiInterface;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by john.curtis on 4/17/17.
 */
public class ServerLocaleGatewayImpl implements ServerLocaleGateway {
    private static final String TAG = ServerLocaleGatewayImpl.class.getSimpleName();

    @Override
    public String requestServerLanguageRevision(String url) {

        ApiInterface apiService = getClient();

        Call<Void> japanHeaders = apiService.getLanguageHeaders(url);
        try {
            return japanHeaders.execute().headers().get("x-amz-version-id");
        } catch (IOException e) {
            log(e, "Can't load revision from server");
        }

        return null;
    }

    @Override
    public List<Language> loadLanguages() throws Exception {
        return getClient().getLanguages().execute().body();
    }

    @Override
    public String loadLocalisationFile(String url) {
        ApiInterface apiService = getClient();
        Call<ResponseBody> call = apiService.getLanguage(url);
        try {
            return call.execute().body().string();
        } catch (IOException e) {
            log(e, "Can't load language file from server");
        }

        return null;
    }

    @Override
    public HashMap<Integer, MallLanguages> loadMallsLanguagesSupportList() throws Exception {
        ApiInterface apiService = getClient();
        Call<HashMap<Integer, MallLanguages>> call = apiService.getMallsLanguages();
        try {
            return call.execute().body();
        } catch (IOException e) {
            log(e, "Can't load language for malls");
        }

        return null;
    }

    private ApiInterface getClient() {
        return LocaleApiClient.getLocaleClient();
    }

    private void log(IOException e, String msg) {
        Log.d(TAG, msg, e);
    }

    @Override
    public InputStream loadLocalisationAsInputStream(String url) {
        //TODO JSON can be big. Consider to implement via IO threads or remove at all.
        throw new IllegalStateException("Is not supported yet");
    }
}
