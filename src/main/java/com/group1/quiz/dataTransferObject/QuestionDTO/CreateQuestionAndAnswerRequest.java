package com.group1.quiz.dataTransferObject.QuestionDTO;

import com.group1.quiz.dataTransferObject.AnswerDTO.CreateAnswerWithQuestion;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateQuestionAndAnswerRequest {
    private String question;
    private String type;
    private String quizId;
    private List<CreateAnswerWithQuestion> answer;
}
