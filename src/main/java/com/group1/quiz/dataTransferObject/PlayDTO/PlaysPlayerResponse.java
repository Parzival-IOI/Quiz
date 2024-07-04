package com.group1.quiz.dataTransferObject.PlayDTO;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlaysPlayerResponse {
    private String id;
    private double score;
    private String quizId;
    private String quizName;
    private String username;
    private Date createdAt;
    private Date updatedAt;
}
