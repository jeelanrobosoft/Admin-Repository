package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCourseRequest {

    private String courseName;
    private String categoryName;
    private String subCategoryName;
    private String courseTagLine;
    private String description;
    private String learningOutCome;
    private String requirements;
    private String difficultyLevel;
    private String coursePhoto;
    private String previewVideo;
    private String courseKeyword;
    private Integer courseId;
}
