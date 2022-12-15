package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private String courseId;
    private String courseName;
    private String coursePhoto;
    private String previewVideo;
    private String uploadedDate;
    private boolean uploadedStatus;
}
