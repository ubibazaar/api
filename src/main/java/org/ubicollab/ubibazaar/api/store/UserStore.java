package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

import org.mindrot.jbcrypt.BCrypt;
import org.ubicollab.ubibazaar.core.User;

@Slf4j
public class UserStore {

  public static User createUser(User user) {
    // generate user id
    user.setId(StoreUtil.generateRandomId());

    // hash password
    user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

    insertToUserTable(user);
    insertToUserGroupTable(user);
    
    return user;
  }

  private static User insertToUserTable(User user) {
    String sql = "INSERT INTO user (id, name, username, password) VALUES (?,?,?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, user.getId());
      ps.setString(2, user.getName());
      ps.setString(3, user.getUsername());
      ps.setString(4, user.getPassword()); // comes in hashed already
      ps.execute();

      return user;
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }
  
  private static User insertToUserGroupTable(User user) {
    String sql = "INSERT INTO user_group (group_name, user_id) VALUES (?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, "user");
      ps.setString(2, user.getId());
      ps.execute();

      return user;
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static User getUser(String id) {
    String sql = "SELECT * FROM user WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String username = rs.getString("username");
          String name = rs.getString("name");
          String password = rs.getString("password");

          return new User(id, username, name, password);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static User getUserByUsername(String username) {
    String sql = "SELECT * FROM user WHERE username = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String id = rs.getString("id");
          String name = rs.getString("name");
          String password = rs.getString("password");

          return new User(id, username, name, password);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
