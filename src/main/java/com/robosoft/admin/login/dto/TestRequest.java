package com.robosoft.admin.login.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestRequest {
    private String testName;
    private Integer chapterId;
    private String testDuration;
    private Integer passingGrade;
    private List<QuestionRequest>questionRequests;
}
