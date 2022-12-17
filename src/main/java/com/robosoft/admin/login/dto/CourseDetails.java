package com.robosoft.admin.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDetails {
    private Integer CategoryId;
    private String CategoryName;
    private String CategoryPhoto;
    private Integer subCategoryId;
    private String subCategoryName;
    private String courseName;
    private String previewVideo;
    private String courseTagLine;
    private String description;
    private String learningOutCome;
    private String requirements;
    private String difficultyLevel;
    private List<CourseKeywords> keywords;
    private List<ChapterResponse> chapter;

}
