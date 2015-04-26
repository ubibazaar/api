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

@Path("installation")
public class InstallationResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() throws UnknownHostException {
    return new Gson().toJson(MockStore.installations);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") Long id) throws UnknownHostException {
    return new Gson().toJson(MockStore.installations.stream()
        .filter(i -> i.getId().equals(id))
        .findAny()
        .get());
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(
      @QueryParam(value = "app") Long app,
      @QueryParam(value = "device") Long device,
      @QueryParam(value = "user") Long user
      )
          throws UnknownHostException {
    return new Gson()
        .toJson(MockStore.installations
            .stream()
            .filter(i -> Objects.isNull(app)
                || i.getApp().getId().equals(app))
            .filter(i -> Objects.isNull(device)
                || i.getDevice().getId().equals(device))
            .filter(i -> Objects.isNull(user)
                || i.getDevice().getOwner().getId().equals(user))
            .collect(Collectors.toList()));
  }
}
