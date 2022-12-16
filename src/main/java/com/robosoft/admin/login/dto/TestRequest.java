package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestRequest {
    private Integer testId;
    private String testName;
    private Integer chapterId;
    private String testDuration;
    private Integer passingGrade;
    private List<QuestionRequest> questionRequests;

    public TestRequest(String testName, Integer chapterId, String testDuration, Integer passingGrade, List<QuestionRequest> questionRequests) {
        this.testName = testName;
        this.chapterId = chapterId;
        this.testDuration = testDuration;
        this.passingGrade = passingGrade;
        this.questionRequests = questionRequests;
    }
}
