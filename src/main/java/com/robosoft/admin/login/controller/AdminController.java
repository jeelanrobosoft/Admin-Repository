package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.dto.*;
import com.robosoft.admin.login.model.ChangePassword;
import com.robosoft.admin.login.model.SaveCertificate;
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

    @GetMapping("/QuestionAndAns")
    public ResponseEntity<?> getQuestionsAndAnswers(@RequestParam Integer chapterId)
    {
        TestRequest testRequest = adminService.getQuestionsAndAnswers(chapterId);
        if(testRequest != null)
            return new ResponseEntity<>(testRequest,HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message","null"),HttpStatus.OK);
    }

    @GetMapping("/coursesAddedWP")
    public ResponseEntity<?> getRecentlyAddedCourseWP(){
        List<CourseResponse> responses = adminService.recentlyAddedCourseWithoutPagination();
        if(responses.isEmpty())
            return new ResponseEntity<>(Collections.singletonMap("message","No course is present"),HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responses,HttpStatus.OK);
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

    @GetMapping("/coursesAdded")
    public ResponseEntity<?> getRecentlyAddedCourse(@RequestParam int pageNumber, @RequestParam int limit){
        List<CourseResponse> responses = adminService.getCoursesAdded(pageNumber,limit);
        if(responses.isEmpty())
            return new ResponseEntity<>(Collections.singletonMap("message","No course is present"),HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @GetMapping("/courseDetails")
    public ResponseEntity<?> getCourseDetails(@RequestParam int courseId){
        CourseDetails courseDetails =  adminService.getCourseDetails(courseId);
        if(courseDetails == null)
            return new ResponseEntity<>(Collections.singletonMap("message","Course ID not present"), HttpStatus.NOT_ACCEPTABLE);
        return new ResponseEntity<>(courseDetails,HttpStatus.OK);
    }

    @GetMapping("/course/completed")
    public ResponseEntity<?> getCourseCompletedStudents(){
        List<CertificateDetails> certificateDetails = adminService.getCourseCompletedDetails();
        if(certificateDetails == null)
            return new ResponseEntity<>(Collections.singletonMap("message","No Student has completed courses"),HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(certificateDetails,HttpStatus.OK);
    }

//    @PostMapping("/course/certificate/save")
//    public ResponseEntity<?> saveCertificateDetails(@RequestBody SaveCertificate certificate){
//        String status = adminService.saveCertificate(certificate.getCertificateUrl());
//
//    }

}
