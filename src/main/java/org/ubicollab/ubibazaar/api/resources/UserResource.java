package org.ubicollab.ubibazaar.api.resources;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ubicollab.ubibazaar.api.ServerProperties;
import org.ubicollab.ubibazaar.api.store.UserStore;
import org.ubicollab.ubibazaar.core.User;

import com.google.gson.Gson;

@Path("users")
public class UserResource {

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam(value = "id") String id) {
    User found = UserStore.getUser(id);

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
  public Response getForQuery(@QueryParam(value = "username") String username) {
    User found = UserStore.getUserByUsername(username);

    // fail fast and return error if does not exist
    // check for existence of the entity
    if (found == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(new Gson().toJson(found)).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(User user) {
    User created = UserStore.createUser(user);

    // construct URI
    URI uri = URI.create(ServerProperties.SERVER_URL + "/resources/users/"
        + created.getId());

    // return response with the newly created resource's uri
    return Response.created(uri).build();
  }

}
