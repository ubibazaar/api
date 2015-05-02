package org.ubicollab.ubibazaar.api.resources;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
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

import org.ubicollab.ubibazaar.api.ApiProperties;
import org.ubicollab.ubibazaar.api.store.DeviceStore;
import org.ubicollab.ubibazaar.api.store.ManagerStore;
import org.ubicollab.ubibazaar.core.Cardinality;
import org.ubicollab.ubibazaar.core.Manager;

import com.google.gson.Gson;

@RolesAllowed("user")
@Path("managers")
public class ManagerResource {

  @Context
  SecurityContext context;

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll() {
    return Response.ok(new Gson().toJson(ManagerStore.getAll().stream()
        .filter(manager -> ResourceUtil.hasAccess(context, manager))
        .collect(Collectors.toList()))).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam(value = "id") String id) {
    Manager found = ManagerStore.getById(id);

    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null || !ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    // if manager doesn't have ALL cardinality, we might want to include installation instructions..
    // go and fetch instructions in case the manager is not installed (check inside)
    if (!found.getType().getDevicePairingCardinality().equals(Cardinality.ALL)) {
      ManagerStore.setInstallationInstructions(found);
    }

    return Response.ok(new Gson().toJson(found)).build();
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getForQuery(
      @QueryParam(value = "type") String type,
      @QueryParam(value = "device") String device
      ) {
    return Response.ok(new Gson().toJson(ManagerStore.getAll().stream()
        .filter(manager -> Objects.isNull(type)
            || manager.getType().getId().equals(type))
        .filter(manager -> ResourceUtil.hasAccess(context, manager))
        .filter(manager -> Objects.isNull(device)
            || manager.getDevices().stream()
                .filter(d -> d.getId().equals(device))
                .collect(Collectors.toSet())
                .size() > 0)
        .collect(Collectors.toList()))).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(Manager manager) {
    // check if all devices belongs to the right user
    boolean allDevicesBelongToUser = manager.getDevices().stream()
        .map(receivedDevice -> DeviceStore.getById(receivedDevice.getId()))
        .allMatch(storedDevice -> ResourceUtil.hasAccess(context, storedDevice));
    
    if(!allDevicesBelongToUser) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    // create manager
    Manager created = ManagerStore.create(manager);

    // construct URI
    URI uri = URI.create(ApiProperties.API_URL + "/resources/managers/"
        + created.getId());

    // return response with the newly created resource's uri
    return Response.created(uri).build();
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(@PathParam(value = "id") String id, Manager manager) {
    // first make sure the id in url and in entity are the same...
    if (manager.getId() == null) {
      // complete the missing id in manager entity and continue
      manager.setId(id);
    } else if (!manager.getId().equals(id)) {
      // different id in url and in entity
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    // check if all devices belongs to the right user
    boolean allDevicesBelongToUser = manager.getDevices().stream()
        .map(receivedDevice -> DeviceStore.getById(receivedDevice.getId()))
        .allMatch(storedDevice -> ResourceUtil.hasAccess(context, storedDevice));
    
    if(!allDevicesBelongToUser) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    Manager found = ManagerStore.getById(id);
    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null || !ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    // update
    ManagerStore.update(manager);

    return Response.ok().build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam(value = "id") String id) {
    Manager found = ManagerStore.getById(id);
    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null || !ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    ManagerStore.delete(id);
    return Response.ok().build();
  }

}
