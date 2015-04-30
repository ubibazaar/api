package org.ubicollab.ubibazaar.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.api.store.InstallationMethodStore;

import com.google.gson.Gson;


@Path("installation_methods")
public class InstallationMethodResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() {
    return new Gson().toJson(InstallationMethodStore.getPlatforms());
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(InstallationMethodStore.getById(id));
  }
}
