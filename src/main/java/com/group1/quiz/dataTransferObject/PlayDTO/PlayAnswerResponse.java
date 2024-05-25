package com.group1.quiz.dataTransferObject.PlayDTO;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlayAnswerResponse {
    private String id;
    private String answer;
}
