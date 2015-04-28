package org.ubicollab.ubibazaar.api.resources;

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

@Path("manager")
public class ManagerResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() {
    return new Gson().toJson(MockStore.managers);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(MockStore.managers.stream()
        .filter(manager -> manager.getId().equals(id))
        .findAny()
        .get());
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(
      @QueryParam(value = "platform") String type,
      @QueryParam(value = "owner") String owner,
      @QueryParam(value = "device") String device
      ) {
    return new Gson()
        .toJson(MockStore.managers
            .stream()
            .filter(manager -> Objects.isNull(type)
                || manager.getType().getId().equals(type))
            .filter(manager -> Objects.isNull(owner)
                || manager.getOwner().getId().equals(owner))
            .filter(manager -> Objects.isNull(device)
                || manager.getDevices().stream()
                    .filter(d -> d.getId().equals(device))
                    .collect(Collectors.toSet())
                    .size() > 0)
            .collect(Collectors.toList()));
  }
}
