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

@Path("manager_type")
public class ManagerTypeResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() {
    return new Gson().toJson(MockStore.managerTypes);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(MockStore.managerTypes.stream()
        .filter(mt -> mt.getId().equals(id))
        .findAny()
        .get());
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(
      @QueryParam(value = "platform") String platform,
      @QueryParam(value = "installation_method") String installationMethod
      ) {
    return new Gson()
        .toJson(MockStore.managerTypes
            .stream()
            .filter(mt -> Objects.isNull(platform)
                || mt.getPlatform().getId().equals(platform))
            .filter(mt -> Objects.isNull(installationMethod)
                || mt.getInstallationMethod().getId().equals(installationMethod))
            .collect(Collectors.toList()));
  }
}
