package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.core.Category;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

@Slf4j
public class CategoryStore {

  public static final Category getCategoryAncestry(String categoryId) {
    // load data from db
    Table<String, String, String> categoryTable = getCategoryTable();

    String name = categoryTable.get(categoryId, "name");
    Category leaf = new Category(categoryId, name, null, null);
    Category category = leaf;

    while (true) {
      // find parent
      String parentId = categoryTable.get(category.getId(), "parent_id");
      String parentName = categoryTable.get(parentId, "name");
      Category parent = new Category(parentId, parentName, null, null);

      // if we found the root, break
      if (parentId.equals(category.getId())) {
        break;
      }

      // link parent
      category.setParent(parent);

      // go level up
      category = parent;
    }

    return leaf;
  }

  public static final Category getCategoryTree() {
    return getCategorySubTree(null);
  }

  public static final Category getCategorySubTree(String categoryId) {
    // load data from db
    Table<String, String, String> table = getCategoryTable();

    // special case, looking for root
    // find a row where id is the same as parent id
    if (categoryId == null) {
      categoryId = table.rowMap().entrySet().stream()
          .filter(row -> row.getKey().equals(row.getValue().get("parent_id")))
          .map(row -> row.getKey())
          .findFirst().get();
    }

    // find name of tree root and construct the root
    String name = table.get(categoryId, "name");
    Category category = new Category(categoryId, name, null, null);

    // link children, going down each branch depth-first
    linkChildren(category, table);

    return category;
  }

  private static final void linkChildren(Category parent, Table<String, String, String> table) {
    // find children categories
    Set<String> childrenIds = table.rowMap().entrySet().stream()
        .filter(row -> row.getValue().get("parent_id").equals(parent.getId()))
        .filter(row -> !row.getKey().equals(row.getValue().get("parent_id")))
        .map(row -> row.getKey())
        .collect(Collectors.toSet());

    // go in depth-first and link
    for (String childId : childrenIds) {
      String name = table.get(childId, "name");
      Category child = new Category(childId, name, null, null);

      parent.addChild(child);

      linkChildren(child, table);
    }
  }

  private static final Table<String, String, String> getCategoryTable() {
    try (Connection conn = Database.getConnection(); Statement ps = conn.createStatement()) {
      ps.execute("SELECT id, name, parent_id FROM category");

      try (ResultSet rs = ps.getResultSet()) {
        Table<String, String, String> results = HashBasedTable.create();

        while (rs.next()) {
          String id = rs.getString("id");

          String parentId = rs.getString("parent_id");
          results.put(id, "parent_id", parentId);

          String name = rs.getString("name");
          results.put(id, "name", name);
        }

        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Database problem. See logs for details.", e);
    }
  }

}
