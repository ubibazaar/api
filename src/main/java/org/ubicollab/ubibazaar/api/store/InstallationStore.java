package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;

import com.google.common.collect.Sets;

@Slf4j
public class InstallationStore {

  public static Installation getById(String id) {
    String sql = "SELECT * FROM installation WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          App app = AppStore.getById(rs.getString("app_id"));
          Device device = DeviceStore.getById(rs.getString("device_id"));

          // FIXME load manager feedback

          return new Installation(id, device, app, null);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Collection<Installation> getAll() {
    String sql = "SELECT * FROM installation";

    try (Connection conn = Database.getConnection();
        Statement ps = conn.createStatement()) {
      ps.execute(sql);

      try (ResultSet rs = ps.getResultSet()) {
        Set<Installation> results = Sets.newHashSet();

        while (rs.next()) {
          String id = rs.getString("id");
          App app = AppStore.getById(rs.getString("app_id"));
          Device device = DeviceStore.getById(rs.getString("device_id"));

          // FIXME load manager feedback

          results.add(new Installation(id, device, app, null));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Installation create(Installation installation) {
    // generate user id
    installation.setId(StoreUtil.generateRandomId());

    String sql = "INSERT INTO installation (id, app_id, device_id) VALUES (?,?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, installation.getId());
      ps.setString(2, installation.getApp().getId());
      ps.setString(3, installation.getDevice().getId());
      ps.execute();

      return installation;
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static void delete(String installationId) {
    String sql = "DELETE FROM installation WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, installationId);
      ps.execute();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
