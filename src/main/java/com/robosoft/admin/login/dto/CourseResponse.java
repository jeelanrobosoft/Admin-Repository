package com.robosoft.admin.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {
    private String courseId;
    private String courseName;
    private String coursePhoto;
    private String previewVideo;
    private String uploadedDate;
    private boolean uploadedStatus;
}
