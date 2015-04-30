package org.ubicollab.ubibazaar.api.resources;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ubicollab.ubibazaar.api.store.ManagerTypeStore;
import org.ubicollab.ubibazaar.core.ManagerType;

import com.google.gson.Gson;

@Path("manager_types")
public class ManagerTypeResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll() {
    return Response.ok(new Gson().toJson(ManagerTypeStore.getManagerTypes())).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam(value = "id") String id) {
    ManagerType found = ManagerTypeStore.getById(id);

    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(new Gson().toJson(found)).build();
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getForQuery(
      @QueryParam(value = "platform") String platform,
      @QueryParam(value = "installation_method") String installationMethod
      ) {
    return Response.ok(new Gson()
        .toJson(ManagerTypeStore.getManagerTypes()
            .stream()
            .filter(mt -> Objects.isNull(platform)
                || mt.getPlatform().getId().equals(platform))
            .filter(mt -> Objects.isNull(installationMethod)
                || mt.getInstallationMethod().getId().equals(installationMethod))
            .collect(Collectors.toList())))
        .build();
  }
}
