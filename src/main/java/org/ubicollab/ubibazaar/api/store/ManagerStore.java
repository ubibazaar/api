package org.ubicollab.ubibazaar.api.store;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.api.ApiProperties;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Manager;
import org.ubicollab.ubibazaar.core.ManagerType;
import org.ubicollab.ubibazaar.core.User;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

@Slf4j
public class ManagerStore {

  public static Manager getById(String id) {
    String sql = "SELECT * FROM manager WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String name = rs.getString("name");

          ManagerType managerType = ManagerTypeStore.getById(rs.getString("manager_type_id"));
          User owner = UserStore.getUser(rs.getString("owner_id"));
          Set<Device> devices = findManagedDevices(id);

          return new Manager(id, name, managerType, owner, devices, null);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  private static Set<Device> findManagedDevices(String managerId) {
    String sql =
        ""
            + "select d.id "
            + "from ubibazaar.device d "
            + "join ubibazaar.manager m on m.owner_id = d.owner_id "
            + "join ubibazaar.manager_type mt on mt.id = m.manager_type_id and mt.platform_id = d.platform_id "
            + "left join ubibazaar.managed_device md on md.device_id = d.id and md.manager_id = m.id "
            + "where ((md.device_id is not null) or (mt.cardinality = 'ALL')) "
            + "and m.id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, managerId);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        Set<Device> results = Sets.newHashSet();

        while (rs.next()) {
          String id = rs.getString("id");
          results.add(DeviceStore.getById(id));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Collection<Manager> getAll() {
    String sql = "SELECT * FROM manager";

    try (Connection conn = Database.getConnection();
        Statement ps = conn.createStatement()) {
      ps.execute(sql);

      try (ResultSet rs = ps.getResultSet()) {
        Set<Manager> results = Sets.newHashSet();

        while (rs.next()) {
          String id = rs.getString("id");
          String name = rs.getString("name");

          ManagerType managerType = ManagerTypeStore.getById(rs.getString("manager_type_id"));
          User owner = UserStore.getUser(rs.getString("owner_id"));

          Set<Device> devices = findManagedDevices(id);

          results.add(new Manager(id, name, managerType, owner, devices, null));
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static Manager create(Manager manager) {
    // generate user id
    manager.setId(StoreUtil.generateRandomId());

    String sql = "INSERT INTO manager (id, name, platform_id, manager_type_id, owner_id) "
        + "VALUES (?,?,?,?,?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, manager.getId());
      ps.setString(2, manager.getName());
      ps.setString(4, manager.getType().getId());
      ps.setString(4, manager.getOwner().getId());
      ps.execute();

      return manager;
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static void update(Manager manager) {
    String sql = "UPDATE manager set name = ? WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, manager.getName());
      ps.setString(2, manager.getId());
      ps.execute();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static void delete(String managerId) {
    String sql = "DELETE FROM manager WHERE id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, managerId);
      ps.execute();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

  public static void setInstallationInstructions(Manager found) {
    String sql = ""
        + "select * "
        + "from manager m "
        + "join manager_type mt on mt.id = m.manager_type_id "
        + "where m.installed = 0 and m.id = ?";

    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, found.getId());
      ps.execute();


      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          String id = rs.getString("id");
          String key = rs.getString("key");
          String instructions = rs.getString("installation_instructions");
          String url = rs.getString("installation_url");

          MustacheFactory mf = new DefaultMustacheFactory();

          if (Strings.isNullOrEmpty(url)) {
            found.setInstallationInstructions(instructions);
          } else {
            // prepare url
            StringWriter urlWriter = new StringWriter();
            mf.compile(new StringReader(url), "url")
                .execute(urlWriter, ImmutableMap.builder()
                    .put("id", id)
                    .put("key", key)
                    .put("server", ApiProperties.API_URL)
                    .build())
                .flush();

            // shorten url
            String shortUrl = StoreUtil.shortenUrl(urlWriter.toString());

            // prepare instructions
            StringWriter instructionsWriter = new StringWriter();
            mf.compile(new StringReader(instructions), "instructions")
                .execute(instructionsWriter, ImmutableMap.builder()
                    .put("url", shortUrl)
                    .build())
                .flush();

            found.setInstallationInstructions(instructionsWriter.toString());
          }
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
