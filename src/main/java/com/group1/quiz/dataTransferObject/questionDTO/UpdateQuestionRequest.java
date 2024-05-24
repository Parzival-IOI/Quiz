package com.group1.quiz.dataTransferObject.questionDTO;

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
