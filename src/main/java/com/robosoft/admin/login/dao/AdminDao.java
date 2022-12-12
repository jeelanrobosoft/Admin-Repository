package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.dto.StudentList;
import com.robosoft.admin.login.model.Enrollment;
import com.robosoft.admin.login.model.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDao {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public void adminRegister(Register register, String url) {
        jdbcTemplate.update("INSERT INTO admin(emailId,profilePhoto,fullName,mobileNumber,designation,description,url,approvalStatus) VALUES(?,?,?,?,?,?,?,?)",register.getEmailId(),url,
                register.getFullName(),register.getMobileNumber(),register.getDesignation(),register.getDescription(),register.getUrl(),false);
    }

    public String getAdminEmailId(String emailId) {
        return jdbcTemplate.queryForObject("SELECT emailId FROM admin WHERE emailId = ?", String.class,emailId);
    }

    public void resetPassword(String emailId, String password) {
        jdbcTemplate.update("UPDATE authenticate SET password = ? WHERE userName = ?", password, emailId);
    }

    public String checkPassword(String emailId) {
        return jdbcTemplate.queryForObject("SELECT password FROM authenticate WHERE userName = ?",String.class,emailId);
    }

    public List<StudentList> getStudentList(String emailId, long pageNumber, long pageLimit) {
        List<StudentList> studentLists = new ArrayList<>();
        List<Enrollment> enrollments = jdbcTemplate.query("SELECT * from enrollment INNER JOIN course ON course.courseId = enrollment.courseId AND course.adminId = ? limit ?,?", new BeanPropertyRowMapper<>(Enrollment.class),emailId,pageNumber,pageLimit);
        for(Enrollment enrollment : enrollments) {
            StudentList studentList = jdbcTemplate.queryForObject("SELECT profilePhoto,user.userName,fullName,joinDate,courseName,completedDate,courseCompletedStatus FROM enrollment INNER JOIN user ON enrollment.userName = user.userName INNER JOIN course ON course.courseId = enrollment.courseId INNER JOIN courseProgress ON courseProgress.userName = enrollment.userName AND courseProgress.courseId = enrollment.courseId AND enrollment.courseId = ? and enrollment.userName = ?", new BeanPropertyRowMapper<>(StudentList.class),enrollment.getCourseId(),enrollment.getUserName());
            studentLists.add(studentList);
        }
        return studentLists;
    }
}
