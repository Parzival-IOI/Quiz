package com.group1.quiz.dataTransferObject.AnswerDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateAnswerRequest {
    private String answer;
    private boolean isCorrect;
}
