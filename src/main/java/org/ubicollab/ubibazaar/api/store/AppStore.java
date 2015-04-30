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

  public static App create(App app) {
    //FIXME properties are coming in empty :(
    
    // generate user id
    app.setId(IdGenerator.generateId());

    // insert to all tables
    App insertedApp = insertApp(app);
    insertCategories(insertedApp);
    insertProperties(insertedApp);

    return insertedApp;
  }

  public static App update(App app) {
    //FIXME properties are coming in empty :(
    
    // insert to all tables
    updateApp(app);

    deleteCategories(app.getId());
    insertCategories(app);

    deleteProperties(app.getId());
    insertProperties(app);

    return app;
  }

  public static void delete(String appId) {
    // delete from all tables
    deleteProperties(appId);
    deleteCategories(appId);
    deleteApp(appId);
  }

  private static App insertApp(App app) {
    String sql = "INSERT INTO app (id, name, description, platform_id, author_id) "
        + "VALUES (?,?,?,?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, app.getId());
      ps.setString(2, app.getName());
      ps.setString(3, app.getDescription());
      ps.setString(4, app.getPlatform().getId());
      ps.setString(5, app.getAuthor().getId());
      ps.execute();

      return app;
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static void updateApp(App app) {
    String sql = "UPDATE app set name = ?, description = ?, platform_id = ? "
        + "WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, app.getName());
      ps.setString(2, app.getDescription());
      ps.setString(3, app.getPlatform().getId());
      ps.setString(4, app.getId());
      ps.execute();
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

  private static void insertCategories(App app) {
    String sql = "INSERT INTO app_category (app_id, category_id) VALUES (?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      for (Category category : app.getCategory()) {

        ps.setString(1, app.getId());
        ps.setString(2, category.getId());
        ps.execute();
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

  private static void insertProperties(App app) {
    String sql = "INSERT INTO app_property (app_id, property_name, property_value) VALUES (?,?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      if (app.getProperties() == null) {
        return;
      }

      for (Entry<String, String> property : app.getProperties().entrySet()) {
        ps.setString(1, app.getId());
        ps.setString(2, property.getKey());
        ps.setString(3, property.getValue());
        ps.execute();
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static void deleteApp(String appId) {
    String sql = "DELETE FROM app WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, appId);
      ps.execute();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static void deleteCategories(String appId) {
    String sql = "DELETE FROM app_category WHERE app_id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, appId);
      ps.execute();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static void deleteProperties(String appId) {
    String sql = "DELETE FROM app_property WHERE app_id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, appId);
      ps.execute();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
