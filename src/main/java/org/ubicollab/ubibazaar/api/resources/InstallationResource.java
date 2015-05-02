package org.ubicollab.ubibazaar.api.resources;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.ubicollab.ubibazaar.api.ApiProperties;
import org.ubicollab.ubibazaar.api.store.DeviceStore;
import org.ubicollab.ubibazaar.api.store.InstallationStore;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;

import com.google.gson.Gson;

@Path("installations")
public class InstallationResource {

  @Context
  SecurityContext context;

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll() {
    return Response.ok(new Gson().toJson(InstallationStore.getAll().stream()
        .filter(i -> ResourceUtil.hasAccess(context, i))
        .collect(Collectors.toList())))
        .build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam(value = "id") String id) {
    Installation found = InstallationStore.getById(id);

    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null || !ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(new Gson().toJson(found)).build();
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getForQuery(
      @QueryParam(value = "app") String app,
      @QueryParam(value = "device") String device
      ) {
    return Response.ok(new Gson().toJson(InstallationStore.getAll().stream()
        .filter(i -> Objects.isNull(app)
            || i.getApp().getId().equals(app))
        .filter(i -> Objects.isNull(device)
            || i.getDevice().getId().equals(device))
        .filter(i -> ResourceUtil.hasAccess(context, i))
        .collect(Collectors.toList())))
        .build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(Installation installation) {
    // load full device object and check it belongs to user
    Device device = DeviceStore.getById(installation.getDevice().getId());
    if(!ResourceUtil.hasAccess(context, device)) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    // create installation
    Installation created = InstallationStore.create(installation);

    // construct URI
    URI uri = URI.create(ApiProperties.API_URL + "/resources/installations/"
        + created.getId());

    // return response with the newly created resource's uri
    return Response.created(uri).build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(final @PathParam("id") String id) {
    Installation found = InstallationStore.getById(id);

    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null || !ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    InstallationStore.delete(id);
    return Response.ok().build();
  }

}
