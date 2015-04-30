package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Category;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Slf4j
public class AppStore {

  public static App getById(String id) {
    String sql = "SELECT * FROM app WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String name = rs.getString("name");
          String description = rs.getString("description");

          Platform platform = PlatformStore.getPlatform(rs.getString("platform_id"));
          User user = UserStore.getUser(rs.getString("author_id"));

          Set<Category> categories = getCategories(id);
          Map<String, String> properties = getProperties(id);
          
          return new App(id, name, platform, user, description, categories, properties);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Collection<App> getAll() {
    String sql = "SELECT * FROM app";

    try (Connection conn = Database.getConnection();
        Statement ps = conn.createStatement()) {
      ps.execute(sql);

      try (ResultSet rs = ps.getResultSet()) {
        Set<App> results = Sets.newHashSet();

        while (rs.next()) {
          String id = rs.getString("id");
          String name = rs.getString("name");
          String description = rs.getString("description");

          Platform platform = PlatformStore.getPlatform(rs.getString("platform_id"));
          User user = UserStore.getUser(rs.getString("author_id"));

          Set<Category> categories = getCategories(id);
          Map<String, String> properties = getProperties(id);
          
          results.add(new App(id, name, platform, user, description, categories, properties));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static Map<String, String> getProperties(String id) {
    String sql = "SELECT * FROM app_property WHERE app_id = ?";

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

  private static Set<Category> getCategories(String id) {
    String sql = "SELECT * FROM app_category WHERE app_id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        Set<Category> results = Sets.newHashSet();

        while (rs.next()) {
          results.add(CategoryStore.getCategoryAncestry(rs.getString("category_id")));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
