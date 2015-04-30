package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

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
          String properties = new Gson().toJson(getProperties(id));

          return new Installation(id, device, app, properties);
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
          String properties = new Gson().toJson(getProperties(id));

          results.add(new Installation(id, device, app, properties));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Installation create(Installation app) {
    // generate user id
    app.setId(StoreUtil.generateRandomId());

    // insert to all tables
    Installation inserted = insertInstallation(app);
    insertProperties(inserted);

    return inserted;
  }

  private static Installation insertInstallation(Installation installation) {
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

  private static Map<String, String> getProperties(String id) {
    String sql = "SELECT * FROM installation_property WHERE installation_id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        Map<String, String> results = Maps.newHashMap();

        while (rs.next()) {
          results.put(rs.getString("property_name"), rs.getString("property_value"));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static void insertProperties(Installation installation) {
    String sql =
        "INSERT INTO installation_property (installation_id, property_name, property_value) "
            + "VALUES (?,?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      if (installation.getProperties() == null) {
        return;
      }

      Map<?, ?> props = new Gson().fromJson(installation.getProperties(), Map.class);

      for (Object property : props.entrySet()) {
        Entry<?, ?> i = (Entry<?, ?>) property;

        ps.setString(1, installation.getId());
        ps.setString(2, (String) i.getKey());
        ps.setString(3, (String) i.getValue());
        ps.execute();
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
