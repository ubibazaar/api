package org.ubicollab.ubibazaar.api.resources;

import java.io.IOException;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;

@Slf4j
@Path("downloads")
public class DownloadResource {

  @GET
  @Path("/ahab_installation")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll(
      @QueryParam(value = "manager_id") String managerId,
      @QueryParam(value = "manager_key") String managerKey) {
    
    if(managerId == null || managerKey == null) {
      Response.status(Status.BAD_REQUEST).build();
    }

    StringWriter sw = new StringWriter();
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile("/ahab_installation_script.sh.mustache");

    try {
      mustache.execute(sw, ImmutableMap.builder()
          .put("manager_id", managerId)
          .put("manager_key", managerKey)
          .build())
          .flush();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    return Response.ok(sw.toString()).build();
  }

}
