package com.robosoft.admin.login.service;


import com.robosoft.admin.login.dao.CourseDataAccess;
import com.robosoft.admin.login.model.AddCategory;
import com.robosoft.admin.login.model.AddSubCategory;
import com.robosoft.admin.login.model.Category;
import com.robosoft.admin.login.model.SubCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.robosoft.admin.login.config.CloudinaryConfig.uploadProfilePhoto;

@Service
public class CourseService {


    @Autowired
    CourseDataAccess courseDataAccess;

    Logger logger = LoggerFactory.getLogger(CourseService.class);


    public List<Category> getCategoriesWithoutPagination() {
        List<Integer> categoriesIds = courseDataAccess.getListOfCategoriesIds();
        List<Category> categories = new ArrayList<>();
        for (Integer categoryId : categoriesIds) {
            int categoryCount = courseDataAccess.getCountOfCategoryId(categoryId);
            if (categoryCount != 0)
                categories.add(courseDataAccess.getCategory(categoryId));
        }
        return categories;
    }

    public List<SubCategory> getAllSubCategoriesWithoutPagination() {
        List<Integer> subCategoryIds = courseDataAccess.getSubCategoryIds();
        List<SubCategory> subCategories = new ArrayList<>();
        for (Integer subCategoryId : subCategoryIds) {
            int subCategoryCount = courseDataAccess.getCountOfSubCategory(subCategoryId);
            if (subCategoryCount != 0)
                subCategories.add(courseDataAccess.getSubCategories(subCategoryId));
        }
        return subCategories;
    }

    public String addCategory(AddCategory category) {
        Integer status = checkStringContainsNumberOrNot(category.getCategoryName());
        if (status == -1)
            return "category name cannot be empty";
        if (status == 1)
            return "Category should not contain digits";
        String categoryPhoto = null;
        String categoryName = category.getCategoryName().trim();
        try {
            category.getCategoryPhoto();
            if (category.getCategoryPhoto().getContentType().startsWith("video"))
                return "Cannot upload videos";
            categoryPhoto = uploadProfilePhoto(category.getCategoryPhoto());
        } catch (Exception e) {
        }
        status = courseDataAccess.checkForCategory(categoryName);
        if (status > 0)
            return "Category Already Present";
        return courseDataAccess.addCategory(categoryName, categoryPhoto);

    }

    public String addSubCategory(AddSubCategory subCategory) {
        List<Integer> categoriesIds = courseDataAccess.getListOfCategoriesIds();
        if (subCategory.getSubCategoryName().length() < 5)
            return "Sub Category name cannot be empty";
        Integer status = checkStringContainsNumberOrNot(subCategory.getSubCategoryName());
        if (status == 1)
            return "Sub Category cannot contain digits";

        if (categoriesIds.contains(subCategory.getCategoryId())) {
            status = courseDataAccess.checkForSubCategory(subCategory.getSubCategoryName().trim());
            if (status > 0)
                return "Sub Category already present";
            return courseDataAccess.addSubCategory(subCategory);

        }
        return "Invalid Category Id";
    }
    public static int  checkStringContainsNumberOrNot(String s) {
        if (s == null)
            return -1;
        else if (s.isEmpty())
            return -1;
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                return 1;
            }
        }
        return 0;
    }
}
