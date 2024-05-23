package com.group1.quiz.dataTransferObject.quizDTO;

import com.group1.quiz.dataTransferObject.questionDTO.QuestionResponse;
import com.group1.quiz.enums.QuizVisibilityEnum;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class QuizResponse {
    private String id;
    private String name;
    private String description;
    private QuizVisibilityEnum visibility;
    private List<QuestionResponse> questions;
    private Date createdAt;
    private Date updatedAt;
}
