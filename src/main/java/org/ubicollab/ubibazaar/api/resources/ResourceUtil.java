package org.ubicollab.ubibazaar.api.resources;

import javax.ws.rs.core.SecurityContext;

import org.ubicollab.ubibazaar.api.store.ManagerStore;
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
    if (context.isUserInRole("user")) {
      return manager.getOwner().getUsername().equals(context.getUserPrincipal().getName());
    } else if (context.isUserInRole("manager")) {
      return manager.getId().equals(context.getUserPrincipal().getName());
    } else {
      return false;
    }
  }

  public static final boolean hasAccess(SecurityContext context, Installation installation) {
    Device deviceBeingInstalledTo = installation.getDevice();

    if (context.isUserInRole("user")) {
      return hasAccess(context, deviceBeingInstalledTo);
    } else if (context.isUserInRole("manager")) {
      // find the manager
      Manager manager =
          ManagerStore.getById(context.getUserPrincipal().getName());

      // check if the device being installed to is one of the managed by this manager
      return manager.getDevices().stream()
          .anyMatch(managedDevice -> managedDevice.getId().equals(deviceBeingInstalledTo.getId()));
    } else {
      return false;
    }
  }
}
