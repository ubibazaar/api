package org.ubicollab.ubibazaar.api.resources;

import javax.ws.rs.core.SecurityContext;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;
import org.ubicollab.ubibazaar.core.Manager;

public class ResourceUtil {
  
  public static final boolean hasAccess(SecurityContext context, Device device) {
    return device.getOwner().getUsername().equals(context.getUserPrincipal().getName());
  }

  public static final boolean hasAccess(SecurityContext context, App app) {
    return app.getAuthor().getUsername().equals(context.getUserPrincipal().getName());
  }

  public static final boolean hasAccess(SecurityContext context, Manager manager) {
    return manager.getOwner().getUsername().equals(context.getUserPrincipal().getName());
  }

  public static final boolean hasAccess(SecurityContext context, Installation installation) {
    return hasAccess(context, installation.getDevice());
  }

}
