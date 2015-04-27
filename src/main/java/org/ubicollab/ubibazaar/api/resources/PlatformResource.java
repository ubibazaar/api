package org.ubicollab.ubibazaar.api.resources;

import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.api.store.MockStore;

import com.google.gson.Gson;

@Path("platform")
public class PlatformResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() throws UnknownHostException {
    return new Gson().toJson(MockStore.platforms);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) throws UnknownHostException {
    return new Gson().toJson(MockStore.platforms.stream()
        .filter(p -> p.getId().equals(id))
        .findAny()
        .get());
  }

}
