package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.AdminDao;
import com.robosoft.admin.login.dto.*;
import com.robosoft.admin.login.model.ChangePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    AdminDao adminDao;

    @Value("${page.data.count}")
    private int perPageDataCount;

    public List<Long> getOffsetUsingCustomLimit(int pageNumber, long limit) {
        List<Long> list = new ArrayList<>();

        if (pageNumber < 1)
            pageNumber = 1;

        if (limit < 1)
            limit = perPageDataCount;

        list.add(limit);
        list.add(limit * (pageNumber - 1));
        return list;
    }

    public String changePassword(ChangePassword changePassword) {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            String oldPassword = adminDao.checkPassword(emailId);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(changePassword.getOldPassword(), oldPassword)) {
                if (encoder.matches(changePassword.getNewPassword(), oldPassword))
                    return "Password Should not be same as Old Password";
                adminDao.resetPassword(emailId, new BCryptPasswordEncoder().encode(changePassword.getNewPassword()));
                return "Password Changed Successfully";
            }
            return "Incorrect Password";
        } catch (Exception e) {
            e.printStackTrace();
            return "Incorrect Password.";
        }
    }

    public List<StudentList> getStudentList(int pageNumber, int pageLimit) {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Long> list = this.getOffsetUsingCustomLimit(pageNumber, pageLimit);
        long limit = list.get(0);
        long offset = list.get(1);
        return adminDao.getStudentList(emailId, offset, limit);
    }

    public List<StudentList> getStudentListWithoutPagination() {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminDao.getStudentListWithoutPagination(emailId);
    }

    public List<ChapterListResponse> getChapterList(Integer courseId) {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Integer> chapterIds = adminDao.getChapterId(emailId, courseId);
        List<ChapterListResponse> chapterListResponses = new ArrayList<>();
        for (Integer chapterId : chapterIds) {
            System.out.println(chapterId);
            try {
                adminDao.checkForTest(chapterId);
            } catch (Exception e) {
                chapterListResponses.add(adminDao.getChapterDetails(chapterId));
            }
        }
        return chapterListResponses;
    }

    public String addTest(TestRequest testRequest) {
        try {
            Integer testId = adminDao.addTest(testRequest.getChapterId(), testRequest.getTestName(), testRequest.getTestDuration(), testRequest.getPassingGrade());
            for (QuestionRequest questions : testRequest.getQuestionRequests()) {
                adminDao.addQuestions(questions, testId);
            }
            adminDao.getQuestionCount(testId);
            return "Test Added SuccessFully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }
    
    public String deleteStudent(List<StudentStatusRequest> studentStatusRequests) {
        if (studentStatusRequests.size() > 0) {
            try {
                for (StudentStatusRequest studentList : studentStatusRequests) {
                    adminDao.deleteStudent(studentList);
                }
                return "Student Deleted Successfully";
            } catch (Exception e) {
                return "Failed to Deleted";
            }
        }
        return "Failed to Deleted";
    }

    public String subscribeStudent(StudentStatusRequest studentStatusRequest) {
        try {
            Boolean subscribeStatus = adminDao.getEnrollment(studentStatusRequest);
            if (subscribeStatus)
                adminDao.unsubscribeStudent(studentStatusRequest);
            else
                adminDao.subscribeStudent(studentStatusRequest);
            return "Subscribed Successfully";
        } catch (Exception e) {
            return "Failed to Subscribe";
        }
    }
        
    public DashBoardHeaderResponse getDashBoardHeader() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer totalStudentsEnrolled = adminDao.getTotalStudentsEnrolled(userName);
        Integer totalCoursesAdded = adminDao.getTotalCoursesAdded(userName);
        Integer overallResult = adminDao.getOverallResult(userName);
        return new DashBoardHeaderResponse(totalStudentsEnrolled,totalCoursesAdded,overallResult);

    }
}


