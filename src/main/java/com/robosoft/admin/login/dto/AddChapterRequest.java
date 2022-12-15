package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddChapterRequest {
    private Integer courseId;
    private String courseName;
//    private Integer chapterId;
    private List<ChapterDataRequest> chapterDataRequestList;
}
