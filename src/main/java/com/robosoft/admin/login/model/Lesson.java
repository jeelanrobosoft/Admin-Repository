package com.robosoft.admin.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lesson {
    private Integer lessonId;
    private Integer lessonNumber;
    private Integer chapterId;
    private String lessonName;
    private String lessonDuration;
    private String videoLink;
}
