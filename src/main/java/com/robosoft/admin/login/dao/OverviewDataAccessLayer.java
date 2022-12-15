package com.robosoft.admin.login.dao;


import com.robosoft.admin.login.dto.AddCourseRequest;
import com.robosoft.admin.login.dto.ChapterDataRequest;
import com.robosoft.admin.login.dto.LessonDataRequest;
import com.robosoft.admin.login.model.Chapter;
import com.robosoft.admin.login.model.CourseKeywords;
import com.robosoft.admin.login.model.Enrollment;
import com.robosoft.admin.login.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class OverviewDataAccessLayer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer getCategoryId(String categoryName)
    {
        return  jdbcTemplate.queryForObject("SELECT categoryId FROM category WHERE categoryName=?",Integer.class,categoryName);
    }

    public Integer getSubCategoryId(String subCategoryName)
    {
        return  jdbcTemplate.queryForObject("SELECT subCategoryId FROM subCategory WHERE subCategoryName=?",Integer.class,subCategoryName);
    }
    public String addCourseKeywords(String keyword, Integer courseId)
    {
        boolean status = true;
        String keywords[] = keyword.split(",");
        List<CourseKeywords> keywordsList = jdbcTemplate.query("SELECT * FROM courseKeywords",new BeanPropertyRowMapper<>(CourseKeywords.class));
        for(String keywordData: keywords)
        {
            if(keywordData.matches(".*\\d.*"))
            {
                return "Invalid Keyword";
            }
            status = true;
            for(CourseKeywords courseKeyword: keywordsList)
            {
                if(courseKeyword.getKeyword().equalsIgnoreCase(keywordData))
                {

                      status=false;
                      break;
                }
            }
            if(status == true)
            {
                jdbcTemplate.update("INSERT INTO courseKeywords(courseId,keyword) values(?,?)",courseId,keywordData);
            }
        }
        return "keyword added";
    }

    public String updateCourseKeywords(String keyword, Integer courseId)
    {
        boolean status = true;
        String keywords[] = keyword.split(",");
        List<CourseKeywords> keywordsList = jdbcTemplate.query("SELECT * FROM courseKeywords",new BeanPropertyRowMapper<>(CourseKeywords.class));
        System.out.println(keywordsList);
        for(String keywordData: keywords)
        {
            if(keywordData.matches(".*\\d.*"))
            {
                return "Invalid Keyword";
            }
            status = true;
            jdbcTemplate.update("DELETE FROM courseKeywords WHERE courseId = ?", courseId);
            System.out.println("deleted");
            for(CourseKeywords courseKeyword: keywordsList)
            {
                if(courseKeyword.getKeyword().equalsIgnoreCase(keywordData))
                {

                    status=false;
                    break;
                }
            }
            if(status == true)
            {
                jdbcTemplate.update("INSERT INTO courseKeywords(courseId,keyword) values(?,?)",courseId,keywordData);
            }
        }
        return "keyword added";
    }
    public String addCourse(AddCourseRequest addCourseRequest, String adminId)
    {
        Integer categoryId = getCategoryId(addCourseRequest.getCategoryName());
        Integer subCategoryId = getSubCategoryId(addCourseRequest.getSubCategoryName());
        String query ="INSERT INTO course(adminId,courseName,categoryId,subCategoryId) values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, adminId);
            ps.setString(2, addCourseRequest.getCourseName());
            ps.setInt(3,categoryId);
            ps.setInt(4,subCategoryId);
            return ps;
        }, keyHolder);
        String courseId = keyHolder.getKey().toString();
        addCourseKeywords(addCourseRequest.getCourseKeyword(), Integer.parseInt(courseId));
        updateUploadDate(addCourseRequest.getCourseId());
        return keyHolder.getKey().toString();
    }

    public String updateCourse(AddCourseRequest addCourseRequest)
    {
        Integer categoryId = getCategoryId(addCourseRequest.getCategoryName());
        Integer subCategoryId = getSubCategoryId(addCourseRequest.getSubCategoryName());
        jdbcTemplate.update("UPDATE course SET courseName = ? ,categoryId = ?, subCategoryId = ? WHERE courseId = ?",addCourseRequest.getCourseName(),categoryId,subCategoryId,addCourseRequest.getCourseId());
        updateCourseKeywords(addCourseRequest.getCourseKeyword(), addCourseRequest.getCourseId());
        updateUploadDate(addCourseRequest.getCourseId());
        return  "Course Updated";
    }
    public void  addOverView(AddCourseRequest addCourseRequest, Integer courseId) throws IOException {
         String instructorId = SecurityContextHolder.getContext().getAuthentication().getName();
         //instructorId="akjeelan22@gmail.com";  //REMOVE THIS AFTER INTEGRATING WITH AUTHENTICATION CODE
//        String coursePhoto = CloudinaryConfig.uploadProfilePhoto(addCourseRequest.getCoursePhoto());
//        String previewVideo = CloudinaryConfig.uploadVideo(addCourseRequest.getPreviewVideo());
         jdbcTemplate.update("UPDATE course SET coursePhoto = ?, previewVideo=? WHERE courseId=?",addCourseRequest.getCoursePhoto(),addCourseRequest.getPreviewVideo(),courseId);
         jdbcTemplate.update("INSERT INTO overView(courseId,courseTagLine,description,learningOutCome,requirements,instructorId,difficultyLevel) values(?,?,?,?,?,?,?)",courseId, addCourseRequest.getCourseTagLine(), addCourseRequest.getDescription(), addCourseRequest.getLearningOutCome(), addCourseRequest.getRequirements(), instructorId,addCourseRequest.getDifficultyLevel());
         updateUploadDate(courseId);
    }

    public void updateOverView(AddCourseRequest addCourseRequest)
    {
        jdbcTemplate.update("UPDATE course SET coursePhoto = ?, previewVideo=? WHERE courseId=?",addCourseRequest.getCoursePhoto(),addCourseRequest.getPreviewVideo(),addCourseRequest.getCourseId());
        jdbcTemplate.update("UPDATE overView SET courseTagLine=?,description=?, learningOutCome=?, requirements=?,difficultyLevel=? WHERE courseId = ?",addCourseRequest.getCourseTagLine(),addCourseRequest.getDescription(),addCourseRequest.getLearningOutCome(),addCourseRequest.getRequirements(), addCourseRequest.getDifficultyLevel(), addCourseRequest.getCourseId());
        updateUploadDate(addCourseRequest.getCourseId());
    }

    public boolean updateUploadStatus(Integer chapterId)
    {
        List<Lesson> lessons = jdbcTemplate.query("SELECT * FROM lesson WHERE chapterId = ?",new BeanPropertyRowMapper<>(Lesson.class),chapterId);
        for(Lesson lesson: lessons)
        {
            if(lesson.getVideoLink() == null)
            {
                 return false;
            }
        }
        return true;
    }

    public void updateUploadDate(Integer courseId)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update("UPDATE course SET uploadedDate = ? WHERE courseId = ?",now,courseId);
    }
    public void addChapter(ChapterDataRequest chapterDataRequest,Integer courseId, Integer chapterNumber) throws ParseException {
        System.out.println("inside add chapter "+courseId);
        String query ="INSERT INTO chapter(courseId,chapterNumber,chapterName) values(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String chapterName = chapterDataRequest.getChapterName();
        System.out.println("course Id "+courseId);
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, courseId);
            ps.setInt(2,chapterNumber);
            ps.setString(3,chapterName);
            return ps;
        }, keyHolder);
        Integer chapterId = Integer.parseInt(keyHolder.getKey().toString());
        addLesson(chapterDataRequest,chapterId);
        updateUploadStatus(chapterId);
        updateUploadDate(courseId);
    }


public void updateChapter(ChapterDataRequest chapterDataRequest,Integer courseId) throws ParseException {
   jdbcTemplate.update("UPDATE chapter SET chapterName = ? WHERE courseId = ? and chapterId = ?",chapterDataRequest.getChapterName(),courseId,chapterDataRequest.getChapterId());
   updateLesson(chapterDataRequest,chapterDataRequest.getChapterId());
    updateUploadStatus(chapterDataRequest.getChapterId());
    updateUploadDate(courseId);
}
    public Integer getChapterId(Integer courseId, String chapterName)
    {
        return  jdbcTemplate.queryForObject("SELECT chapterId FROM chapter WHERE courseId=? and chapterName=?", Integer.class, courseId, chapterName);
    }

    public Integer getLessonId(Integer chapterId, String lessonName)
    {
        return jdbcTemplate.queryForObject("SELECT lessonId FROM lesson WHERE chapterId=? and lessonName=?",Integer.class,chapterId, lessonName);
    }
    public Integer getCourseId(Integer courseId)
    {
        try
        {
            Integer courseIdData =  jdbcTemplate.queryForObject("SELECT courseId FROM course WHERE courseId = ?", Integer.class,courseId);
            return courseIdData;
        }
        catch(Exception e)
        {
            return  null;
        }
    }

    public Integer getLastLessonNumber(List<LessonDataRequest> lessonDataRequests,Integer chapterId)
    {
        Integer lessonNumber =0;
       for(LessonDataRequest lessonDataRequest : lessonDataRequests)
       {
           if(lessonDataRequest.getLessonId() != null)
           {
               lessonNumber  = jdbcTemplate.queryForObject("SELECT lessonNumber FROM lesson WHERE lessonId = ?", Integer.class, lessonDataRequest.getLessonId());
           }
       }
       return lessonNumber;
    }

    public void enrollmentUpdate(String adminId, Integer courseId, Integer chapterId, Integer lessonId)
    {
        System.out.println("Inside enrollment");
        List<Enrollment> enrollments = jdbcTemplate.queryForList("SELECT * FROM enrollment WHERE courseId = ?",Enrollment.class,chapterId);
        System.out.println(enrollments);
        if(enrollments.size() > 0)
        {
        for(Enrollment enrollment: enrollments)
        {
            jdbcTemplate.update("INSERT INTO courseProgress(userName, courseId) values(?,?)",enrollment.getUserName(),courseId);
            List<Integer> chapterIds = jdbcTemplate.queryForList("SELECT chapterId FROM chapter WHERE courseId = ?",Integer.class,courseId);
            for(Integer chapterIdData :chapterIds)
            {
                jdbcTemplate.update("INSERT INTO chapterProgress(userName,courseId,chapterId) values(?,?,?)",enrollment.getUserName(),courseId,chapterIdData);
                List<Integer> lessonIds = jdbcTemplate.queryForList("SELECT lessonId FROM lesson WHERE chapterId = ?", Integer.class, chapterId);
                for(Integer lessonIdData : lessonIds)
                {
                    jdbcTemplate.update("INSERT INTO lessonProgress(userName,chapterId,lessonId) values(?,?,?)",enrollment.getUserName(),chapterIdData,lessonIdData);
                }
                System.out.println("Status updation "+lessonIds.get(0));
                jdbcTemplate.update("UPDATE lessonStatus = true WHERE lessonId = ?",lessonIds.get(0));
            }
        }
        }
    }
    public void includeLesson(Integer courseId, ChapterDataRequest chapterDataRequest, String adminId, Integer chapterId) throws ParseException {
        System.out.println("include lesson"+chapterDataRequest);
        List<LessonDataRequest> lessonsList = chapterDataRequest.getLessonsList();
        Integer lessonNumber = getLastLessonNumber(chapterDataRequest.getLessonsList(),chapterId);
        for(LessonDataRequest lesson: lessonsList)
        {
            if(lesson.getLessonId() == null)
            {
                String lessonName = lesson.getLessonName();
                String lessonDuration = lesson.getLessonDuration();
                String videoLink = lesson.getVideoLink();
                Integer finalLessonNumber = lessonNumber;
                String query = "INSERT INTO lesson(lessonNumber,chapterId,lessonName,lessonDuration,videoLink) values(?,?,?,?,?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                String chapterName = chapterDataRequest.getChapterName();
                System.out.println("course Id "+courseId);
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection
                            .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, finalLessonNumber);
                    ps.setInt(2,chapterId);
                    ps.setString(3,lessonName);
                    ps.setString(4,lessonDuration);
                    ps.setString(5,videoLink);
                    return ps;
                }, keyHolder);
                Integer lessonId = Integer.parseInt(keyHolder.getKey().toString());
                ++lessonNumber;
                updateChapterDuration(chapterId,lesson.getLessonDuration());
                enrollmentUpdate(adminId,courseId, chapterId,lessonId);
            }
        }

    }
    public void includeChapter(ChapterDataRequest chapterDataRequest,Integer courseId,Integer chapterNumber, String adminId) throws ParseException {
        System.out.println("inside include chapter "+chapterDataRequest);
        String query ="INSERT INTO chapter(courseId,chapterNumber,chapterName) values(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String chapterName = chapterDataRequest.getChapterName();
        System.out.println("course Id "+courseId);
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, courseId);
            ps.setInt(2,chapterNumber);
            ps.setString(3,chapterName);
            return ps;
        }, keyHolder);
        Integer chapterId = Integer.parseInt(keyHolder.getKey().toString());
        includeLesson(courseId,chapterDataRequest,adminId,chapterId);
        updateUploadStatus(chapterId);
        updateUploadDate(courseId);
    }


    public Integer getLastChapterNumber(List<ChapterDataRequest> chapterDataRequestList)
    {
        Integer chapterNumber =0;
        for(ChapterDataRequest chapterDataRequest: chapterDataRequestList)
        {
            if(chapterDataRequest.getChapterId() != null)
            {
                chapterNumber = jdbcTemplate.queryForObject("SELECT chapterNumber FROM chapter WHERE chapterId = ?",Integer.class,chapterDataRequest.getChapterId());
            }
        }
        return chapterNumber;
    }
    public void updateChapterDuration(Integer chapterId,String lessonDuration) throws ParseException {
        String chapterTime = jdbcTemplate.queryForObject("SELECT chapterDuration FROM chapter WHERE chapterId = ?", String.class,chapterId);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date1 = timeFormat.parse(chapterTime);
        Date date2 = timeFormat.parse(lessonDuration);
        long sumOfDurations = date1.getTime() + date2.getTime();
        String chapterDuration = timeFormat.format(new Date(sumOfDurations));
        jdbcTemplate.update("UPDATE chapter SET chapterDuration = ? WHERE chapterId = ?",chapterDuration,chapterId);
    }

    public void reduceChapterDuration(Integer chapterId, String lessonDuration) throws ParseException {
        String chapterTime = jdbcTemplate.queryForObject("SELECT chapterDuration FROM chapter WHERE chapterId = ?", String.class,chapterId);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date1 = timeFormat.parse(chapterTime);
        Date date2 = timeFormat.parse(lessonDuration);
        long sumOfDurations = date1.getTime() - date2.getTime();
        String chapterDuration = timeFormat.format(new Date(sumOfDurations));
        jdbcTemplate.update("UPDATE chapter SET chapterDuration = ? WHERE chapterId = ?",chapterDuration,chapterId);
    }

    public void updateCourseDuration(Integer courseId) throws ParseException {
        long durationsSum = 0;
        String finalDuration = "";
        Date durationDate1 = null;
        Date durationDate2;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        jdbcTemplate.update("UPDATE course SET courseDuration= ? WHERE courseId = ?", "00:00:00",courseId);
        String courseDurationInfo = "00:00:00";

        List<Chapter> chapterDurationList = jdbcTemplate.query("SELECT chapterDuration FROM chapter WHERE courseId= ?", (rs, rowNum) -> {
            return new Chapter(rs.getString("chapterDuration"));
        }, courseId);
        for (Chapter chapter: chapterDurationList) {
            String chapterDurationInfo = chapter.getChapterDuration();
            SimpleDateFormat timeFormatInfo = new SimpleDateFormat("HH:mm:ss");
            timeFormatInfo.setTimeZone(TimeZone.getTimeZone("UTC"));
            durationDate1 = timeFormatInfo.parse(courseDurationInfo);
            durationDate2 = timeFormatInfo.parse(chapterDurationInfo);
            durationsSum = durationDate1.getTime() + durationDate2.getTime();
            finalDuration = timeFormat.format(new Date(durationsSum));
            courseDurationInfo = finalDuration;
        }
        System.out.println(finalDuration);
        jdbcTemplate.update("UPDATE course SET courseDuration = ? WHERE courseId = ?", finalDuration,courseId);
    }
    public void addLesson(ChapterDataRequest chapterDataRequest, Integer chapterId) throws ParseException {
        boolean uploadStatus = true;
        //String videoLink = null;
        List<LessonDataRequest> lessonsList = chapterDataRequest.getLessonsList();
        Integer lessonNumber =1;
        for(LessonDataRequest lesson: lessonsList)
        {
            if(lesson.getVideoLink() == null)
            {
                uploadStatus = false;
            }
            jdbcTemplate.update("INSERT INTO lesson(lessonNumber,chapterId,lessonName,lessonDuration,videoLink) values(?,?,?,?,?)",lessonNumber,chapterId,lesson.getLessonName(), lesson.getLessonDuration(), lesson.getVideoLink());
            ++lessonNumber;
            updateChapterDuration(chapterId,lesson.getLessonDuration());
        }
        if(uploadStatus == true)
        {
            jdbcTemplate.update("UPDATE chapter SET uploadStatus= true WHERE chapterId = ?", chapterId);
        }
    }
    public void updateLesson(ChapterDataRequest chapterDataRequest, Integer chapterId) throws ParseException {
        boolean uploadStatus = true;
        List<LessonDataRequest> lessonsList = chapterDataRequest.getLessonsList();
        for(LessonDataRequest lesson: lessonsList)
        {
            if(lesson.getVideoLink() == null)
            {
                uploadStatus = false;
            }
            jdbcTemplate.update("UPDATE lesson SET lessonName=?,lessonDuration=?,videoLink=? WHERE chapterId = ? and lessonId = ? ",lesson.getLessonName(), lesson.getLessonDuration(), lesson.getVideoLink(), chapterDataRequest.getChapterId(),lesson.getLessonId());
            reduceChapterDuration(chapterId, lesson.getLessonDuration());
            updateChapterDuration(chapterId,lesson.getLessonDuration());
        }
        if(uploadStatus == true)
        {
            jdbcTemplate.update("UPDATE chapter SET uploadStatus= true WHERE chapterId = ?", chapterId);
        }
        else
        {
            jdbcTemplate.update("UPDATE chapter SET uploadStatus = false WHERE chapterId = ?",chapterId);
        }
    }

    public String publish(Integer courseId)
    {
        boolean status= true;
        List<Chapter> chapterList = jdbcTemplate.query("SELECT * FROM chapter WHERE courseId = ?",new BeanPropertyRowMapper<>(Chapter.class),courseId);
        for(Chapter chapter: chapterList)
        {
             if(chapter.getUploadStatus() == false)
             {
                     status = false;
             }
        }
        if(status == true)
        {
            jdbcTemplate.update("UPDATE course SET publishstatus = true WHERE courseId = ?",courseId);
            return "Course Published";
        }
        return "Fail To Publish, Check the course and publish";
    }


}
