package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.api.ApiProperties;

@Slf4j
public class Database {

  public static Connection getConnection() throws SQLException {
    Connection result = null;
    try {
      Context initialContext = new InitialContext();
      DataSource datasource = (DataSource) initialContext.lookup(ApiProperties.DATASOURCE);
      if (datasource != null) {
        result = datasource.getConnection();
      }
      else {
        log.info("Failed to lookup datasource.");
      }
    } catch (NamingException ex) {
      log.info("Cannot get connection: " + ex);
    } catch (SQLException ex) {
      log.info("Cannot get connection: " + ex);
    }
    return result;
  }

}
