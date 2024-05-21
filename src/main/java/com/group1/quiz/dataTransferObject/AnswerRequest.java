package com.group1.quiz.dataTransferObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {
    private String answer;
    private boolean is_correct;
}
