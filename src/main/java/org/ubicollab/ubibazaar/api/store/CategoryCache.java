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

// FIXME possible race conditions.. check

@Slf4j
public class CategoryCache {

  private static final LoadingCache<String, String> relationCache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(10, TimeUnit.MINUTES)
      // .removalListener(null) // FIXME listen and reload
      .build(new RelationLoader());

  // FIXME optimzie
  private static final LoadingCache<String, Category> cache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(10, TimeUnit.MINUTES)
      // .removalListener(null) // FIXME listen and reload
      .build(new CategoryLoader());


  public static Category getCategoryTree(String categoryId) {
    // special case, looking for root
    if (categoryId == null) {
      categoryId = relationCache.asMap().entrySet().stream()
          .filter(entry -> entry.getKey().equals(entry.getValue()))
          .findFirst().get().getKey();
    }

    // find tree root and link subcategories, going down each branch depth-first
    Category treeRoot = cache.getUnchecked(categoryId);
    linkSubCategories(treeRoot);

    return treeRoot;
  }

  // FIXME terminology
  private static void linkSubCategories(Category superCategory) {
    relationCache.asMap().entrySet().stream()
        .filter(entry -> entry.getValue().equals(superCategory.getId())) // with this parent
        .filter(entry -> !entry.getKey().equals(entry.getValue())) // not themself, if root
        .forEach(x -> log.info("{} has child {}", superCategory.getName(), x.getKey()));
    // .map(entry -> cache.getUnchecked(entry.getKey()))
    // .collect(Collectors.toSet());

    Set<Category> subCategories = relationCache.asMap().entrySet().stream()
        .filter(entry -> entry.getValue().equals(superCategory.getId())) // with this parent
        .filter(entry -> !entry.getKey().equals(entry.getValue())) // not themself, if root
        .map(entry -> cache.getUnchecked(entry.getKey()))
        .collect(Collectors.toSet());

    for (Category subCategory : subCategories) {
      log.info("{} has sub category {}", superCategory.getName(), subCategory.getName());
      superCategory.addSubCategory(subCategory);
      // subCategory.setSuperCategory(superCategory);

      linkSubCategories(subCategory);
    }
  }

  public static Category getCategoryAncestry(String categoryId) {
    Category leaf = cache.getUnchecked(categoryId);
    Category category = leaf;

    while (true) {
      // find parent
      String superCategoryId = relationCache.getUnchecked(category.getId());
      Category superCategory = cache.getUnchecked(superCategoryId);
      
      log.info("{} has parent {} {}", category.getId(), superCategory.getId(), superCategoryId);

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

  private static final class CategoryLoader extends CacheLoader<String, Category> {
    public Category load(String key) throws Exception {
      log.warn("Missing key {} in category cache, reloading");

      Connection connection = Database.getConnection();
      Statement ps = connection.createStatement();
      ps.execute("SELECT id, name FROM category");
      ResultSet rs = ps.getResultSet();

      Category result = null;

      while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");
        Category category = new Category(id, name, null, null);

        cache.put(id, category);

        if (id.equals(key)) {
          result = category;
        }
      }

      if (result == null) {
        throw new IllegalArgumentException("Category " + key + " has no parent.");
      }

      return result;
    }

    // FIXME load all
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
        throw new IllegalArgumentException("Category " + key + " has no parent.");
      }

      return result;
    }

    // FIXME load all
  }

  public static void main(String[] args) throws SQLException, ExecutionException {

    // Connection connection = Database.getConnection();
    //
    // PreparedStatement ps = connection.prepareStatement("SELECT * FROM category");
    //
    // ps.execute();
    //
    // ResultSet rs = ps.getResultSet();
    //
    // while (rs.next()) {
    // log.info("Found category {} with id {} and parent {}", rs.getString("name"),
    // rs.getString("id"), rs.getString("parent_id"));
    // }
    // log.info("parent of {} is {}", "f47754d0c689443ebc1bbf0cd2c8bd86",
    // cc.relationCache.getUnchecked("f47754d0c689443ebc1bbf0cd2c8bd86"));
    //
    // log.info(cc.relationCache.asMap().toString());
    //
    // Category categoryTree = cc.getCategoryTree(null);
    // log.info(categoryTree.getName());
    //
    // log.info(new Gson().toJson(categoryTree));

    CategoryCache.relationCache.getUnchecked("f47754d0c689443ebc1bbf0cd2c8bd86");
    log.info(CategoryCache.relationCache.asMap().toString());
    log.info(new Gson().toJson(CategoryCache.getCategoryAncestry("f47754d0c689443ebc1bbf0cd2c8bd86")));
    log.info(new Gson().toJson(CategoryCache.getCategoryTree("f47754d0c689443ebc1bbf0cd2c8bd86")));

  }

}
