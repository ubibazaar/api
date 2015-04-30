package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.core.InstallationMethod;

import com.google.common.collect.Sets;

@Slf4j
public class InstallationMethodStore {

  public static InstallationMethod getById(String id) {
    String sql = "SELECT * FROM installation_method WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String name = rs.getString("name");

          return new InstallationMethod(id, name, getProperties(id));
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Collection<InstallationMethod> getPlatforms() {
    String sql = "SELECT * FROM installation_method";

    try (Connection conn = Database.getConnection();
        Statement ps = conn.createStatement()) {
      ps.execute(sql);

      try (ResultSet rs = ps.getResultSet()) {
        Set<InstallationMethod> results = Sets.newHashSet();

        while (rs.next()) {
          String id = rs.getString("id");
          String name = rs.getString("name");

          results.add(new InstallationMethod(id, name, getProperties(id)));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static Set<String> getProperties(String id) {
    String sql = "SELECT * FROM installation_method_property WHERE installation_method_id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        Set<String> results = Sets.newHashSet();

        while (rs.next()) {
          results.add(rs.getString("property_name"));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
