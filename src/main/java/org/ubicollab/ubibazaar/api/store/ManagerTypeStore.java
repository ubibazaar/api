package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

import org.ubicollab.ubibazaar.core.Cardinality;
import org.ubicollab.ubibazaar.core.InstallationMethod;
import org.ubicollab.ubibazaar.core.ManagerType;
import org.ubicollab.ubibazaar.core.Platform;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManagerTypeStore {

  public static ManagerType getById(String id) {
    String sql = "SELECT * FROM manager_type WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String name = rs.getString("name");
          Platform platform = PlatformStore.getPlatform(rs.getString("platform_id"));
          InstallationMethod installationMethod = InstallationMethodStore.getById(rs.getString("installation_method_id"));
          Cardinality devicePairingCardinality = Cardinality.valueOf(rs.getString("cardinality"));

          return new ManagerType(id, name, platform, installationMethod, devicePairingCardinality);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Collection<ManagerType> getManagerTypes() {
    String sql = "SELECT * FROM manager_type";

    try (Connection conn = Database.getConnection();
        Statement ps = conn.createStatement()) {
      ps.execute(sql);

      try (ResultSet rs = ps.getResultSet()) {
        Set<ManagerType> results = Sets.newHashSet();

        while (rs.next()) {
          String id = rs.getString("id");
          String name = rs.getString("name");
          Platform platform = PlatformStore.getPlatform(rs.getString("platform_id"));
          InstallationMethod installationMethod = InstallationMethodStore.getById(rs.getString("installation_method_id"));
          Cardinality devicePairingCardinality = Cardinality.valueOf(rs.getString("cardinality"));

          results.add(new ManagerType(id, name, platform, installationMethod, devicePairingCardinality));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
