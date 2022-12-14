package com.robosoft.admin.login.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    private String emailId;
    private MultipartFile profilePhoto;
    private String fullName;
    private String mobileNumber;
    private String designation;
    private String description;
    private String url;
    private boolean approvalStatus;

}
