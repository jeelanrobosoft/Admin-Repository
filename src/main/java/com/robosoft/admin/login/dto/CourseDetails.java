package com.robosoft.admin.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.robosoft.admin.login.model.Chapter;
import com.robosoft.admin.login.model.Lesson;
import com.robosoft.admin.login.model.Overview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDetails {
    private String courseName;
    private Integer CategoryId;
    private String CategoryName;
    private String CategoryPhoto;
    private Integer subCategoryId;
    private String subCategoryName;
    private Overview overview;
    private Chapter chapter;
    private Lesson lesson;


}
