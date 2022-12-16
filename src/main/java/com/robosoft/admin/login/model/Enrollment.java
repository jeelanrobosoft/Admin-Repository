
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
    private String courseId;
    private Date joinDate;
    private Date completedDate;
    private Boolean subscribeStatus;
    private Boolean deleteStatus;
    private Integer courseScore;
}

