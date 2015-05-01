package org.ubicollab.ubibazaar.api.resources;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.ubicollab.ubibazaar.api.ServerProperties;
import org.ubicollab.ubibazaar.api.store.DeviceStore;
import org.ubicollab.ubibazaar.core.Device;

import com.google.gson.Gson;

@Path("devices")
public class DeviceResource {

  @Context
  SecurityContext context;

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll() {
    return Response.ok(new Gson().toJson(DeviceStore.getAll().stream()
        .filter(app -> ResourceUtil.hasAccess(context, app))
        .collect(Collectors.toList())))
        .build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam(value = "id") String id) {
    Device found = DeviceStore.getById(id);
    String username = context.getUserPrincipal().getName();

    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null || !found.getOwner().getUsername().equals(username)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(new Gson().toJson(found)).build();
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getForQuery(
      @QueryParam(value = "platform") String platform
      ) {
    return Response.ok(new Gson().toJson(DeviceStore.getAll().stream()
        .filter(app -> Objects.isNull(platform)
            || app.getPlatform().getId().equals(platform))
        .filter(app -> ResourceUtil.hasAccess(context, app))
        .collect(Collectors.toList())))
        .build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(Device device) {
    // create device
    Device created = DeviceStore.create(device);

    // construct URI
    URI uri = URI.create(ServerProperties.SERVER_URL + "/resources/devices/"
        + created.getId());

    // return response with the newly created resource's uri
    return Response.created(uri).build();
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(@PathParam(value = "id") String id, Device device) {
    // first make sure the id in url and in entity are the same...
    if (device.getId() == null) {
      // complete the missing id in device entity and continue
      device.setId(id);
    } else if (!device.getId().equals(id)) {
      // different id in url and in entity
      return Response.status(Status.BAD_REQUEST).build();
    }

    Device found = DeviceStore.getById(id);
    // fail fast and return error if does not exist
    // check for existence of the entity
    // check for access by user
    if (found == null || ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    // update
    DeviceStore.update(device);

    return Response.ok().build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam(value = "id") String id) {
    Device found = DeviceStore.getById(id);

    // fail fast and return error if does not exist
    // check for existence of the entity
    // check for access by user
    if (found == null || ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    DeviceStore.delete(id);
    return Response.ok().build();
  }

}
