package org.ubicollab.ubibazaar.api.resources;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.api.store.AppStore;

import com.google.gson.Gson;

@Path("apps")
public class AppResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() {
    return new Gson().toJson(AppStore.getAll());
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(AppStore.getById(id));
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(
      @QueryParam(value = "platform") String platform,
      @QueryParam(value = "author") String author
      ) {
    return new Gson().toJson(AppStore.getAll().stream()
        .filter(a -> Objects.isNull(platform)
            || a.getPlatform().getId().equals(platform))
        .filter(a -> Objects.isNull(author)
            || a.getAuthor().getId().equals(author))
        .collect(Collectors.toList()));
  }

}
