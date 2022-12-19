package com.robosoft.admin.login.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.robosoft.admin.login.model.Lesson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChapterResponse {
    private String chapterId;
    private String chapterName;

    private List<Lesson> lessonList;
    private String uploadStatus;
}
