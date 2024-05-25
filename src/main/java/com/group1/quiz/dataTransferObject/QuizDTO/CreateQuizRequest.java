package com.group1.quiz.dataTransferObject.QuizDTO;

import com.group1.quiz.dataTransferObject.QuestionDTO.QuestionRequest;
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
