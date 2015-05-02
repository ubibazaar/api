package org.ubicollab.ubibazaar.api.store;

import java.util.UUID;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.api.ApiProperties;

import com.google.gson.JsonParser;

public class StoreUtil {

  public static final String generateRandomId() {
    return UUID.randomUUID().toString().replace("-", "");
  }
  
  public static final String shortenUrl(String longUrl) {
    WebTarget target = ClientBuilder.newClient()
        .target("https://api-ssl.bitly.com")
        .path("/v3/shorten");

    String response = target
        .queryParam("longUrl", longUrl)
        .queryParam("access_token", ApiProperties.BITLY_ACCESS_TOKEN)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(String.class);
    
    JsonParser jp = new JsonParser();
    String shortUrl = jp.parse(response).getAsJsonObject()
        .get("data").getAsJsonObject()
        .get("url").getAsString();
    
    return shortUrl;
  }

}
