package com.robosoft.admin.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateDetails {
    private String fullName;
    private String userName;
    private Integer courseId;
    private String courseName;
    private String joinDate;
    private String CompletedDate;
    private String courseDuration;
    private Integer courseScore;
    private String certificateNo;
}
