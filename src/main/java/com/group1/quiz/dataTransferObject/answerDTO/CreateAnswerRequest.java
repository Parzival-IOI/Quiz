package com.group1.quiz.dataTransferObject.answerDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateAnswerRequest {
    private String answer;
    private boolean isCorrect;
    private String questionId;
}
