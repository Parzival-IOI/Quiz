package com.group1.quiz.dataTransferObject.QuizDTO;

import com.group1.quiz.enums.QuizVisibilityEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateQuizRequest {
    private String name;
    private String description;
    private QuizVisibilityEnum visibility;
}
