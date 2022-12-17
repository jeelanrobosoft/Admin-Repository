package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.dto.AddCategoryResponse;
import com.robosoft.admin.login.dto.AddCourseRequest;
import com.robosoft.admin.login.dto.AddSubCategoryResponse;
import com.robosoft.admin.login.model.AddCategory;
import com.robosoft.admin.login.model.AddSubCategory;
import com.robosoft.admin.login.model.Category;
import com.robosoft.admin.login.model.SubCategory;
import com.robosoft.admin.login.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.robosoft.admin.login.dto.AddChapterRequest;
import com.robosoft.admin.login.service.OverViewData;
import java.text.ParseException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class AddCourseController {

    @Autowired
    CourseService courseService;
    
     @Autowired
    private OverViewData overViewData;

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(){
        List<Category> categories = courseService.getCategoriesWithoutPagination();

        if ((categories) != null)
            return ResponseEntity.of(Optional.of(categories));
        else {
            return ResponseEntity.of(Optional.of(Collections.singletonMap("message", "No Categories added Yet"))).status(HttpStatus.NOT_FOUND).build();
        }

    }


    @GetMapping("/subCategories")
    public ResponseEntity<?> getAllSubcategoryWithoutPagination() {
        List<SubCategory> subCategories = courseService.getAllSubCategoriesWithoutPagination();

        if ((subCategories) != null)
            return ResponseEntity.of(Optional.of(subCategories));
        else
            return ResponseEntity.of(Optional.of(Collections.singletonMap("message", "No SubCategories added Yet"))).status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/category")
    public ResponseEntity<?> addCategory(@ModelAttribute AddCategory category) throws IOException {
        Integer categoryId;
        String status = courseService.addCategory(category);
        try{
        categoryId = Integer.parseInt(status);
        } catch (NumberFormatException e)
        {
            return new ResponseEntity<>(Collections.singletonMap("Error",status),HttpStatus.NOT_ACCEPTABLE);
        }
        status = "Category " + category.getCategoryName() + " has been Added SuccessFully";
        AddCategoryResponse response = new AddCategoryResponse(categoryId,status);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/subCategory")
    public ResponseEntity<?> addSubCategory(@RequestBody AddSubCategory subCategory) throws IOException {
        Integer subCategoryId;
        String status = courseService.addSubCategory(subCategory);
        try{
            subCategoryId = Integer.parseInt(status);
        } catch (NumberFormatException e){
            return new ResponseEntity<>(Collections.singletonMap("Error",status),HttpStatus.NOT_ACCEPTABLE);
        }
        status = "Sub Category " + subCategory.getSubCategoryName() + " has been Added SuccessFully";
        AddSubCategoryResponse response = new AddSubCategoryResponse(subCategoryId,status);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/overView")
    public ResponseEntity<?> addOverView(@RequestBody AddCourseRequest addCourseRequest) throws IOException {
        String response = overViewData.addCourseOverView(addCourseRequest);
        if(response.equals("Overview added"))
        {
            return new ResponseEntity<>(Collections.singletonMap("message", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(Collections.singletonMap("message", response), HttpStatus.OK);
    }

    @PostMapping("/chapter")
    public ResponseEntity<?> addChapter(@RequestBody AddChapterRequest addCourseRequest) throws ParseException {
          String response = overViewData.addChapter(addCourseRequest);
        System.out.println("response "+response);
          if(response.equals("Chapter Data Added") || response.equals("Chapter Data Updated"))
          {
              return new ResponseEntity<>(Collections.singletonMap("message", response), HttpStatus.OK);
          }
        return new ResponseEntity<>(Collections.singletonMap("message", "Failed"), HttpStatus.NOT_MODIFIED);
    }

    @PutMapping("/publishToWeb")
    public ResponseEntity<?> publishToWeb(@RequestParam Integer courseId)
    {
        String response = overViewData.publishToWeb(courseId);
        return new ResponseEntity<>(Collections.singletonMap("message", response), HttpStatus.OK);
        //return new ResponseEntity<>(Collections.singletonMap("message", "Failed To Publish"), HttpStatus.NOT_MODIFIED);
    }

}

