package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashBoardDetails {
    private Integer totalNumberOfAdmins;
    private Integer totalCourses;
    private List<Register> listOfAdmins;

    public AdminDashBoardDetails(Integer totalNumberOfAdmins, Integer totalCourses) {
        this.totalNumberOfAdmins = totalNumberOfAdmins;
        this.totalCourses = totalCourses;
    }
}
