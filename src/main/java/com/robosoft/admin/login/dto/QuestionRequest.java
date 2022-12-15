package com.robosoft.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
    private Integer questionId;
    private String questionName;
    private String option_1;
    private String option_2;
    private String option_3;
    private String option_4;
    private String correctAnswer;
    private boolean deleteStatus = false;
}
