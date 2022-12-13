package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {
    private String userName;
    private String courseId;
    private Boolean subscribeStatus;
    private Boolean deleteStatus;
}
