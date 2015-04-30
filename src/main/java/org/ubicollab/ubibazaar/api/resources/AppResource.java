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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ubicollab.ubibazaar.api.ServerProperties;
import org.ubicollab.ubibazaar.api.store.AppStore;
import org.ubicollab.ubibazaar.core.App;

import com.google.gson.Gson;

@Path("apps")
public class AppResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll() {
    return Response.ok(new Gson().toJson(AppStore.getAll())).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@PathParam(value = "id") String id) {
    App found = AppStore.getById(id);

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
      @QueryParam(value = "author") String author
      ) {
    return Response.ok(new Gson().toJson(AppStore.getAll().stream()
        .filter(a -> Objects.isNull(platform)
            || a.getPlatform().getId().equals(platform))
        .filter(a -> Objects.isNull(author)
            || a.getAuthor().getId().equals(author))
        .collect(Collectors.toList())))
        .build();
  }


  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(App app) {
    // create app
    App created = AppStore.create(app);

    // construct URI
    URI uri = URI.create(ServerProperties.SERVER_URL + "/resources/apps/"
        + created.getId());

    // return response with the newly created resource's uri
    return Response.created(uri).build();
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(@PathParam(value = "id") String id, App app) {
    // first make sure the id in url and in entity are the same...
    if (app.getId() == null) {
      // complete the missing id in app entity and continue
      app.setId(id);
    } else if (!app.getId().equals(id)) {
      // different id in url and in entity
      return Response.status(Status.BAD_REQUEST).build();
    } 
    
    // fail fast and return error if does not exist
    // check for existence of the entity
    if (AppStore.getById(id) == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    // update
    AppStore.update(app);
    
    return Response.ok().build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam(value = "id") String id) {
    // fail fast and return error if does not exist
    // check for existence of the entity
    if (AppStore.getById(id) == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    AppStore.delete(id);
    return Response.ok().build();
  }


}
