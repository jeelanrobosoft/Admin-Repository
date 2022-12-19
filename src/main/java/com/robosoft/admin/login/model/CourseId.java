package com.robosoft.admin.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseId {
    private Integer courseId;
    private boolean uploadStatus;
    private String userName;
}
