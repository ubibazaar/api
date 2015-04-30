package org.ubicollab.ubibazaar.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ubicollab.ubibazaar.api.store.PlatformStore;
import org.ubicollab.ubibazaar.core.Platform;

import com.google.gson.Gson;

@Path("platforms")
public class PlatformResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll() {
    return Response.ok(new Gson().toJson(PlatformStore.getPlatforms())).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam(value = "id") String id) {
    Platform found = PlatformStore.getPlatform(id);

    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(new Gson().toJson(found)).build();
  }

}
