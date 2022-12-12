package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDataRequest {
    private Integer chapterId;
    private String lessonName;
    private String lessonDuration;
    private String videoLink;
}
