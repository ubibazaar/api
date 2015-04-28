package org.ubicollab.ubibazaar.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.api.store.MockStore;

import com.google.gson.Gson;


@Path("installation_method")
public class InstallationMethodResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() {
    return new Gson().toJson(MockStore.installationMethods);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(MockStore.installationMethods.stream()
        .filter(im -> im.getId().equals(id))
        .findAny()
        .get());
  }
}
