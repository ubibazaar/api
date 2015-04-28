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

@Path("device")
public class DeviceResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() {
    return new Gson().toJson(MockStore.devices);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(MockStore.devices.stream()
        .filter(d -> d.getId().equals(id))
        .findAny()
        .get());
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(
      @QueryParam(value = "platform") String platform,
      @QueryParam(value = "owner") String owner
      ) {
    return new Gson()
        .toJson(MockStore.devices
            .stream()
            .filter(d -> Objects.isNull(platform)
                || d.getPlatform().getId().equals(platform))
            .filter(d -> Objects.isNull(owner)
                || d.getOwner().getId().equals(owner))
            .collect(Collectors.toList()));
  }
}
