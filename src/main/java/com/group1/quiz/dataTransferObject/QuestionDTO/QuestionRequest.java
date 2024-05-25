package com.group1.quiz.dataTransferObject.QuestionDTO;

import com.group1.quiz.dataTransferObject.AnswerDTO.AnswerRequest;
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
