package com.group1.quiz.dataTransferObject;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TableResponse<T> {
    private List<T> quizzes;
    private long columns;
}
