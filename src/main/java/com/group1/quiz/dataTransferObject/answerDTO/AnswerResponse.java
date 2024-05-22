package com.group1.quiz.dataTransferObject.answerDTO;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AnswerResponse {
    private String id;
    private String answer;
    private boolean isCorrect;
    private Date createdAt;
    private Date updatedAt;
}
