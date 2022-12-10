package com.robosoft.admin.login.service;


import com.robosoft.admin.login.dao.LoginDataAccessLayer;
import com.robosoft.admin.login.model.EmailId;
import com.robosoft.admin.login.model.OtpVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LoginService {

    @Autowired
    LoginDataAccessLayer dataAccessLayer;


    @Autowired
    JavaMailSender javaMailSender;

    public String checkForEmail(EmailId emailId) {
        Integer status = dataAccessLayer.checkForEmail(emailId);
        if(status == 1)
            return null;
        return "Email Id Doesn't exist";
    }

    public void deletePreviousOtp(EmailId emailId) {
        dataAccessLayer.deletePreviousOtp(emailId);
    }

    public long sendOtp(EmailId emailId) {
        String twoFaCode = String.valueOf(new Random().nextInt(8999) + 1000);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("virtuallearn2022@gmail.com");
        email.setTo(emailId.getEmailId());
        email.setSubject("OTP");
        email.setText("OTP to verify the account : " + twoFaCode);
        javaMailSender.send(email);
        String generatedTime = String.valueOf(System.currentTimeMillis() / 1000);
        String expiryTime = String.valueOf((System.currentTimeMillis() / 1000) + 120);
        return dataAccessLayer.saveOtp(generatedTime,expiryTime,twoFaCode,emailId.getEmailId());
    }

//    public String verifyOtp(OtpVerification verification) {
//        return dataAccessLayer.fetchOtpForGivenEmail(verification);
//    }
}