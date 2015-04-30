package org.ubicollab.ubibazaar.api.resources;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.api.store.MockStore;
import org.ubicollab.ubibazaar.core.Installation;

import com.google.gson.Gson;

@Slf4j
@Path("installations")
public class InstallationResource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public String getAll() {
    log.info("hello");

    return new Gson().toJson(MockStore.installations);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getById(@PathParam(value = "id") String id) {
    return new Gson().toJson(MockStore.installations.stream()
        .filter(i -> i.getId().equals(id))
        .findAny()
        .get());
  }

  @GET
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  public String getForQuery(
      @QueryParam(value = "app") String app,
      @QueryParam(value = "device") String device,
      @QueryParam(value = "user") String user
      ) {
    return new Gson()
        .toJson(MockStore.installations
            .stream()
            .filter(i -> Objects.isNull(app)
                || i.getApp().getId().equals(app))
            .filter(i -> Objects.isNull(device)
                || i.getDevice().getId().equals(device))
            .filter(i -> Objects.isNull(user)
                || i.getDevice().getOwner().getId().equals(user))
            .collect(Collectors.toList()));
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response save(Installation installation) {
    // TODO check if device belongs to user
    // TODO check if has id.. should be in the adress actually
    // - no: new installation, store, return including ID
    // - yes: updated properties, store, return
    log.info("Received updated installation {}", new Gson().toJson(installation));

    MockStore.installations.stream()
        .filter(i -> i.getId().equals(installation.getId()))
        .findFirst().get()
        .setManagerFeedback(installation.getManagerFeedback());

    return Response.ok(installation).build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(final @PathParam("id") String id) {
    // TODO check if belongs to user
    // TODO remove from database
    log.info("deleting installation {} ", id);

    return Response.ok().build();
  }

}
