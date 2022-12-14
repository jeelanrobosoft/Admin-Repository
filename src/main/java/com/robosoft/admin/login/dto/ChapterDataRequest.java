package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDataRequest {
    private Integer chapterId;
    private String chapterName;
    private List<LessonDataRequest> lessonsList;
}
