package org.ubicollab.ubibazaar.api.store;

import java.util.UUID;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Cardinality;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;
import org.ubicollab.ubibazaar.core.InstallationMethod;
import org.ubicollab.ubibazaar.core.Manager;
import org.ubicollab.ubibazaar.core.ManagerType;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MockStore {
  private static final String ARDUINO_SOURCE_FILE = "arduino_source_file";
  private static final String DOCKER_HUB_REPO_PROPERTY = "docker_hub_repo";
  
  private static String generateId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  // platform, deployment method and manager type
  public static final Platform RPI = Platform.builder()
      .id(generateId())
      .name("Raspbery Pi")
      .build();
  public static final Platform ARDUINO = Platform.builder()
      .id(generateId())
      .name("Arduino")
      .build();
  public static final Platform LINUX_X64 = Platform.builder()
      .id(generateId())
      .name("Linux x64")
      .build();

  public static final InstallationMethod DOCKER = InstallationMethod.builder()
      .id(generateId())
      .name("Docker")
      .properties(ImmutableList.of(DOCKER_HUB_REPO_PROPERTY))
      .build();
  public static final InstallationMethod ARDUINO_BT = InstallationMethod.builder()
      .id(generateId())
      .name("Arduino Bluetooth Deployment")
      .properties(ImmutableList.of(ARDUINO_SOURCE_FILE))
      .build();

  public static final ManagerType AHAB_RPI = ManagerType.builder()
      .id(generateId())
      .name("Ahab")
      .platform(RPI)
      .devicePairingCardinality(Cardinality.ONE)
      .installationMethod(DOCKER)
      .build();
  public static final ManagerType MCSS = ManagerType.builder()
      .id(generateId())
      .name("MCSS for Android")
      .platform(ARDUINO)
      .devicePairingCardinality(Cardinality.ALL)
      .installationMethod(ARDUINO_BT)
      .build();
  public static final ManagerType AHAB_LINUX_X64 = ManagerType.builder()
      .id(generateId())
      .name("Ahab for Linux x64")
      .platform(LINUX_X64)
      .devicePairingCardinality(Cardinality.ONE)
      .installationMethod(DOCKER)
      .build();

  // user
  public static final User SIMON = User.builder()
      .id(generateId())
      .username("simon")
      .name("Simon Stastny")
      .build();
  public static final User OLA = User.builder()
      .id(generateId())
      .username("ola")
      .name("Ola Nordmann")
      .build();

  // device and manager
  public static final Device CURARE = Device.builder()
      .id(generateId())
      .name("Curare")
      .platform(LINUX_X64)
      .owner(SIMON)
      .build();
  public static final Device PEQUOD = Device.builder()
      .id(generateId())
      .name("Pequod")
      .platform(RPI)
      .owner(SIMON)
      .build();
  public static final Device BLACK_PEARL = Device.builder()
      .id(generateId())
      .name("Black Pearl")
      .platform(RPI)
      .owner(SIMON)
      .build();
  public static final Device KOBAYASHI_MARU = Device.builder()
      .id(generateId())
      .name("Kobayashi Maru")
      .platform(RPI)
      .owner(SIMON)
      .build();
  public static final Device ENOLA_GAY = Device.builder()
      .id(generateId())
      .name("Enola Gay")
      .platform(ARDUINO)
      .owner(SIMON)
      .build();
  public static final Device BOCKSCAR = Device.builder()
      .id(generateId())
      .name("Bockscar")
      .platform(ARDUINO)
      .owner(SIMON)
      .build();
  public static final Device AKUTAN_ZERO = Device.builder()
      .id(generateId())
      .name("Akutan Zero")
      .platform(ARDUINO)
      .owner(OLA)
      .build();

  public static final Manager AHAB_ON_CURARE = Manager.builder()
      .id("1aa191aac64b4770ba6fc05bc36cf1f5")
      .name("Ahab on Curare")
      .type(AHAB_LINUX_X64)
      .devices(ImmutableList.of(CURARE))
      .build();
  public static final Manager AHAB_ON_PEQUOD = Manager.builder()
      .id(generateId())
      .name("AHAB_ON_PEQUOD")
      .type(AHAB_RPI)
      .devices(ImmutableList.of(PEQUOD))
      .build();
  public static final Manager AHAB_ON_BLACK_PEARL = Manager.builder()
      .id(generateId())
      .name("AHAB_ON_BLACK_PEARL")
      .type(AHAB_RPI)
      .devices(ImmutableList.of(BLACK_PEARL))
      .build();
  public static final Manager AHAB_ON_KOBAYASHI_MARU = Manager.builder()
      .id(generateId())
      .name("AHAB_ON_KOBAYASHI_MARU")
      .type(AHAB_RPI)
      .devices(ImmutableList.of(KOBAYASHI_MARU))
      .build();
  public static final Manager SIMONS_MSCC = Manager.builder()
      .id(generateId())
      .name("Simon's MCSS")
      .type(MCSS)
      .devices(ImmutableList.of(AKUTAN_ZERO, BOCKSCAR, ENOLA_GAY))
      .build();

  // app
  public static final App COSSMIC = App.builder()
      .id(generateId())
      .name("CoSSMIC EMonCMS")
      .platform(RPI)
      .author(SIMON)
      .properties(ImmutableMap.of(DOCKER_HUB_REPO_PROPERTY, "simonstastny/cossmic"))
      .build();
  public static final App ARDUINO_APP = App.builder()
      .id(generateId())
      .name("Random Arduino App")
      .platform(ARDUINO)
      .author(SIMON)
      .properties(ImmutableMap.of(ARDUINO_SOURCE_FILE, "import fdf lisfysfly fy sufy sdfy"))
      .build();
  public static final App FRMS = App.builder()
      .id(generateId())
      .name("Jihocesky Kotel")
      .platform(RPI)
      .author(OLA)
      .properties(ImmutableMap.of(DOCKER_HUB_REPO_PROPERTY, "jihocech/JCK"))
      .build();
  public static final App NGINX = App.builder()
      .id(generateId())
      .name("nginx")
      .platform(LINUX_X64)
      .author(OLA)
      .properties(ImmutableMap.of(DOCKER_HUB_REPO_PROPERTY, "nginx", "ports", ""))
      .build();

  // installations
  public static final Installation NGINX_RUNNING_ON_CURARE = Installation.builder()
      .id(generateId())
      .app(NGINX)
      .device(CURARE)
      .build();
  public static final Installation COSSMIC_RUNNING_ON_PEQUOD = Installation.builder()
      .id(generateId())
      .app(COSSMIC)
      .device(PEQUOD)
      .build();
  public static final Installation FRMS_RUNNING_ON_PEQUOD = Installation.builder()
      .id(generateId())
      .app(FRMS)
      .device(PEQUOD)
      .build();
  public static final Installation COSSMIC_RUNNING_ON_BLACK_PEARL = Installation.builder()
      .id(generateId())
      .app(COSSMIC)
      .device(BLACK_PEARL)
      .build();
  public static final Installation COSSMIC_RUNNING_ON_KOBAYASHI_MARU = Installation.builder()
      .id(generateId())
      .app(COSSMIC)
      .device(KOBAYASHI_MARU)
      .build();
  public static final Installation APP_RUNNING_ON_BOCKSCAR = Installation.builder()
      .id(generateId())
      .app(ARDUINO_APP)
      .device(BOCKSCAR)
      .build();
  public static final Installation APP_RUNNING_ON_ENOLA_GAY = Installation.builder()
      .id(generateId())
      .app(ARDUINO_APP)
      .device(ENOLA_GAY)
      .build();

  // COLLECTIONS

  public static final ImmutableList<Platform> platforms = ImmutableList.<Platform>builder()
      .add(RPI)
      .add(ARDUINO)
      .add(LINUX_X64)
      .build();

  public static final ImmutableList<InstallationMethod> installationMethods = ImmutableList
      .<InstallationMethod>builder()
      .add(DOCKER)
      .add(ARDUINO_BT)
      .build();

  public static final ImmutableList<ManagerType> managerTypes = ImmutableList
      .<ManagerType>builder()
      .add(AHAB_RPI)
      .add(AHAB_LINUX_X64)
      .add(MCSS)
      .build();

  public static final ImmutableList<User> users = ImmutableList.<User>builder()
      .add(SIMON)
      .add(OLA)
      .build();

  public static final ImmutableList<App> apps = ImmutableList.<App>builder()
      .add(COSSMIC)
      .add(ARDUINO_APP)
      .add(FRMS)
      .add(NGINX)
      .build();

  public static final ImmutableList<Device> devices = ImmutableList.<Device>builder()
      .add(ENOLA_GAY)
      .add(BOCKSCAR)
      .add(AKUTAN_ZERO)
      .add(PEQUOD)
      .add(AKUTAN_ZERO)
      .add(BLACK_PEARL)
      .add(CURARE)
      .build();

  public static final ImmutableList<Manager> managers = ImmutableList.<Manager>builder()
      .add(AHAB_ON_BLACK_PEARL)
      .add(AHAB_ON_KOBAYASHI_MARU)
      .add(AHAB_ON_PEQUOD)
      .add(SIMONS_MSCC)
      .add(AHAB_ON_CURARE)
      .build();

  public static final ImmutableList<Installation> installations = ImmutableList
      .<Installation>builder()
      .add(COSSMIC_RUNNING_ON_PEQUOD)
      .add(COSSMIC_RUNNING_ON_KOBAYASHI_MARU)
      .add(COSSMIC_RUNNING_ON_BLACK_PEARL)
      .add(APP_RUNNING_ON_BOCKSCAR)
      .add(APP_RUNNING_ON_ENOLA_GAY)
      .add(FRMS_RUNNING_ON_PEQUOD)
      .add(NGINX_RUNNING_ON_CURARE)
      .build();
  
  public static void main(String[] args) {
    System.out.println(UUID.randomUUID());
  }

}
