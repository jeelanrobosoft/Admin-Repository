package com.robosoft.admin.login.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerification {
    private String emailId;
    private String otp;
}