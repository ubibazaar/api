package org.ubicollab.ubibazaar.api.store;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Database {

  public static final String DATASOURCE_CONTEXT;

  static {
    String loaded = null;

    try {
      Properties prop = new Properties();
      InputStream in = Database.class.getResourceAsStream("/db.properties");
      prop.load(in);
      in.close();

      loaded = prop.getProperty("datasource_jndi_name");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DATASOURCE_CONTEXT = loaded;
    }
  }

  public static Connection getConnection() throws SQLException {
    Connection result = null;
    try {
      Context initialContext = new InitialContext();
      DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
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
