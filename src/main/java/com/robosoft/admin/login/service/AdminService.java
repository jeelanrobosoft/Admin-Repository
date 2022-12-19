package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.AdminDao;
import com.robosoft.admin.login.dto.*;
import com.robosoft.admin.login.model.ChangePassword;
import com.robosoft.admin.login.model.CourseId;
import com.robosoft.admin.login.model.Lesson;
import com.robosoft.admin.login.model.SaveCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.robosoft.admin.login.config.CloudinaryConfig.uploadProfilePhoto;

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


    public List<StudentList> getStudentListWithoutPagination() {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminDao.getStudentListWithoutPagination(emailId);
    }

    public List<ChapterListResponse> getChapterList(Integer courseId) {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Integer> chapterIds = adminDao.getChapterId(emailId, courseId);
        List<ChapterListResponse> chapterListResponses = new ArrayList<>();
        for (Integer chapterId : chapterIds) {
            try {
                chapterListResponses.add(adminDao.getChapterDetails(chapterId));
            } catch (Exception ignored) {
            }
        }
        return chapterListResponses;
    }

    public String addTest(TestRequest testRequest) {
        if (testRequest.getTestId() != null) {
            try {
                editTest(testRequest);
                return "Test Updated SuccessFully";
            } catch (Exception e) {
                return "Failed";
            }

        } else {
            try {
                Integer testId = adminDao.addTest(testRequest.getChapterId(), testRequest.getTestName(), testRequest.getTestDuration(), testRequest.getPassingGrade());
                for (QuestionRequest questions : testRequest.getQuestionRequests()) {
                    adminDao.addQuestions(questions, testId);
                }
                List<String> userNames = adminDao.getEnrolledUserNames(testRequest.getChapterId());
                for (String userName : userNames) {
                    if (!adminDao.hasChapterCompleted(userName, testRequest.getChapterId())) {
                        try {
                            adminDao.addTestForEnrolled(testId, userName, testRequest.getChapterId());
                            return "Test Added SuccessFully";
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Failed";
                        }
                    }

                }
                adminDao.getQuestionCount(testId);
                return "Test Added SuccessFully";
            } catch (Exception e) {
                return "Failed";
            }
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
            if (subscribeStatus) {
                adminDao.unsubscribeStudent(studentStatusRequest);
                return "Unsubscribed Successfully";
            } else {
                adminDao.subscribeStudent(studentStatusRequest);
                return "Subscribed Successfully";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to Subscribe";
        }
    }

    public void editTest(TestRequest testRequest) {
        adminDao.editTest(testRequest.getTestId(), testRequest.getTestName(), testRequest.getTestDuration(), testRequest.getChapterId(), testRequest.getPassingGrade());
        for (QuestionRequest questionRequest : testRequest.getQuestionRequests()) {
            if (questionRequest.getQuestionId() != null) {
                if (questionRequest.isDeleteStatus()) {
                    adminDao.deleteQuestion(questionRequest);
                } else {
                    adminDao.editQuestion(questionRequest);
                }
            }
        }
    }

    public DashBoardHeaderResponse getDashBoardHeader() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer totalStudentsEnrolled = adminDao.getTotalStudentsEnrolled(userName);
        Integer totalCoursesAdded = adminDao.getTotalCoursesAdded(userName);
        Integer overallResult = adminDao.getOverallResult(userName);
        return new DashBoardHeaderResponse(totalStudentsEnrolled, totalCoursesAdded, overallResult);

    }

    public TestRequest getQuestionsAndAnswers(Integer chapterId) {
        try {
            TestRequest testRequest = adminDao.getTestDetails(chapterId);
            List<QuestionRequest> questionRequests = adminDao.getQuestionAndAns(testRequest.getTestId());
            testRequest.setQuestionRequests(questionRequests);
            return testRequest;
        } catch (Exception e) {
            return null;
        }
    }


    public List<CourseResponse> recentlyAddedCourseWithoutPagination() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CourseResponse> courseResponseList = new ArrayList<>();
        List<CourseId> courseIdList = adminDao.recentlyAddedCourseWithoutPagination(userName);
        for (CourseId courseId : courseIdList) {
            new CourseResponse();
            CourseResponse courseResponse;
            if (!courseId.isUploadStatus()) {
                courseResponse = adminDao.GetCourses(userName, courseId.getCourseId());
                courseResponse.setUploadedStatus(false);
                courseResponseList.add(courseResponse);
                continue;
            }
            courseResponse = adminDao.GetCourses(userName, courseId.getCourseId());
            courseResponse.setUploadedStatus(true);
            courseResponseList.add(courseResponse);
        }
        return courseResponseList;
    }

    public List<StudentList> getStudentList(int pageNumber, int pageLimit) {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Long> list = this.getOffsetUsingCustomLimit(pageNumber, pageLimit);
        long limit = list.get(0);
        long offset = list.get(1);
        return adminDao.getStudentList(emailId, offset, limit);
    }

    public List<CourseResponse> getCoursesAdded(int pageNumber, int pageLimit) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Long> list = this.getOffsetUsingCustomLimit(pageNumber, pageLimit);
        List<CourseResponse> courseResponseList = new ArrayList<>();
        long limit = list.get(0);
        long offset = list.get(1);
        List<CourseId> courseIdList = adminDao.recentlyAddedCourseWithPagination(limit, offset, userName);
        for (CourseId courseId : courseIdList) {
            new CourseResponse();
            CourseResponse courseResponse;
            if (!courseId.isUploadStatus()) {
                courseResponse = adminDao.GetCourses(userName, courseId.getCourseId());
                courseResponse.setUploadedStatus(false);
                courseResponseList.add(courseResponse);
                continue;
            }
            courseResponse = adminDao.GetCourses(userName, courseId.getCourseId());
            courseResponse.setUploadedStatus(true);
            courseResponseList.add(courseResponse);
        }
        return courseResponseList;

    }

    public CourseDetails getCourseDetails(int courseId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer status = adminDao.checkForCourseDetails(userName, courseId);
        if (status == 0)
            return null;
        CourseDetails courseDetails = adminDao.getCourseOverview(userName, courseId);
        List<ChapterResponse> chapterList = adminDao.getTotalChapters(courseId);
        for (ChapterResponse chapterResponse : chapterList) {
            List<Lesson> lessonList = adminDao.getLessonDetails(chapterResponse.getChapterId());
            chapterResponse.setLessonList(lessonList);
        }
        List<CourseKeywords> courseKeywords = adminDao.getCourseKeywords(courseId);
        courseDetails.setChapter(chapterList);
        courseDetails.setKeywords(courseKeywords);

        return courseDetails;
    }

    public List<CertificateDetails> getCourseCompletedDetails() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CertificateDetails> certificateDetails = adminDao.getCourseCompletedDetails(userName);
        if (certificateDetails.isEmpty())
            return null;
        certificateDetails = certificateDetails.stream().map(
                student -> {
                    String studentUserName = student.getUserName();
                    String certificateNumber = " Certificate Number: CER57RF9" + studentUserName + "S978" + student.getCourseId();
                    student.setCertificateNo(certificateNumber);
                    return student;
                }
        ).collect(Collectors.toList());
        return certificateDetails;


    }

    public String saveCertificate(SaveCertificate certificate) {
        String certificateUrl;
        boolean status = false;
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CourseId> courseIdList = adminDao.getCourseIds(userName);
        try{
        for (CourseId courseId: courseIdList) {
            if (courseId.getCourseId() == certificate.getCourseId() && certificate.getUserName().equalsIgnoreCase(courseId.getUserName()))
            {
                status = true;
            }
        }
        } catch (Exception e)
        {
            return "Invalid courseId";
        }
        if(certificate.getCertificate() == null)
            return "Certificate cannot be Empty";
        if(certificate.getCertificate().isEmpty())
            return "Certificate cannot be Empty";
        if(certificate.getUserName() == null)
            return "userName cannot be empty";
        if(certificate.getUserName().isEmpty())
            return "userName cannot be empty";
        if(status == true){
            certificateUrl = uploadProfilePhoto(certificate.getCertificate());
            try{
            adminDao.saveCertificate(certificate,certificateUrl);
            return null;
            } catch (Exception e){
                return "Cannot add same data again";
            }
        }
        else
            return "Invalid courseId or userName";
    }
}


