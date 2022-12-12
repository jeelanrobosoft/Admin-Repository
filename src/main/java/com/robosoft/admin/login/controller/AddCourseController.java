package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.dto.AddCourseRequest;
import com.robosoft.admin.login.dto.ChapterDataRequest;
import com.robosoft.admin.login.model.Overview;
import com.robosoft.admin.login.service.OverViewData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/addCourse")
public class AddCourseController {

    @Autowired
    private OverViewData overViewData;

    @PostMapping("/overView")
    public ResponseEntity<?> addOverView(@ModelAttribute Overview overview)
    {
        String response = overViewData.addOverView(overview);
        if(response.equals("Overview added"))
        {
            return new ResponseEntity<>(Collections.singletonMap("message", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(Collections.singletonMap("message", "Failed"), HttpStatus.NOT_MODIFIED);
    }

    @PostMapping("/chapter")
    public ResponseEntity<?> addChapter(@RequestBody AddCourseRequest addCourseRequest) throws ParseException {
          String response = overViewData.addChapter(addCourseRequest);
          if(response.equals("Chapter Data added"))
          {
              return new ResponseEntity<>(Collections.singletonMap("message", response), HttpStatus.OK);
          }
        return new ResponseEntity<>(Collections.singletonMap("message", "Failed"), HttpStatus.NOT_MODIFIED);
    }
}
