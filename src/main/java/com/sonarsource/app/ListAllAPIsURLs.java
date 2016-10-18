package com.sonarsource.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Realm;
import com.ning.http.client.Realm.AuthScheme;
import com.ning.http.client.Response;
import com.sonarsource.app.json.Action;
import com.sonarsource.app.json.Parameter;
import com.sonarsource.app.json.WebService;
import com.sonarsource.app.json.WebServicesList;

public class ListAllAPIsURLs {

  private static final String OUTPUT_FILENAME = "ws-urls.txt";

  private static final String API_WS_LIST = "api/webservices/list";

  private static final Logger LOGGER = LoggerFactory.getLogger(ListAllAPIsURLs.class);

  @com.beust.jcommander.Parameter(names = "-sq.url", description = "SonarQube URL", required = true)
  private String url;

  @com.beust.jcommander.Parameter(names = "-admin.login", description = "SonarQube Admin User", required = true)
  private String login;

  @com.beust.jcommander.Parameter(names = "-admin.password", description = "SonarQube Admin Password", required = true)
  private String password;

  public static void main(String[] args) {
    ListAllAPIsURLs app = new ListAllAPIsURLs();

    new JCommander(app, args);
    try {
      app.process();
    } catch (IOException e) {
      LOGGER.error("Can't write " + OUTPUT_FILENAME + " : " + e);
    }

    LOGGER.info("APIs URLs generation done");

    System.exit(0);
  }

  private Realm getAuthentificationRealm() {
    return new Realm.RealmBuilder()
      .setPrincipal(login)
      .setPassword(password)
      .setUsePreemptiveAuth(true)
      .setScheme(AuthScheme.BASIC)
      .build();
  }

  public void process() throws IOException {

    if (!url.endsWith("/")) {
      url = url + "/";
    }

    AsyncHttpClient httpClient = null;
    FileWriter fstream = null;
    BufferedWriter out = null;

    try {
      httpClient = new AsyncHttpClient();
      Realm realm = getAuthentificationRealm();

      List<String> urls = gatherAllWebServices(httpClient, realm);

      fstream = new FileWriter(OUTPUT_FILENAME);
      out = new BufferedWriter(fstream);
      for (String u : urls) {
        out.write(u);
        out.write("\n");
      }
      out.close();

    } catch (InterruptedException | ExecutionException | IOException e) {
      LOGGER.error("Aborted", e);
    } finally {
      if (httpClient != null) {
        httpClient.close();
      }
      if (fstream != null) {
        fstream.close();
      }
    }
  }

  private List<String> gatherAllWebServices(AsyncHttpClient httpClient, Realm realm) throws InterruptedException, ExecutionException, IOException {
    Future<com.ning.http.client.Response> f = httpClient.prepareGet(url + API_WS_LIST)
      .setRealm(realm)
      .addQueryParam("include_internals", Boolean.TRUE.toString())
      .execute();

    Response r = f.get();
    String jsonData = r.getResponseBody();
    LOGGER.debug(jsonData);

    Gson gson = new Gson();
    WebServicesList wsList = gson.fromJson(jsonData, WebServicesList.class);

    List<WebService> webServices = wsList.getWebServices();
    int countURLs = 0;
    List<String> urls = new ArrayList<>();
    for (WebService webService : webServices) {
      String path = webService.getPath();

      List<Action> actions = webService.getActions();
      if (actions != null && !actions.isEmpty()) {

        String generatedURL;
        for (Action action : actions) {
          generatedURL = path;
          if (!generatedURL.endsWith("/")) {
            generatedURL += "/";
          }

          String actionKey = action.getKey();

          String requiredParameters = generateRequiredParameters(action);
          String generatedURLRequiredParams;
          if (!StringUtils.isEmpty(requiredParameters)) {
            generatedURLRequiredParams = generatedURL + actionKey + "?" + requiredParameters;
          } else {
            generatedURLRequiredParams = generatedURL + actionKey;
          }

          LOGGER.info(generatedURLRequiredParams);
          urls.add(generatedURLRequiredParams);
          countURLs++;

          String optionalParameters = generateOptionalParameters(action);
          String generatedURLAllParams;
          if (!StringUtils.isEmpty(optionalParameters)) {
            if (StringUtils.isEmpty(requiredParameters)) {
              generatedURLAllParams = generatedURL + actionKey + "?" + optionalParameters;
            } else {
              generatedURLAllParams = generatedURL + actionKey + "?" + requiredParameters + "&" + optionalParameters;
            }
            LOGGER.info(generatedURLAllParams);
            urls.add(generatedURLAllParams);
            countURLs++;
          }
        }

      }
    }

    LOGGER.info(countURLs + " generated URLs");
    return urls;

  }

  private String generateRequiredParameters(Action action) {
    List<Parameter> params = action.getParams();
    StringBuilder ret = new StringBuilder("");
    if (params != null && !params.isEmpty()) {
      for (Parameter param : params) {
        if (Boolean.TRUE.toString().equals(param.getRequired())) {
          ret.append(param.getKey());
          if ("componentKey".equals(param.getKey())) {
            ret.append("=org.sonarsource.sonarqube%3Asonarqube");
          } else if ("componentId".equals(param.getKey())) {
            ret.append("=4e548e0d-6ce8-4d10-8e2c-c5474395d575");
          } else {
            ret.append("=AVHDa4kIHP_wYhFWCAxA");
          }
          ret.append("&");
        }
      }
      if (ret.length() > 0) {
        return ret.substring(0, ret.length() - 1);
      }
    }
    return "";
  }

  private String generateOptionalParameters(Action action) {
    List<Parameter> params = action.getParams();
    StringBuilder ret = new StringBuilder("");
    if (params != null && !params.isEmpty()) {
      for (Parameter param : params) {
        if (!Boolean.TRUE.toString().equals(param.getRequired())) {
          ret.append(param.getKey());
          if ("componentKey".equals(param.getKey())) {
            ret.append("=org.sonarsource.sonarqube%3Asonarqube");
          } else if ("componentId".equals(param.getKey())) {
            ret.append("=4e548e0d-6ce8-4d10-8e2c-c5474395d575");
          } else {
            ret.append("=AVHDa4kIHP_wYhFWCAxA");
          }
          ret.append("&");
        }
      }
      if (ret.length() > 0) {
        return ret.substring(0, ret.length() - 1);
      }
    }
    return "";
  }

}
