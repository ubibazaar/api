package org.ubicollab.ubibazaar.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.api.store.UserStore;
import org.ubicollab.ubibazaar.core.User;

import com.google.gson.Gson;

@Path("users")
public class UserResource {

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(UserStore.getUser(id));
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(@QueryParam(value = "username") String username) {
    return new Gson().toJson(UserStore.getUserByUsername(username));
  }
  
  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public String save(User user) {
    return new Gson().toJson(UserStore.createUser(user));
  }

}
