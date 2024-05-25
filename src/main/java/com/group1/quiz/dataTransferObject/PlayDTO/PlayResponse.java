package com.group1.quiz.dataTransferObject.PlayDTO;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayResponse {
    private String id;
    private double score;
    private String quizId;
    private Date createdAt;
    private Date updatedAt;
}
