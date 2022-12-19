package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.dto.*;
import com.robosoft.admin.login.model.CourseId;
import com.robosoft.admin.login.model.Enrollment;
import com.robosoft.admin.login.model.Lesson;
import com.robosoft.admin.login.model.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminDao {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public void adminRegister(Register register, String url) {
        System.out.println(url);
        jdbcTemplate.update("INSERT INTO admin(emailId,profilePhoto,fullName,mobileNumber,designation,description,url,approvalStatus) VALUES(?,?,?,?,?,?,?,?)", register.getEmailId(), url,
                register.getFullName(), register.getMobileNumber(), register.getDesignation(), register.getDescription(), register.getUrl(), false);
    }

    public String getAdminEmailId(String emailId) {
        return jdbcTemplate.queryForObject("SELECT emailId FROM admin WHERE emailId = ?", String.class, emailId);
    }

    public void resetPassword(String emailId, String password) {
        jdbcTemplate.update("UPDATE authenticate SET password = ? WHERE userName = ?", password, emailId);
    }

    public String checkPassword(String emailId) {
        return jdbcTemplate.queryForObject("SELECT password FROM authenticate WHERE userName = ?", String.class, emailId);
    }

    public List<StudentList> getStudentList(String emailId, long pageNumber, long pageLimit) {
        List<StudentList> studentLists = new ArrayList<>();
        List<Enrollment> enrollments = jdbcTemplate.query("SELECT userName,enrollment.courseId,deleteStatus,subscribeStatus from enrollment INNER JOIN course ON course.courseId = enrollment.courseId AND course.adminId = ? AND ((subscribeStatus = true AND deleteStatus = true) OR deleteStatus = false) limit ?,? ", new BeanPropertyRowMapper<>(Enrollment.class),emailId,pageNumber,pageLimit);
        for (Enrollment enrollment : enrollments) {
            StudentList studentList = new StudentList();
            try {
                if (enrollment.getSubscribeStatus()) {
                    studentList = jdbcTemplate.queryForObject("SELECT profilePhoto,user.userName,fullName,joinDate,enrollment.courseId,courseName,completedDate,courseCompletedStatus,enrollment.subscribeStatus FROM enrollment INNER JOIN user ON enrollment.userName = user.userName INNER JOIN course ON course.courseId = enrollment.courseId INNER JOIN courseProgress ON courseProgress.userName = enrollment.userName AND courseProgress.courseId = enrollment.courseId AND enrollment.courseId = ? and enrollment.userName = ?", new BeanPropertyRowMapper<>(StudentList.class), enrollment.getCourseId(), enrollment.getUserName());
                    studentLists.add(studentList);
                } else {
                    studentList = jdbcTemplate.queryForObject("SELECT profilePhoto,user.userName,fullName,joinDate,enrollment.courseId,courseName,completedDate,courseCompletedStatus,enrollment.subscribeStatus FROM enrollment INNER JOIN user ON enrollment.userName = user.userName INNER JOIN course ON course.courseId = enrollment.courseId INNER JOIN courseProgress ON courseProgress.userName = enrollment.userName AND courseProgress.courseId = enrollment.courseId AND enrollment.courseId = ? and enrollment.userName = ? and enrollment.deleteStatus = false", new BeanPropertyRowMapper<>(StudentList.class), enrollment.getCourseId(), enrollment.getUserName());
                    studentLists.add(studentList);
                }
            } catch (Exception e) {
            }
        }
        return studentLists;
    }

    public List<StudentList> getStudentListWithoutPagination(String emailId) {
        List<StudentList> studentLists = new ArrayList<>();
        List<Enrollment> enrollments = jdbcTemplate.query("SELECT * from enrollment INNER JOIN course ON course.courseId = enrollment.courseId AND course.adminId = ?", new BeanPropertyRowMapper<>(Enrollment.class), emailId);
        for (Enrollment enrollment : enrollments) {
            StudentList studentList = jdbcTemplate.queryForObject("SELECT profilePhoto,user.userName,fullName,joinDate,courseName,completedDate,courseCompletedStatus FROM enrollment INNER JOIN user ON enrollment.userName = user.userName INNER JOIN course ON course.courseId = enrollment.courseId INNER JOIN courseProgress ON courseProgress.userName = enrollment.userName AND courseProgress.courseId = enrollment.courseId AND enrollment.courseId = ? and enrollment.userName = ?", new BeanPropertyRowMapper<>(StudentList.class), enrollment.getCourseId(), enrollment.getUserName());
            studentLists.add(studentList);
        }
        return studentLists;
    }


    public List<Integer> getChapterId(String adminId, Integer courseId) {
        return jdbcTemplate.queryForList("SELECT chapterId FROM chapter INNER JOIN course ON chapter.courseId = course.courseId WHERE chapter.courseId = ? AND course.adminId = ?", Integer.class, courseId, adminId);
    }

    public void checkForTest(Integer chapterId) {
        jdbcTemplate.queryForObject("SELECT testId FROM test WHERE chapterId = ?", Integer.class, chapterId);
    }

    public ChapterListResponse getChapterDetails(Integer chapterId) {
        return jdbcTemplate.queryForObject("SELECT chapterId,chapterName FROM chapter WHERE chapterId = ?", new BeanPropertyRowMapper<>(ChapterListResponse.class), chapterId);
    }

    public Integer addTest(Integer chapterId, String testName, String testDuration, Integer passingGrade) {
        String query = "INSERT INTO test(testName,chapterId,testDuration,passingGrade) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, testName);
            ps.setInt(2, chapterId);
            ps.setString(3, testDuration);
            ps.setInt(4, passingGrade);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public void addQuestions(QuestionRequest questions, Integer testId) {
        jdbcTemplate.update("INSERT INTO question(questionName,testId,option_1,option_2,option_3,option_4,correctAnswer) VALUES(?,?,?,?,?,?,?)", questions.getQuestionName(), testId, questions.getOption_1(), questions.getOption_2(), questions.getOption_3(), questions.getOption_4(), questions.getCorrectAnswer());
    }

    public void getQuestionCount(Integer testId) {
        Integer testCount = jdbcTemplate.queryForObject("SELECT COUNT(questionId) FROM question WHERE testId = ?", Integer.class, testId);
        jdbcTemplate.update("UPDATE test SET questionsCount = ? WHERE testId = ?", testCount, testId);
    }

    public void deleteStudent(StudentStatusRequest studentList) {
        jdbcTemplate.update("UPDATE enrollment SET deleteStatus = true WHERE userName = ? AND courseId = ?", studentList.getUserName(), studentList.getCourseId());
    }

    public void subscribeStudent(StudentStatusRequest studentStatusRequest) {
        jdbcTemplate.update("UPDATE enrollment SET subscribeStatus = true WHERE userName = ? AND courseId = ?", studentStatusRequest.getUserName(), studentStatusRequest.getCourseId());

    }

    public Boolean getEnrollment(StudentStatusRequest studentStatusRequest) {
        return jdbcTemplate.queryForObject("SELECT subscribeStatus from enrollment WHERE userName = ? AND courseId = ?", Boolean.class, studentStatusRequest.getUserName(), studentStatusRequest.getCourseId());
    }

    public void unsubscribeStudent(StudentStatusRequest studentStatusRequest) {
        jdbcTemplate.update("UPDATE enrollment SET subscribeStatus = false WHERE userName = ? AND courseId = ?", studentStatusRequest.getUserName(), studentStatusRequest.getCourseId());

    }

    public Boolean getAdminApprovalStatus(String emailId) {
        return jdbcTemplate.queryForObject("SELECT approvalStatus FROM admin WHERE emailId = ?", Boolean.class, emailId);
    }

    public void updateRegistration(Register register, String url) {
        jdbcTemplate.update("UPDATE admin SET profilePhoto = ?,fullName = ?,mobileNumber = ?,designation = ?,description = ?,url = ?,rejectStatus=false WHERE emailId = ?", url, register.getFullName(),
                register.getMobileNumber(), register.getDesignation(), register.getDescription(), register.getUrl(), register.getEmailId());
    }

    public Integer getTotalStudentsEnrolled(String userName) {
        return jdbcTemplate.queryForObject("select count(enrollment.courseId) from course inner join enrollment on course.courseId=enrollment.courseId where adminId=? and ((subscribeStatus = true AND deleteStatus = true) OR deleteStatus = false) ", Integer.class, userName);
    }

    public Integer getTotalCoursesAdded(String userName) {
        return jdbcTemplate.queryForObject("select count(*) from course where adminId=? and publishstatus=true", Integer.class, userName);
    }

    public Integer getOverallResult(String userName) {
        return jdbcTemplate.queryForObject("select avg(courseScore) from course inner join enrollment on course.courseId=enrollment.courseId where adminId=? and deleteStatus=false", Integer.class, userName);
    }

    public TestRequest getTestDetails(Integer chapterId) {
        return jdbcTemplate.queryForObject("SELECT testId,testName,chapterId,testDuration,passingGrade FROM test WHERE chapterId = ?", new BeanPropertyRowMapper<>(TestRequest.class), chapterId);
    }

    public List<QuestionRequest> getQuestionAndAns(Integer testId) {
        return jdbcTemplate.query("SELECT questionId,questionName,option_1,option_2,option_3,option_4,correctAnswer FROM question WHERE testId = ?", new BeanPropertyRowMapper<>(QuestionRequest.class), testId);
    }

    public void editTest(Integer testId, String testName, String testDuration, Integer chapterId, Integer passingGrade) {
        jdbcTemplate.update("UPDATE test SET testName = ?,testDuration = ?,chapterId = ?, passingGrade = ? WHERE testId = ?", testName, testDuration, chapterId, passingGrade, testId);
    }

    public void editQuestion(QuestionRequest questionRequest) {
        jdbcTemplate.update("UPDATE question SET questionName = ?,option_1 = ?,option_2 = ?, option_3 = ?,option_4 = ?, correctAnswer = ? WHERE questionId = ?",
                questionRequest.getQuestionName(), questionRequest.getOption_1(), questionRequest.getOption_2(), questionRequest.getOption_3(), questionRequest.getOption_4(), questionRequest.getCorrectAnswer(), questionRequest.getQuestionId());
    }


    public List<CourseId> recentlyAddedCourseWithoutPagination(String userName) {
        return jdbcTemplate.query("select distinct(chapter.courseId),uploadStatus from chapter inner join course on course.courseId=chapter.courseId where adminId=?", new BeanPropertyRowMapper<>(CourseId.class), userName);
    }

    public CourseResponse GetCourses(String userName, Integer courseId) {
        return jdbcTemplate.queryForObject("select courseId,courseName,coursePhoto,previewVideo,uploadedDate from course where adminId=? and courseId=?", new BeanPropertyRowMapper<>(CourseResponse.class), userName, courseId);
    }

    public List<CourseId> recentlyAddedCourseWithPagination(long limit, long offset, String userName) {
        return jdbcTemplate.query("select distinct(chapter.courseId),uploadStatus from chapter inner join course on course.courseId=chapter.courseId where adminId=? limit ?,?", new BeanPropertyRowMapper<>(CourseId.class), userName, offset, limit);
    }

    public void deleteQuestion(QuestionRequest questionRequest) {
        jdbcTemplate.update("DELETE FROM question WHERE questionId = ?", questionRequest.getQuestionId());
    }

    public List<String> getEnrolledUserNames(Integer chapterId) {
        return jdbcTemplate.queryForList("SELECT DISTINCT userName FROM chapterProgress WHERE chapterId = ?", String.class, chapterId);
    }

    public boolean hasChapterCompleted(String userName, Integer chapterId) {
        return jdbcTemplate.queryForObject("SELECT chapterCompletedStatus FROM chapterProgress WHERE userName = ? AND chapterId = ?", Boolean.class, userName, chapterId);
    }

    public void addTestForEnrolled(Integer testId, String userName, Integer chapterId) {
        jdbcTemplate.update("UPDATE chapterProgress SET testId = ? WHERE userName = ? AND chapterId = ?", userName, chapterId);
    }

    public Integer checkForCourseDetails(String userName, int courseId) {
        return jdbcTemplate.queryForObject("select count(*) from course where courseId=? and adminId=?", Integer.class, courseId, userName);
    }

    public CourseDetails getCourseOverview(String userName, int courseId) {
        return jdbcTemplate.queryForObject("select course.categoryId,categoryName,categoryPhoto,subCategory.subCategoryId,subCategoryName,courseName,coursePhoto,previewVideo," +
                "courseTagLine,description,learningOutCome,requirements,difficultyLevel from course " +
                "inner join category on course.categoryId=category.categoryId inner join subCategory on course.subCategoryId" +
                "=subCategory.subCategoryId inner join overView on course.courseId=overView.courseId where course.courseId=? and adminId=?", new BeanPropertyRowMapper<>(CourseDetails.class), courseId, userName);

    }

    public List<ChapterResponse> getTotalChapters(int courseId) {
        return jdbcTemplate.query("select chapterId,chapterName,uploadStatus from chapter where courseId=?", new BeanPropertyRowMapper<>(ChapterResponse.class), courseId);

    }

    public List<Lesson> getLessonDetails(String chapterId) {
        return jdbcTemplate.query("select lessonId,lessonName,videoLink from lesson where chapterId=?", new BeanPropertyRowMapper<>(Lesson.class), chapterId);
    }

    public List<CourseKeywords> getCourseKeywords(int courseId) {
        return jdbcTemplate.query("select keyword from courseKeywords where courseId=?", new BeanPropertyRowMapper<>(CourseKeywords.class), courseId);
    }

    public List<CertificateDetails> getCourseCompletedDetails(String userName) {
        String query = "select fullName,enrollment.userName,enrollment.courseId,courseName,joinDate,completedDate,courseDuration,courseScore" +
                " from course inner join enrollment on course.courseId=enrollment.courseId " +
                "  inner join user on (course.courseId=enrollment.courseId and enrollment.userName = user.userName)" +
                " where adminId=? and deleteStatus=false and\n" +
                " completedDate > '0000-00-00' and courseScore >= 0";
        return jdbcTemplate.query(query,new BeanPropertyRowMapper<>(CertificateDetails.class),userName);

    }

//    public void saveCertificate(String certificateUrl) {
//        String query = ""
//    }
}
