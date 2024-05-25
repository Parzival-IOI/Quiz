package com.group1.quiz.dataTransferObject.AnswerDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {
    private String answer;
    private boolean isCorrect;
}
