package com.group1.quiz.dataTransferObject.QuizDTO;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuizTableResponse {
    private List<QuizzesResponse> quizzes;
    private long columns;
}
