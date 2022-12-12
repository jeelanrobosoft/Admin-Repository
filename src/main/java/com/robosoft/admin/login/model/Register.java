package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Register {
    private String emailId;
    private MultipartFile profilePhoto;
    private String fullName;
    private String mobileNumber;
    private String designation;
    private String description;
    private String url;

    public Register(String emailId, String fullName, String mobileNumber, String designation, String description, String url) {
        this.emailId = emailId;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.designation = designation;
        this.description = description;
        this.url = url;
    }
}
