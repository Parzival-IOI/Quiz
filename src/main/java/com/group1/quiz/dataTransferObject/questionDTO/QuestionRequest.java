package com.group1.quiz.dataTransferObject.questionDTO;

import com.group1.quiz.dataTransferObject.answerDTO.AnswerRequest;
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
