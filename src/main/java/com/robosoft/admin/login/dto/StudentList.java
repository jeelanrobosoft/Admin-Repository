package com.robosoft.admin.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentList {
    private String profilePhoto;
    private String userName;
    private String fullName;
    private String joinDate;
    private String courseName;
    private String completedDate;
    private Boolean courseCompletedStatus;
    private Boolean subscribeStatus;
}
