package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.AdminDashBoardDetails;
import com.robosoft.admin.login.model.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperAdminApproval {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int addToAuthenticate(Admin admin, String password)
    {
        password =  new BCryptPasswordEncoder().encode(password);
        jdbcTemplate.update("UPDATE admin SET approvalStatus=true WHERE emailId=?",admin.getEmailId());
       return jdbcTemplate.update("insert into authenticate(userName,password,role) values(?,?,?)", admin.getEmailId(), password,"ROLE_ADMIN");
    }

    public Integer deleteFromAuthenticate(Admin admin)
    {
        return jdbcTemplate.update("update admin set rejectStatus = true where emailId=?",admin.getEmailId());

    }

    public List<Register> getDetails() {
        return jdbcTemplate.query("select * from admin where approvalStatus=false and rejectStatus=false",new BeanPropertyRowMapper<>(Register.class));
    }

    public AdminDashBoardDetails getAdminCountAndCoursesAdded() {
        AdminDashBoardDetails details = new AdminDashBoardDetails();
        Integer totalNumberOfAdmins = jdbcTemplate.queryForObject("select count(*) from admin where approvalStatus=true and emailId!='akjeelan22@gmail.com'", Integer.class);
        Integer totalCourses = jdbcTemplate.queryForObject("select count(*) from course", Integer.class);
        return new AdminDashBoardDetails(totalNumberOfAdmins,totalCourses);

    }
}
