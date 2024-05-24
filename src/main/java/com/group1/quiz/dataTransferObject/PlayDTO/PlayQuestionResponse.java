package com.group1.quiz.dataTransferObject.PlayDTO;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlayQuestionResponse {
    private String id;
    private String question;
    private String type;
    private List<PlayAnswerResponse> answers;
}
