package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.OverviewDataAccessLayer;
import com.robosoft.admin.login.dto.AddChapterRequest;
import com.robosoft.admin.login.dto.AddCourseRequest;
import com.robosoft.admin.login.dto.ChapterDataRequest;
import com.robosoft.admin.login.model.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class OverViewData {

    private static Integer courseIdData = 0;
    @Autowired
    private OverviewDataAccessLayer overviewDataAccessLayer;
    public String addCourseOverView(AddCourseRequest addCourseRequest) throws IOException {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        String keywords[] = addCourseRequest.getCourseKeyword().split(",");
        for(String keyword: keywords)
        {
            if(keyword.matches(".*\\d.*"))
            {
                return "Invalid Keyword";
            }
        }

        Integer courseIdData = overviewDataAccessLayer.getCourseId(addCourseRequest.getCourseId());
        if(courseIdData == null)
        {
            String courseId =overviewDataAccessLayer.addCourse(addCourseRequest,adminId);
            //courseIdData = Integer.parseInt(courseId);
            overviewDataAccessLayer.addOverView(addCourseRequest,Integer.parseInt(courseId));
            return "Overview added";
        }
       else
       {
         overviewDataAccessLayer.updateCourse(addCourseRequest);
         overviewDataAccessLayer.updateOverView(addCourseRequest);
         return "Overview updated";
       }
    }
   public String addChapter(AddChapterRequest addCourseRequest) throws ParseException {
       Integer courseId = overviewDataAccessLayer.getCourseId(addCourseRequest.getCourseId());
       List<ChapterDataRequest> chapterDataRequestList = addCourseRequest.getChapterDataRequestList();
       Integer chapterNumber = 1;
       if(courseId == null)
       {
           for(ChapterDataRequest chapter: chapterDataRequestList)
           {
               overviewDataAccessLayer.addChapter(chapter,courseId,chapterNumber);
               ++chapterNumber;
           }
           overviewDataAccessLayer.updateCourseDuration(courseIdData);
           return "Chapter Data Added";
       }
       else
       {
           for(ChapterDataRequest chapter: chapterDataRequestList)
           {
               overviewDataAccessLayer.updateChapter(chapter,addCourseRequest.getCourseId());
           }
           overviewDataAccessLayer.updateCourseDuration(courseId);
           return "Chapter Data Updated";
       }
   }

   public String publishToWeb(Integer courseIdentifier)
   {
       if(courseIdentifier == null)
       {
           return overviewDataAccessLayer.publish(courseIdData);
       }
       {
           //Integer courseId = overviewDataAccessLayer.getCourseId(courseIdentifier);
           return overviewDataAccessLayer.publish(courseIdentifier);
       }
   }
}
