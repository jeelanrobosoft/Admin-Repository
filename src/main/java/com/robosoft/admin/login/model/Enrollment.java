package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {
    private String userName;
    private Integer courseId;
    private Date joinDate;
    private Date completedDate;
    private Integer courseScore;
}
