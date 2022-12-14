package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Register {
    private String emailId;
    private String profilePhoto;
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
