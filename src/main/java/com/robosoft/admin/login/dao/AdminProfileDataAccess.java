package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminProfileDataAccess {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Admin getProfile(String adminId)
    {
         return jdbcTemplate.queryForObject("SELECT fullName,emailId,mobileNumber,profilePhoto FROM admin WHERE emailId=?",new BeanPropertyRowMapper<>(Admin.class),adminId);
    }
}
