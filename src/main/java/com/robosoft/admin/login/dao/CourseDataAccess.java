package com.robosoft.admin.login.dao;


import com.robosoft.admin.login.model.AddSubCategory;
import com.robosoft.admin.login.model.Category;
import com.robosoft.admin.login.model.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
public class CourseDataAccess {

    @Autowired
    JdbcTemplate jdbcTemplate;
    public List<Integer> getListOfCategoriesIds() {
        return jdbcTemplate.queryForList("SELECT categoryId FROM category", Integer.class);
    }

    public int getCountOfCategoryId(Integer categoryId) {
        return jdbcTemplate.queryForObject("SELECT count(categoryId) FROM course WHERE categoryId = ?", Integer.class, categoryId);
    }

    public Category getCategory(Integer categoryId) {
    return jdbcTemplate.queryForObject("SELECT * FROM category WHERE categoryId = ?", new BeanPropertyRowMapper<>(Category .class), categoryId);
    }


    public List<Integer> getSubCategoryIds() {
        return jdbcTemplate.queryForList("SELECT subCategoryId FROM subCategory ", Integer.class);
    }

    public int getCountOfSubCategory(Integer subCategoryId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(subCategoryId) FROM course WHERE subCategoryId = ?", Integer.class, subCategoryId);
    }

    public SubCategory getSubCategories(Integer subCategoryId) {
        return jdbcTemplate.queryForObject("SELECT * FROM subCategory WHERE subCategoryId = ?", new BeanPropertyRowMapper<>(SubCategory.class), subCategoryId);
    }


    public Integer checkForCategory(String categoryName) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM category WHERE categoryName = ?", Integer.class, categoryName);
    }

    public String addCategory(String categoryName, String categoryPhoto) {
        String query = "INSERT INTO category(categoryName,categoryPhoto) VALUES(?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, categoryName);
            ps.setString(2, categoryPhoto);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().toString();
    }

    public Integer checkForSubCategory(String subCategoryName) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM subCategory WHERE subCategoryName = ?", Integer.class, subCategoryName);
    }

    public String addSubCategory(AddSubCategory subCategory) {
        String query = "INSERT INTO subCategory(categoryId,subCategoryName) VALUES(?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, subCategory.getCategoryId());
            ps.setString(2, subCategory.getSubCategoryName().trim());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().toString();
    }
}
