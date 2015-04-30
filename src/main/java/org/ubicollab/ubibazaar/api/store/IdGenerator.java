package org.ubicollab.ubibazaar.api.store;

import java.util.UUID;

public class IdGenerator {

  public static final String generateId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

}
