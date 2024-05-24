package com.group1.quiz.dataTransferObject.PlayDTO;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlayQuizResponse {
    private String id;
    private String name;
    private String description;
    private List<PlayQuestionResponse> questions;
}
