package org.ubicollab.ubibazaar.api.resources;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.net.UnknownHostException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("ping")
public class PingResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String ping() throws UnknownHostException {
    return new Gson().toJson(ImmutableMap.of("pong", System.currentTimeMillis()));
  }

}
