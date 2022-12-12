package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCourseRequest {
    private Integer courseId;
    private List<ChapterDataRequest> chapterDataRequestList;
}
