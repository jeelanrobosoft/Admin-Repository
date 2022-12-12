package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.dto.ChapterListResponse;
import com.robosoft.admin.login.dto.QuestionRequest;
import com.robosoft.admin.login.dto.StudentList;
import com.robosoft.admin.login.model.Enrollment;
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

    public List<StudentList> getStudentListWithoutPagination(String emailId) {
        List<StudentList> studentLists = new ArrayList<>();
        List<Enrollment> enrollments = jdbcTemplate.query("SELECT * from enrollment INNER JOIN course ON course.courseId = enrollment.courseId AND course.adminId = ?", new BeanPropertyRowMapper<>(Enrollment.class),emailId);
        for(Enrollment enrollment : enrollments) {
            StudentList studentList = jdbcTemplate.queryForObject("SELECT profilePhoto,user.userName,fullName,joinDate,courseName,completedDate,courseCompletedStatus FROM enrollment INNER JOIN user ON enrollment.userName = user.userName INNER JOIN course ON course.courseId = enrollment.courseId INNER JOIN courseProgress ON courseProgress.userName = enrollment.userName AND courseProgress.courseId = enrollment.courseId AND enrollment.courseId = ? and enrollment.userName = ?", new BeanPropertyRowMapper<>(StudentList.class),enrollment.getCourseId(),enrollment.getUserName());
            studentLists.add(studentList);
        }
        return studentLists;
    }


    public List<Integer> getChapterId(String adminId, Integer courseId) {
        return jdbcTemplate.queryForList("SELECT chapterId FROM chapter INNER JOIN course ON chapter.courseId = course.courseId WHERE chapter.courseId = ? AND course.adminId = ?", Integer.class,courseId,adminId);
    }

    public void checkForTest(Integer chapterId) {
        jdbcTemplate.queryForObject("SELECT testId FROM test WHERE chapterId = ?", Integer.class, chapterId);
    }

    public ChapterListResponse getChapterDetails(Integer chapterId) {
        return jdbcTemplate.queryForObject("SELECT chapterId,chapterName FROM chapter WHERE chapterId = ?",new BeanPropertyRowMapper<>(ChapterListResponse.class),chapterId);
    }

    public Integer addTest(Integer chapterId, String testName, String testDuration, Integer passingGrade) {
        String query = "INSERT INTO test(testName,chapterId,testDuration,passingGrade) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,testName);
            ps.setInt(2, chapterId);
            ps.setString(3, testDuration);
            ps.setInt(4, passingGrade);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void addQuestions(QuestionRequest questions, Integer testId) {
        jdbcTemplate.update("INSERT INTO question(questionName,testId,option_1,option_2,option_3,option_4,correctAnswer) VALUES(?,?,?,?,?,?,?)",questions.getQuestionName(),testId,questions.getOption_1(),questions.getOption_2(),questions.getOption_3(),questions.getOption_4(),questions.getCorrectAnswer());
    }

    public void getQuestionCount(Integer testId)
    {
        Integer testCount = jdbcTemplate.queryForObject("SELECT COUNT(questionId) FROM questions WHERE testId = ?", Integer.class,testId);
        jdbcTemplate.update("UPDATE test SET questionCount = ? WHERE testId = ?",testCount,testId);
    }
}
