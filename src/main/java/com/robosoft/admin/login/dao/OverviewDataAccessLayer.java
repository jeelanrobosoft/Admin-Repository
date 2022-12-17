package com.robosoft.admin.login.dao;


import com.robosoft.admin.login.dto.AddCourseRequest;
import com.robosoft.admin.login.dto.ChapterDataRequest;
import com.robosoft.admin.login.dto.LessonDataRequest;
import com.robosoft.admin.login.model.Chapter;
import com.robosoft.admin.login.model.CourseKeywords;
import com.robosoft.admin.login.model.Enrollment;
import com.robosoft.admin.login.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
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

        for(String keywordData: keywords)
        {
            if(keywordData.matches(".*\\d.*"))
            {
                return "Invalid Keyword";
            }
            status = true;
            jdbcTemplate.update("DELETE FROM courseKeywords WHERE courseId = ?", courseId);

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

    public void updateUploadStatus(List<LessonDataRequest> lessonDataRequest, Integer chapterId)
    {
        boolean status = true;

        for(LessonDataRequest lessonDataRequest1 : lessonDataRequest)
        {
            if(lessonDataRequest1.getVideoLink() == null)
            {
               status = false;
            }
        }
        if(status == true)
        {
            jdbcTemplate.update("UPDATE chapter SET uploadStatus = true WHERE chapterId = ?", chapterId);
        }
    }

    public void updateUploadDate(Integer courseId)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update("UPDATE course SET uploadedDate = ? WHERE courseId = ?",now,courseId);
    }
    public void addChapter(ChapterDataRequest chapterDataRequest,Integer courseId, Integer chapterNumber) throws ParseException {

        String query ="INSERT INTO chapter(courseId,chapterNumber,chapterName) values(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String chapterName = chapterDataRequest.getChapterName();

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
      //  updateUploadStatus(chapterDataRequest.getLessonsList());
     updateUploadStatus(chapterDataRequest.getLessonsList(), chapterId);
        updateUploadDate(courseId);
    }


public void updateChapter(ChapterDataRequest chapterDataRequest,Integer courseId) throws ParseException {
   jdbcTemplate.update("UPDATE chapter SET chapterName = ? WHERE courseId = ? and chapterId = ?",chapterDataRequest.getChapterName(),courseId,chapterDataRequest.getChapterId());
   updateLesson(chapterDataRequest,chapterDataRequest.getChapterId(), courseId);
   // updateUploadStatus(chapterDataRequest.getLessonsList());
   updateUploadStatus(chapterDataRequest.getLessonsList(), chapterDataRequest.getChapterId());
    updateUploadDate(courseId);
}
    public List<Chapter> getChaptersList(Integer courseId)
    {
        return  jdbcTemplate.query("SELECT * FROM chapter WHERE courseId=?", new BeanPropertyRowMapper<>(Chapter.class), courseId);
    }

    public List<Lesson> getLessonsList(Integer chapterId)
    {
        return jdbcTemplate.query("SELECT * FROM lesson WHERE chapterId=? ",new BeanPropertyRowMapper<>(Lesson.class),chapterId);
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

    public Integer getLastLessonNumber(Integer courseId, Integer chapterId)
    {
        Integer lessonNumber =0;
       List<Chapter> chapterList = jdbcTemplate.query("SELECT * FROM chapter WHERE courseId = ?", new BeanPropertyRowMapper<>(Chapter.class), courseId);
       for(Chapter chapter : chapterList)
       {
           List<Lesson> lessons = jdbcTemplate.query("SELECT * FROM lesson WHERE chapterId = ?", new BeanPropertyRowMapper<>(Lesson.class), chapter.getChapterId());
           for(Lesson lesson:lessons) {
               if (lesson.getLessonId() != null) {
                   lessonNumber = jdbcTemplate.queryForObject("SELECT lessonNumber FROM lesson WHERE lessonId = ?", Integer.class, lesson.getLessonId());
               }
           }
       }

       return lessonNumber;
    }

    public List<String> getEnrolledUsers(Integer courseId)
    {

        List<String> enrolledUsers = jdbcTemplate.queryForList("SELECT userName FROM enrollment WHERE courseId = ?",  String.class,courseId);

        return  enrolledUsers;
    }
    public void chapterEnrollmentUpdate(Integer courseId, Integer chapterId)
    {
         List<String> usernames = getEnrolledUsers(courseId);
         for(String username:usernames)
         {
             jdbcTemplate.update("INSERT INTO chapterProgress(userName,courseId,chapterId) values(?,?,?)",username,courseId,chapterId);
         }
    }
    public void

    updateLessonStatus(Integer courseId, Integer chapterId, Integer lessonId)
    {
        List<String> enrolledUsers = getEnrolledUsers(courseId);
        boolean previousLessonCompleted = false;
        List<Lesson> lessons = jdbcTemplate.query("SELECT * FROM lesson WHERE chapterId = ?", new BeanPropertyRowMapper<>(Lesson.class), chapterId);
        for(int i=0;i<lessons.size();i++)
        {
            for(String user: enrolledUsers)
            {
                if(lessons.get(i).getLessonId() == lessonId)
                {
                    previousLessonCompleted = jdbcTemplate.queryForObject("SELECT lessonCompletedStatus FROM lessonProgress WHERE lessonId = ? and userName = ?",new BeanPropertyRowMapper<>(Boolean.class),lessons.get(i-1).getLessonId(),user);
                    if (previousLessonCompleted == true)
                    {
                        jdbcTemplate.update("UPDATE lessonProgress SET lessonStatus = true WHERE lessonId = ? and userName = ?", lessonId, user);
                        jdbcTemplate.update("UPDATE chapterProgress SET chapterCompletedStatus = false WHERE chapterId = ? and userName = ?", chapterId,user);

                    }
                }
            }

        }
    }

    public void updateLessonNumbers(Integer courseId, Integer chapterId)
    {
        Integer lastLessonNumber = getLastLessonNumberOfChapter(chapterId);
        List<Chapter> chapterList = getChaptersList(courseId);
        for(Chapter chapter : chapterList)
        {
            if(chapter.getChapterId() > chapterId)
            {
                List<Lesson> lessons = getLessonsList(chapter.getChapterId());
                for(Lesson lesson : lessons)
                {

                    jdbcTemplate.update("UPDATE lesson SET lessonNumber = ? WHERE lessonId = ? and  chapterId = ?",++lastLessonNumber,lesson.getLessonId(),chapter.getChapterId());
                }
            }
        }
    }

    public void lessonEnrollmentUpdate(Integer courseId, Integer chapterId,Integer lessonId)
    {

        List<String> usernames = getEnrolledUsers(courseId);

        for(String username:usernames)
        {

            jdbcTemplate.update("INSERT INTO lessonProgress(userName,chapterId,lessonId) values(?,?,?)",username,chapterId,lessonId);
        }

        updateLessonStatus(courseId,chapterId,lessonId);
    }
//    public void enrollmentUpdate(Integer courseId)
//    {
//
//        List<Enrollment> enrollments = jdbcTemplate.query("SELECT username, courseId FROM enrollment WHERE courseId = ?",new BeanPropertyRowMapper<>(Enrollment.class),courseId);
//
//        ////////remove
//        if(enrollments.size() > 0)
//        {
//        for(Enrollment enrollment: enrollments)
//        {
//
//            jdbcTemplate.update("INSERT INTO courseProgress(userName, courseId) values(?,?)",enrollment.getUserName(),courseId);
//            List<Chapter> chapterIds = jdbcTemplate.query("SELECT * FROM chapter WHERE courseId = ?",new BeanPropertyRowMapper<>(Chapter.class),courseId);
//            for(Chapter chapterIdData :chapterIds)
//            {
//
//                jdbcTemplate.update("INSERT INTO chapterProgress(userName,courseId,chapterId) values(?,?,?)",enrollment.getUserName(),courseId,chapterIdData.getChapterId());
//                List<Lesson> lessonIds = jdbcTemplate.query("SELECT * FROM lesson WHERE chapterId = ?",new BeanPropertyRowMapper<>(Lesson.class),chapterIdData.getChapterId());
//                for(Lesson lessonIdData : lessonIds)
//                {
//
//                    jdbcTemplate.update("INSERT INTO lessonProgress(userName,chapterId,lessonId) values(?,?,?)",enrollment.getUserName(),chapterIdData.getChapterId(),lessonIdData.getLessonId());
//                }
//                jdbcTemplate.update("UPDATE lessonProgress set lessonStatus = true WHERE lessonId = ? and userName = ?",lessonIds.get(0).getLessonId(),enrollment.getUserName());
//
//            }
//        }
//        }
//    }

 public void isCourseCompleted(Integer courseId,Integer chapterId, Integer lessonId)
 {
     List<String> users = getEnrolledUsers(courseId);
     for(String user : users)
     {
         boolean chaptercompletedStatus = jdbcTemplate.queryForObject("SELECT chapterCompletedStatus FROM chapterProgress WHERE chapterId = ? and username = ?", Boolean.class,chapterId, user);
         if(chaptercompletedStatus == true)
         {
             jdbcTemplate.update("UPDATE lessonProgress SET lessonStatus = true WHERE lessonId = ? and userName = ?", lessonId, user);
         }
         jdbcTemplate.update("UPDATE chapterProgress SET chapterCompletedStatus = false, chapterStatus = true WHERE chapterId = ? and userName = ?", chapterId, user);
//         String completedDate = jdbcTemplate.queryForObject("SELECT completedDate FROM enrollment WHERE courseId = ? and userName = ?", new BeanPropertyRowMapper<>(String.class),courseId, user);
//         if(!(completedDate.equals("0000-00-00")))
//         {
//
//             jdbcTemplate.update("UPDATE lessonProgress SET lessonStatus= true WHERE userName = ? and lessonId = ? ", user,lessonId);
//         }
     }
 }
 public Integer includeChapterWithVideoLink(Integer courseId,Integer chapterId,Integer finalLessonNumber, String lessonDuration,String lessonName, String videoLink) throws ParseException {
     String query = "INSERT INTO lesson(lessonNumber,chapterId,lessonName,lessonDuration,videoLink) values(?,?,?,?,?)";
     KeyHolder keyHolder = new GeneratedKeyHolder();
     jdbcTemplate.update(connection -> {
         PreparedStatement ps = connection
                 .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
         ps.setInt(1, finalLessonNumber);
         ps.setInt(2, chapterId);
         ps.setString(3, lessonName);
         ps.setString(4, lessonDuration);
         ps.setString(5, videoLink);
         return ps;
     }, keyHolder);

     Integer lessonId = Integer.parseInt(keyHolder.getKey().toString());
     //  ++lessonNumber;
     updateChapterDuration(chapterId, lessonDuration);

     lessonEnrollmentUpdate(courseId,chapterId,lessonId);
     isCourseCompleted(courseId,chapterId,lessonId);
     return  lessonId;
 }
    public Integer includeChapterWithOutVideoLink(Integer courseId,Integer chapterId,Integer finalLessonNumber, String lessonDuration,String lessonName) throws ParseException {
        String query = "INSERT INTO lesson(lessonNumber,chapterId,lessonName,lessonDuration) values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, finalLessonNumber);
            ps.setInt(2, chapterId);
            ps.setString(3, lessonName);
            ps.setString(4, lessonDuration);
            return ps;
        }, keyHolder);

        Integer lessonId = Integer.parseInt(keyHolder.getKey().toString());
        //  ++lessonNumber;
        updateChapterDuration(chapterId, lessonDuration);

        lessonEnrollmentUpdate(courseId,chapterId,lessonId);
        isCourseCompleted(courseId,chapterId,lessonId);
        return lessonId;
    }
    public void includeLesson(Integer courseId, ChapterDataRequest chapterDataRequest, Integer chapterId) throws ParseException {

        List<LessonDataRequest> lessonsList = chapterDataRequest.getLessonsList();
        for(LessonDataRequest lesson: lessonsList)
        {
            Integer lessonNumber = getLastLessonNumber(courseId,chapterId);
            if(lesson.getLessonId() == null)
            {
                String lessonName = lesson.getLessonName();
                String lessonDuration = lesson.getLessonDuration();
                String videoLink = lesson.getVideoLink();
                Integer finalLessonNumber = lessonNumber+1;
                if(videoLink != null) {
                    includeChapterWithVideoLink(courseId,chapterId,finalLessonNumber,lessonDuration,lessonName,videoLink);
                }
                else {

                    includeChapterWithOutVideoLink(courseId,chapterId,finalLessonNumber,lessonDuration,lessonName);
                }
            }
        }
    }
    public void includeChapter(ChapterDataRequest chapterDataRequest,Integer courseId,Integer chapterNumber, String adminId) throws ParseException {

        String query ="INSERT INTO chapter(courseId,chapterNumber,chapterName) values(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String chapterName = chapterDataRequest.getChapterName();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, courseId);
            ps.setInt(2,chapterNumber);
            ps.setString(3,chapterName);
            return ps;
        }, keyHolder);
        Integer chapterId = Integer.parseInt(keyHolder.getKey().toString());
        includeLesson(courseId,chapterDataRequest,chapterId);
        chapterEnrollmentUpdate(courseId,chapterId);
       updateUploadStatus(chapterDataRequest.getLessonsList(), chapterId);
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

    public Integer getMiddleChapterLastLessonNumber(Integer chapterId)
    {
        Integer lastLessonNumber =0;
        List<Lesson> lessons = jdbcTemplate.query("SELECT * FROM lesson WHERE chapterId = ?", new BeanPropertyRowMapper<>(Lesson.class),chapterId);
        for(Lesson lesson: lessons)
        {
            lastLessonNumber = lesson.getLessonNumber();
        }
        return lastLessonNumber;
    }

    public Integer getLastLessonNumberOfChapter(Integer chapterId)
    {
        Integer lessonNumber =0;
        List<Lesson> lessons = jdbcTemplate.query("SELECT * FROM lesson WHERE chapterId =?", new BeanPropertyRowMapper<>(Lesson.class), chapterId);
        for(Lesson lesson : lessons)
        {
            lessonNumber = jdbcTemplate.queryForObject("SELECT lessonNumber FROM lesson WHERE chapterId = ? and lessonId = ?",Integer.class, chapterId, lesson.getLessonId());
        }
        return lessonNumber;
    }
    public Integer addLessonToExistingChapter(LessonDataRequest lesson, Integer chapterId, Integer courseId) throws ParseException {
        Integer lessonNumber = getLastLessonNumberOfChapter(chapterId);
        Integer lessonId = 0;
        if(lesson.getVideoLink() == null)
        {
            lessonId= includeChapterWithOutVideoLink(courseId,chapterId,++lessonNumber,lesson.getLessonDuration(), lesson.getLessonName());
        }
        else {
            lessonId= includeChapterWithVideoLink(courseId,chapterId,++lessonNumber,lesson.getLessonDuration(), lesson.getLessonName(),lesson.getVideoLink());
        }
        return lessonId;
    }
    public void updateLesson(ChapterDataRequest chapterDataRequest, Integer chapterId, Integer courseId) throws ParseException {
        boolean uploadStatus = true;
        List<LessonDataRequest> lessonsList = chapterDataRequest.getLessonsList();
        for(LessonDataRequest lesson: lessonsList)
        {
            if(lesson.getVideoLink() == null)
            {
                uploadStatus = false;
            }
            if(lesson.getLessonId() != null) {
                jdbcTemplate.update("UPDATE lesson SET lessonName=?,lessonDuration=?,videoLink=? WHERE chapterId = ? and lessonId = ? ", lesson.getLessonName(), lesson.getLessonDuration(), lesson.getVideoLink(), chapterDataRequest.getChapterId(), lesson.getLessonId());
                reduceChapterDuration(chapterId, lesson.getLessonDuration());
                updateChapterDuration(chapterId, lesson.getLessonDuration());
            }
            else {
                Integer lessonId = addLessonToExistingChapter(lesson,chapterId,courseId);
                updateLessonNumbers(courseId,chapterId);
            }
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
        Integer size = chapterList.size()-1;
        System.out.println(size);
        try
        {
            Integer test = jdbcTemplate.queryForObject("SELECT testId FROM test WHERE chapterId = ?", Integer.class, chapterList.get(size).getChapterId());
        }
       catch (Exception e)
       {
            return "Publishing to web failed.... Final test is missing";
        }
        if(status == true)
        {
            jdbcTemplate.update("UPDATE course SET publishstatus = true WHERE courseId = ?",courseId);
            return "Course Published";
        }
        System.out.println(status);
        return "Fail To Publish, Check the course and publish";
    }


}
