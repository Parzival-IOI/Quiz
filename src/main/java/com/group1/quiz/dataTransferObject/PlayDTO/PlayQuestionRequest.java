package com.group1.quiz.dataTransferObject.PlayDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlayQuestionRequest {
    private String id;
    private String answerId;
}
