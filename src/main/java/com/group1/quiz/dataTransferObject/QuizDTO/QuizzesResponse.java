package com.group1.quiz.dataTransferObject.QuizDTO;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class QuizzesResponse {
    private String id;
    private String name;
    private String description;
    private String visibility;
    private Date createdAt;
    private Date updatedAt;
}
