package org.ubicollab.ubibazaar.api.resources;

import java.net.UnknownHostException;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.api.store.MockStore;

import com.google.gson.Gson;

@Path("app")
public class AppResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() throws UnknownHostException {
    return new Gson().toJson(MockStore.apps);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") Long id) throws UnknownHostException {
    return new Gson().toJson(MockStore.apps.stream()
        .filter(a -> a.getId().equals(id))
        .findAny()
        .get());
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(
      @QueryParam(value = "platform") Long platform,
      @QueryParam(value = "author") Long author
      )
          throws UnknownHostException {
    return new Gson().toJson(MockStore.apps.stream()
        .filter(a -> Objects.isNull(platform) 
            || a.getPlatform().getId().equals(platform))
        .filter(a -> Objects.isNull(author) 
            || a.getAuthor().getId().equals(author))
        .collect(Collectors.toList()));
  }

}
