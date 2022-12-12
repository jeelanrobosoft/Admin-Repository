package com.robosoft.admin.login.model;


import lombok.Data;

@Data
public class UserAuth {
    private String userName;
    private String password;
    private String role;
}