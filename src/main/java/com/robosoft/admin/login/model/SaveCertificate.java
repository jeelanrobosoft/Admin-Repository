package com.robosoft.admin.login.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveCertificate {
    private Integer courseId;
    private String certificateUrl;
}
