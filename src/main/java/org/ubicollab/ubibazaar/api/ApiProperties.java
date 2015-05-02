package org.ubicollab.ubibazaar.api;

import java.io.InputStream;
import java.util.Properties;

import org.ubicollab.ubibazaar.api.store.Database;

public class ApiProperties {

  public static final String API_URL;
  public static final String DATASOURCE;
  public static final String BITLY_ACCESS_TOKEN;

  static {
    String apiUrl = null;
    String datasourceContext = null;
    String bitlyAccessToken = null;

    try {
      Properties prop = new Properties();
      InputStream in = Database.class.getResourceAsStream("/api.properties");
      prop.load(in);
      in.close();

      apiUrl = prop.getProperty("api_url");
      datasourceContext = prop.getProperty("datasource_jndi_name");
      bitlyAccessToken = prop.getProperty("bitly_access_token");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      API_URL = apiUrl;
      DATASOURCE = datasourceContext;
      BITLY_ACCESS_TOKEN = bitlyAccessToken;
    }
  }


}
