package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.OverviewDataAccessLayer;
import com.robosoft.admin.login.dto.AddCourseRequest;
import com.robosoft.admin.login.dto.ChapterDataRequest;
import com.robosoft.admin.login.model.Overview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public class OverViewData {

    @Autowired
    private OverviewDataAccessLayer overviewDataAccessLayer;
   public String addOverView(Overview overview)
   {
        overviewDataAccessLayer.addOverView(overview);
        return "Overview added";
   }

   public String addChapter(AddCourseRequest addCourseRequest) throws ParseException {
       List<ChapterDataRequest> chapterDataRequestList = addCourseRequest.getChapterDataRequestList();
       for(ChapterDataRequest chapter: chapterDataRequestList)
       {
           overviewDataAccessLayer.addChapter(chapter,addCourseRequest.getCourseId());
       }
       overviewDataAccessLayer.updateCourseDuration(addCourseRequest.getCourseId());
       return "Chapter Data added";
   }
}
