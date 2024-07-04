package com.group1.quiz.dataTransferObject.QuizDTO;

import com.group1.quiz.dataTransferObject.QuestionDTO.UpdateQuestionRequest2;
import com.group1.quiz.enums.QuizVisibilityEnum;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateQuizRequest2 {
    private String name;
    private String description;
    private QuizVisibilityEnum visibility;
    private List<UpdateQuestionRequest2> questions;
}
