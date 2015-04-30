package org.ubicollab.ubibazaar.api.store;

import java.util.UUID;

public class StoreUtil {

  public static final String generateRandomId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

}
