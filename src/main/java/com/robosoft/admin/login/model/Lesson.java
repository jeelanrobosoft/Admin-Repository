package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    private Integer lessonId;
    private Integer lessonNumber;
    private Integer chapterId;
    private String lessonName;
    private String lessonDuration;
    private String videoLink;
}
