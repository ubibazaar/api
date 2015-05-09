package org.ubicollab.ubibazaar.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.ubicollab.ubibazaar.api.store.DeviceStore;
import org.ubicollab.ubibazaar.api.store.ManagerStore;
import org.ubicollab.ubibazaar.core.Cardinality;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Manager;

@Path("pairings")
public class PairingsResource {
  
  @Context
  SecurityContext context;
  
  @POST
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response manageDevice(@PathParam(value = "id") String managerId, Device device) {
    Manager manager = ManagerStore.getById(managerId);
    
    // fail fast and return error if does not exist
    // check for existence of the entity
    if (manager == null || !ResourceUtil.hasAccess(context, manager)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    Device found = DeviceStore.getById(device.getId());
    // fail fast and return error if does not exist
    // check for existence of the entity
    // check for access by user
    if (found == null || !ResourceUtil.hasAccess(context, found)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    // check this manager is the same platform.. 
    if(!found.getPlatform().equals(manager.getType().getPlatform())) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    // check if it doesn't manage something else..
    if(Cardinality.ONE.equals(manager.getType().getDevicePairingCardinality())
        && !manager.getDevices().isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    // link device to manager
    ManagerStore.linkDeviceToManager(manager.getId(), device.getId());

    return Response.ok().build();
  }

}
