package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminApproval {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int addToAuthenticate(Admin admin, String password)
    {
        password =  new BCryptPasswordEncoder().encode(password);
       return jdbcTemplate.update("insert into authenticate(emailId,password,role) values(?,?,?)", admin.getEmailId(), password,"ROLE_ADMIN");
    }

    public Integer deleteFromAuthenticate(Admin admin)
    {
        jdbcTemplate.update("delete from admin where emailId=?",admin.getEmailId());
        return jdbcTemplate.update("delete from authenticate where emailId=?",admin.getEmailId());
    }

    public void updateStatus(String adminId)
    {
       jdbcTemplate.update("UPDATE admin SET approvalStatus=? WHERE emailId=?",true,adminId);
    }
}
