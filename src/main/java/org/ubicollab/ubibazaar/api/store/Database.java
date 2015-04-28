package org.ubicollab.ubibazaar.api.store;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Database {

  static ComboPooledDataSource cpds;

  static {
    try {
      Properties prop = new Properties();
      InputStream in = Database.class.getResourceAsStream("/db.properties");
      prop.load(in);
      in.close();

      cpds = new ComboPooledDataSource();
      cpds.setDriverClass("com.mysql.jdbc.Driver");

      cpds.setJdbcUrl(prop.getProperty("jdbc_url"));
      cpds.setUser(prop.getProperty("user"));
      cpds.setPassword(prop.getProperty("password"));

      cpds.setMinPoolSize(5);
      cpds.setAcquireIncrement(5);
      cpds.setMaxPoolSize(20);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() throws SQLException {
    return cpds.getConnection();
  }

}
