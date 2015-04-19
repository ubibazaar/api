package org.ubicollab.ubibazaar.api.resources;

import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Instance;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

@Path("instance")
public class InstanceResource {
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getAllInstances() throws UnknownHostException {
    User owner = new User();
    owner.setUsername("stastny.simon@gmail.com");
    owner.setName("Simon Stastny");

    User author = new User();
    author.setName("resin");
    author.setUsername("hello@resin.io");

    Platform platform = new Platform();
    platform.setName("Raspberry Pi");
    
    App app = new App();
    app.setName("Google Coder");
    app.setPlatform(platform);
    app.setAuthor(author);
    ImmutableMap<String, Object> extra = ImmutableMap.<String, Object>builder()
        .put("docker_hub_repository", "resin/rpi-google-coder")
        .put("ports", "80,20")
        .build();
    app.setExtra(extra);
    
    Device device = new Device();
    device.setName("Pequod");
    device.setOwner(owner);
    
    Instance instance = new Instance();
    instance.setApp(app);
    instance.setDevice(device);
    
    return new Gson().toJson(ImmutableList.of(instance));
  }

}
