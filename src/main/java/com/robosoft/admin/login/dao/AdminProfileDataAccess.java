package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminProfileDataAccess {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Profile getProfile(String adminId)
    {

        return jdbcTemplate.queryForObject("SELECT fullName,emailId,mobileNumber,profilePhoto FROM admin WHERE emailId=?",new BeanPropertyRowMapper<>(Profile.class),adminId);

    }

    public void saveDetails(String userName,String fullName, String profilePhoto, String mobileNumber) {
        if(fullName != null)
            jdbcTemplate.update("update admin set fullName=? where emailId=?",fullName,userName);
        if(profilePhoto != null)
            jdbcTemplate.update("update admin set profilePhoto=? where emailId=?",profilePhoto,userName);
        if(mobileNumber != null)
            jdbcTemplate.update("update admin set mobileNumber=? where emailId=?",mobileNumber,userName);
    }
}
