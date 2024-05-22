package com.group1.quiz.dataTransferObject.questionDTO;

import com.group1.quiz.dataTransferObject.answerDTO.AnswerResponse;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionResponse {
    private String id;
    private String question;
    private String type;
    private List<AnswerResponse> answers;
    private Date createdAt;
    private Date updatedAt;
}
