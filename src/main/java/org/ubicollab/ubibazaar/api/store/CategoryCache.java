package org.ubicollab.ubibazaar.api.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.ubicollab.ubibazaar.core.Category;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;

// FIXME revoke relation cache if new category was added or one removed..

@Slf4j
public class CategoryCache {

  private static final LoadingCache<String, String> relationCache = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(new RelationLoader());

  private static final LoadingCache<String, String> nameCache = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(new NameLoader());


  public static Category getCategoryTree(String categoryId) {
    // special case, looking for root
    if (categoryId == null) {
      if(relationCache.asMap().isEmpty()) {
        try {
          relationCache.get("fakekey");
        } catch (Exception e) {
          // do nothing, this is expected to fail
        }
      }
      
      categoryId = relationCache.asMap().entrySet().stream()
          .filter(entry -> entry.getKey().equals(entry.getValue()))
          .findFirst().get().getKey();
    }

    // find name of tree root and construct the root
    String name = nameCache.getUnchecked(categoryId);
    Category treeRoot = new Category(categoryId, name, null, null);

    // link subcategories, going down each branch depth-first
    linkSubCategories(treeRoot);

    return treeRoot;
  }

  private static void linkSubCategories(Category superCategory) {
    // find subcategories
    Set<String> subCategoriesIds = relationCache.asMap().entrySet().stream()
        .filter(entry -> entry.getValue().equals(superCategory.getId())) // with this parent
        .filter(entry -> !entry.getKey().equals(entry.getValue())) // skip root, parent of itself
        .map(entry -> entry.getKey())
        .collect(Collectors.toSet());

    // go in depth-first and link
    for (String subCategoryId : subCategoriesIds) {
      String name = nameCache.getUnchecked(subCategoryId);
      Category subCategory = new Category(subCategoryId, name, null, null);

      superCategory.addSubCategory(subCategory);

      linkSubCategories(subCategory);
    }
  }

  public static Category getCategoryAncestry(String categoryId) {
    String name = nameCache.getUnchecked(categoryId);
    Category leaf = new Category(categoryId, name, null, null);
    Category category = leaf;

    while (true) {
      // find parent
      String superCategoryId = relationCache.getUnchecked(category.getId());
      String superCategoryName = nameCache.getUnchecked(superCategoryId);
      Category superCategory = new Category(superCategoryId, superCategoryName, null, null);

      // if we found the root, break
      if (superCategoryId.equals(category.getId())) {
        break;
      }

      // link supercategory
      category.setSuperCategory(superCategory);

      // go level up
      category = superCategory;
    }

    return leaf;
  }

  private static final class NameLoader extends CacheLoader<String, String> {
    public String load(String key) throws Exception {
      log.warn("Missing key {} in name cache, reloading");

      Connection connection = Database.getConnection();
      Statement ps = connection.createStatement();
      ps.execute("SELECT id, name FROM category");
      ResultSet rs = ps.getResultSet();

      String result = null;

      while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");

        nameCache.put(id, name);

        if (id.equals(key)) {
          result = name;
        }
      }

      if (result == null) {
        throw new IllegalArgumentException("Category " + key + " does nt exist.");
      }

      return result;
    }
  }

  private static final class RelationLoader extends CacheLoader<String, String> {
    public String load(String key) throws Exception {
      log.warn("reloading relations");

      Connection connection = Database.getConnection();
      Statement ps = connection.createStatement();
      ps.execute("SELECT id, parent_id FROM category");
      ResultSet rs = ps.getResultSet();

      String result = null;

      while (rs.next()) {
        String id = rs.getString("id");
        String parentId = rs.getString("parent_id");
        relationCache.put(id, parentId);

        if (id.equals(key)) {
          result = parentId;
        }
      }

      if (result == null) {
        throw new IllegalArgumentException("Category " + key + " does not exist.");
      }

      return result;
    }
  }

  public static void main(String[] args) throws SQLException, ExecutionException {
    log.info("category ancestry of energy-monitoring {}",
        new Gson().toJson(CategoryCache.getCategoryAncestry("f47754d0c689443ebc1bbf0cd2c8bd86")));
    log.info("category tree of root {}",
        new Gson().toJson(CategoryCache.getCategoryTree("4220f24cd59f4ef6b2b07f74a1e264bd")));
  }

}
