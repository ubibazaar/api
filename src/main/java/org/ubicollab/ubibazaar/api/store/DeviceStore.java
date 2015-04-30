package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.collect.Sets;

@Slf4j
public class DeviceStore {

  public static Device getById(String id) {
    String sql = "SELECT * FROM device WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String name = rs.getString("name");

          Platform platform = PlatformStore.getPlatform(rs.getString("platform_id"));
          User owner = UserStore.getUser(rs.getString("owner_id"));

          return new Device(id, name, platform, owner);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Collection<Device> getAll() {
    String sql = "SELECT * FROM device";

    try (Connection conn = Database.getConnection();
        Statement ps = conn.createStatement()) {
      ps.execute(sql);

      try (ResultSet rs = ps.getResultSet()) {
        Set<Device> results = Sets.newHashSet();

        while (rs.next()) {
          String id = rs.getString("id");
          String name = rs.getString("name");

          Platform platform = PlatformStore.getPlatform(rs.getString("platform_id"));
          User owner = UserStore.getUser(rs.getString("owner_id"));

          results.add(new Device(id, name, platform, owner));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Device create(Device device) {
    // generate user id
    device.setId(StoreUtil.generateRandomId());

    String sql = "INSERT INTO device (id, name, platform_id, owner_id) VALUES (?,?,?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, device.getId());
      ps.setString(2, device.getName());
      ps.setString(3, device.getPlatform().getId());
      ps.setString(4, device.getOwner().getId());
      ps.execute();

      return device;
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  // FIXME should be returning?
  public static Device update(Device device) {
    String sql = "UPDATE device set name = ?, platform_id = ? WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, device.getName());
      ps.setString(2, device.getPlatform().getId());
      ps.setString(3, device.getId());
      ps.execute();

      return device;
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static void delete(String deviceId) {
    String sql = "DELETE FROM device WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, deviceId);
      ps.execute();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
