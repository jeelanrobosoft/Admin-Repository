package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.OverviewDataAccessLayer;
import com.robosoft.admin.login.dto.AddChapterRequest;
import com.robosoft.admin.login.dto.AddCourseRequest;
import com.robosoft.admin.login.dto.ChapterDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class OverViewData {


    public static Integer CourseIdentifier =0;
    @Autowired
    private OverviewDataAccessLayer overviewDataAccessLayer;
    public String addCourseOverView(AddCourseRequest addCourseRequest) throws IOException {
        System.out.println(addCourseRequest);
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
        System.out.println("CourseIdData "+courseIdData);
        if(courseIdData == null)
        {
            String courseId =overviewDataAccessLayer.addCourse(addCourseRequest,adminId);
             CourseIdentifier = Integer.parseInt(courseId);
            overviewDataAccessLayer. addOverView(addCourseRequest,Integer.parseInt(courseId));
            System.out.println("overview data ="+CourseIdentifier);
            return "Overview added";
        }
       else
       {
           System.out.println("else");
         overviewDataAccessLayer.updateCourse(addCourseRequest);
         overviewDataAccessLayer.updateOverView(addCourseRequest);
         return "Overview updated";
       }
    }
   public String addChapter(AddChapterRequest addCourseRequest) throws ParseException {
       String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
       System.out.println("chapter data "+CourseIdentifier);
       Integer courseId = overviewDataAccessLayer.getCourseId(addCourseRequest.getCourseId());
       List<ChapterDataRequest> chapterDataRequestList = addCourseRequest.getChapterDataRequestList();
       Integer chapterNumber = 1;
       System.out.println("course Id data "+CourseIdentifier);
       if(courseId == null || courseId ==0)
       {
           for(ChapterDataRequest chapter: chapterDataRequestList)
           {
               System.out.println("Course identifier "+CourseIdentifier);
               overviewDataAccessLayer.addChapter(chapter,CourseIdentifier,chapterNumber);
               ++chapterNumber;
           }
           overviewDataAccessLayer.updateCourseDuration(CourseIdentifier);
           return "Chapter Data Added";
       }
       else
       {
           chapterNumber = overviewDataAccessLayer.getLastChapterNumber(chapterDataRequestList);
           System.out.println("Last chapter number "+chapterNumber);
           for(ChapterDataRequest chapter: chapterDataRequestList)
           {

               if(chapter.getChapterId() != null)
               {
                   overviewDataAccessLayer.updateChapter(chapter,addCourseRequest.getCourseId());
               }
               else
               {
                   System.out.println("NEW CHAPTER");
                    overviewDataAccessLayer.includeChapter(chapter,addCourseRequest.getCourseId(),chapterNumber, adminId);
               }
           }
           overviewDataAccessLayer.updateCourseDuration(courseId);
           return "Chapter Data Updated";
       }
   }

   public String publishToWeb(Integer courseIdentifier)
   {
       if(courseIdentifier == null)
       {
           return overviewDataAccessLayer.publish(CourseIdentifier);
       }
       {
           //Integer courseId = overviewDataAccessLayer.getCourseId(courseIdentifier);
           return overviewDataAccessLayer.publish(courseIdentifier);
       }
   }
}
