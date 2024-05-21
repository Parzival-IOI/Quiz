package com.group1.quiz.dataTransferObject;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionRequest {
    private String question;
    private String type;
    private List<AnswerRequest> answers;
}
