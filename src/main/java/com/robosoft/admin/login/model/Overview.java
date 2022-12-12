package com.robosoft.admin.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Overview {
    private Integer courseId;
    private String courseTagLine;
    private String description;
    private String learningOutCome;
    private String requirements;
    private Integer instructorId;
    private String difficultyLevel;
    private String coursePhoto;
    private String previewVideo;
}
