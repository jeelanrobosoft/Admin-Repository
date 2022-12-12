package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.dto.StudentList;
import com.robosoft.admin.login.model.ChangePassword;
import com.robosoft.admin.login.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword){
        String s = adminService.changePassword(changePassword);
        if(s != null)
            return new ResponseEntity<>(Collections.singletonMap("message",s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message","Something Went Wrong"),HttpStatus.OK);
    }

    @GetMapping("/studentList")
    public List<StudentList> getStudentList(@RequestParam int pageNumber, @RequestParam int limit) {
        return adminService.getStudentList(pageNumber, limit);
    }
}
