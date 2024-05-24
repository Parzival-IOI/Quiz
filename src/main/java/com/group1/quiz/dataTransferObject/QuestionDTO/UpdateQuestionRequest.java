package com.group1.quiz.dataTransferObject.QuestionDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateQuestionRequest {
    private String question;
    private String type;
}
