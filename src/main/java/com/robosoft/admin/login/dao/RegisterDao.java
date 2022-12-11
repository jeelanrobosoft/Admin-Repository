package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.model.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RegisterDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void adminRegister(Register register, String url) {
        jdbcTemplate.update("INSERT INTO admin(emailId,profilePhoto,fullName,mobileNumber,designation,description,url,approvalStatus) VALUES(?,?,?,?,?,?,?,?)",register.getEmailId(),url,
                register.getFullName(),register.getMobileNumber(),register.getDesignation(),register.getDescription(),register.getUrl(),false);
    }

    public String getAdminEmailId(String emailId) {
        return jdbcTemplate.queryForObject("SELECT emailId FROM admin WHERE emailId = ?", String.class,emailId);
    }

    public void resetPassword(String emailId, String password) {
        jdbcTemplate.update("UPDATE authenticate SET password = ? WHERE emailId = ?",password,emailId);
    }

}
