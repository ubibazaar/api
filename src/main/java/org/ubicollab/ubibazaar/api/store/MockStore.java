package org.ubicollab.ubibazaar.api.store;

import java.util.UUID;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;
import org.ubicollab.ubibazaar.core.InstallationMethod;
import org.ubicollab.ubibazaar.core.Manager;
import org.ubicollab.ubibazaar.core.ManagerType;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.collect.ImmutableList;

public class MockStore {

  private static String generateId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static final ImmutableList<InstallationMethod> installationMethods = ImmutableList
      .<InstallationMethod>builder()
      .build();

  public static final ImmutableList<ManagerType> managerTypes = ImmutableList
      .<ManagerType>builder()
      .build();

  public static final ImmutableList<User> users = ImmutableList.<User>builder()
      .build();

  public static final ImmutableList<App> apps = ImmutableList.<App>builder()
      .build();

  public static final ImmutableList<Device> devices = ImmutableList.<Device>builder()
      .build();

  public static final ImmutableList<Manager> managers = ImmutableList.<Manager>builder()
      .build();

  public static final ImmutableList<Installation> installations = ImmutableList
      .<Installation>builder()
      .build();

  public static void main(String[] args) {
    System.out.println(generateId());
  }

}
