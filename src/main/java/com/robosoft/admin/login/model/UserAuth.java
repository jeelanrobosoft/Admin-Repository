package com.robosoft.admin.login.model;


import lombok.Data;

@Data
public class UserAuth {
    private String emailId;
    private String password;
    private String role;
}