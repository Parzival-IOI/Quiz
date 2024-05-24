package com.group1.quiz.dataTransferObject.questionDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateQuestionRequest {
    private String question;
    private String type;
    private String quizId;
}
