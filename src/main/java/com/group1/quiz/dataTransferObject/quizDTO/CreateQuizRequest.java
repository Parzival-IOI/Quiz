package com.group1.quiz.dataTransferObject.quizDTO;

import com.group1.quiz.dataTransferObject.questionDTO.QuestionRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateQuizRequest {
    private String name;
    private String description;
    private String visibility;
    private List<QuestionRequest> questions;
}
