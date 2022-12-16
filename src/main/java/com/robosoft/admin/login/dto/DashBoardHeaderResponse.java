package com.robosoft.admin.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashBoardHeaderResponse {
    private Integer totalStudentsEnrolled;
    private Integer totalCoursesAdded;
    private Integer overallResult;
}
