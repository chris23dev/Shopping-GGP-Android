package com.ggp.theclub.customlocale.gateway;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by john.curtis on 4/17/17.
 */
public interface ServerLocaleGateway {

    String requestServerLanguageRevision(String url) throws Exception;
    List<Language> loadLanguages() throws Exception;

    String loadLocalisationFile(String url) throws Exception;
    HashMap<Integer, MallLanguages> loadMallsLanguagesSupportList() throws Exception;
    InputStream loadLocalisationAsInputStream(String url) throws Exception;

}
