package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Overview {
    private Integer courseId;
    private String courseTagLine;
    private String description;
    private String learningOutCome;
    private String requirements;
    private String instructorId;
    private String difficultyLevel;
}
