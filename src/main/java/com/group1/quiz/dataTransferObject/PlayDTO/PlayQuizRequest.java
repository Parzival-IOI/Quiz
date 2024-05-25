package com.group1.quiz.dataTransferObject.PlayDTO;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlayQuizRequest {
    private String id;
    private List<PlayQuestionRequest> questions;
}
