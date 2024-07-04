package com.group1.quiz.dataTransferObject.QuestionDTO;

import com.group1.quiz.dataTransferObject.AnswerDTO.UpdateAnswerRequest2;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateQuestionRequest2 {
    private String id;
    private String question;
    private String type;
    private List<UpdateAnswerRequest2> answers;
}
