package com.group1.quiz.dataTransferObject.PlayDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayedAnswer {
    private String answer;
    private boolean isCorrect;
    private boolean isPick;
}
