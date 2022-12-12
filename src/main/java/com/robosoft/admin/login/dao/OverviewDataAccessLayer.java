package com.robosoft.admin.login.dao;

import com.robosoft.admin.login.dto.ChapterDataRequest;
import com.robosoft.admin.login.dto.LessonDataRequest;
import com.robosoft.admin.login.model.Chapter;
import com.robosoft.admin.login.model.Overview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class OverviewDataAccessLayer {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void  addOverView(Overview overview)
    {
         String instructorId = SecurityContextHolder.getContext().getAuthentication().getName();
         instructorId="akjeelan22@gmail.com";  //REMOVE THIS AFTER INTEGRATING WITH AUTHENTICATION CODE
         jdbcTemplate.update("UPDATE course SET coursePhoto = ?, previewVideo=? WHERE courseId=?",overview.getCoursePhoto(),overview.getPreviewVideo(),overview.getCourseId());
         jdbcTemplate.update("INSERT INTO overView(courseId,courseTagLine,description,learningOutCome,requirements,instructorId,difficultyLevel) values(?,?,?,?,?,?,?)",overview.getCourseId(), overview.getCourseTagLine(), overview.getDescription(), overview.getLearningOutCome(), overview.getRequirements(), instructorId,overview.getDifficultyLevel());
    }

    public void addChapter(ChapterDataRequest chapterDataRequest, Integer courseId) throws ParseException {
        Integer chapterNumber = 1;
        jdbcTemplate.update("INSERT INTO chapter(courseId,chapterNumber,chapterName,) values(?,?,?)", courseId,chapterNumber++,chapterDataRequest.getChapterName());
        addLesson(chapterDataRequest,courseId);
    }

    public Integer getChapterId(Integer courseId, String chapterName)
    {
        return  jdbcTemplate.queryForObject("SELECT chapterId FROM chapter WHERE courseId=? and chapterName=?", Integer.class, courseId, chapterName);
    }

    public Integer getLessonId(Integer chapterId, String lessonName)
    {
        return jdbcTemplate.queryForObject("SELECT lessonId FROM lesson WHERE chapterId=? and lessonName=?",Integer.class,chapterId, lessonName);
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
    public void addLesson(ChapterDataRequest chapterDataRequest, Integer courseId) throws ParseException {
        Integer lessonNumber = 1;
        List<LessonDataRequest> lessonsList = chapterDataRequest.getLessonsList();
        Integer chapterId = getChapterId(courseId, chapterDataRequest.getChapterName());
        for(LessonDataRequest lesson: lessonsList)
        {
            jdbcTemplate.update("INSERT INTO lesson(lessonNumber,chapterId,lessonName,lessonDuration,videoLink) values(?,?,?,?,?)",lessonNumber++,chapterId,lesson.getLessonName(), lesson.getLessonDuration(),lesson.getVideoLink());
            Integer lessonId = getLessonId(chapterId,lesson.getLessonName());
            updateChapterDuration(chapterId,lesson.getLessonDuration());
        }
    }
}
