package com.group1.quiz.dataTransferObject.PlayDTO;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayResponse {
    private String id;
    private double score;
    private List<PlayQuestionRequest> answers;
    private String quizId;
    private String quizName;
    private Date createdAt;
    private Date updatedAt;
}
