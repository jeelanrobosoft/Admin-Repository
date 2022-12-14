package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.dto.ChapterListResponse;
import com.robosoft.admin.login.dto.StudentList;
import com.robosoft.admin.login.dto.StudentStatusRequest;
import com.robosoft.admin.login.dto.TestRequest;
import com.robosoft.admin.login.model.ChangePassword;
import com.robosoft.admin.login.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class AdminController {

    @Autowired
    AdminService adminService;

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
        String s = adminService.changePassword(changePassword);
        if (s != null)
            return new ResponseEntity<>(Collections.singletonMap("message", s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message", "Something Went Wrong"), HttpStatus.OK);
    }

    @GetMapping("/studentList")
    public ResponseEntity<?> getStudentList(@RequestParam int pageNumber, @RequestParam int limit) {
        try {
            List<StudentList> studentLists = adminService.getStudentList(pageNumber, limit);
            if (studentLists.isEmpty())
                return new ResponseEntity<>(Collections.singletonMap("message", "null"), HttpStatus.OK);
            return new ResponseEntity<>(studentLists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("Error", "Something Went Wrong"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/studentListWithoutPage")
    public ResponseEntity<?> getStudentListWithoutPagination() {
        try {
            List<StudentList> studentLists = adminService.getStudentListWithoutPagination();
            if (studentLists.isEmpty())
                return new ResponseEntity<>(Collections.singletonMap("message", "null"), HttpStatus.OK);
            return new ResponseEntity<>(studentLists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("Error", "Something Went Wrong"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/chapterList")
    public ResponseEntity<?> getChapterList(@RequestParam Integer courseId) {
        try {
            List<ChapterListResponse> chapterList = adminService.getChapterList(courseId);
            if (chapterList.isEmpty())
                return new ResponseEntity<>(Collections.singletonMap("message", "null"), HttpStatus.OK);
            return new ResponseEntity<>(chapterList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("Error", "Something Went Wrong"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addTest")
    public ResponseEntity<?> addTest(@RequestBody TestRequest testRequest) {
        String s = adminService.addTest(testRequest);
        if (s != null)
            return new ResponseEntity<>(Collections.singletonMap("message", s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message", "Something Went Wrong"), HttpStatus.OK);
    }

    @DeleteMapping("/deleteStudent")
    public ResponseEntity<?> deleteStudent(@RequestBody List<StudentStatusRequest> studentStatusRequests) {
        String s = adminService.deleteStudent(studentStatusRequests);
        if (s != null)
            return new ResponseEntity<>(Collections.singletonMap("message", s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message", "Something Went Wrong"), HttpStatus.OK);
    }

    @PutMapping("/subscribe")
    public ResponseEntity<?> subscribeStudent(@RequestBody StudentStatusRequest studentStatusRequest) {
        String s = adminService.subscribeStudent(studentStatusRequest);
        if (s != null)
            return new ResponseEntity<>(Collections.singletonMap("message", s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message", "Something Went Wrong"), HttpStatus.OK);
    }

    @GetMapping("/dashBoard/header")
    public ResponseEntity<?> getDashBoardHeader() {
        return new ResponseEntity<>(adminService.getDashBoardHeader(), HttpStatus.OK);
    }
}
