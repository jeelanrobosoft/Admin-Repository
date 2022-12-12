package com.robosoft.admin.login.dao;


import com.robosoft.admin.login.model.EmailId;
import com.robosoft.admin.login.model.OtpValidity;
import com.robosoft.admin.login.model.OtpVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginDataAccessLayer {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public Integer checkForEmail(EmailId emailId) {
        String query = "select count(*) from admin where emailId=?";
        return jdbcTemplate.queryForObject(query,Integer.class,emailId.getEmailId());
    }

    public void deletePreviousOtp(EmailId emailId) {
        String query = "update otpVerification set status=false where emailId=? and status=true";
        jdbcTemplate.update(query,emailId.getEmailId());
    }

    public long saveOtp(String generatedTime, String expiryTime, String twoFaCode, String emailId) {
        String query = "insert into otpVerification values(?,?,?,?,true)";
        jdbcTemplate.update(query, emailId, twoFaCode, generatedTime, expiryTime);
        return ((Long.parseLong(expiryTime)) / 60) - ((Long.parseLong(generatedTime)) / 60);
    }


    public String fetchOtpForGivenEmail(OtpVerification verification) {
        String query = "select otp,expiryTime from otpVerification where mobileNumber='" + verification.getEmailId() + "' and status=true";
        OtpValidity otpVerification = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(OtpValidity.class));
        if (otpVerification.getOtp().equals(verification.getOtp()) && ((System.currentTimeMillis() / 1000) <= Long.parseLong(otpVerification.getExpiryTime()))) {
            jdbcTemplate.update("update otpVerification set status=false where mobileNumber='" + verification.getEmailId() + "' and status=true");
            return "Verified";
        }
        return "Verification Fail";

    }

}