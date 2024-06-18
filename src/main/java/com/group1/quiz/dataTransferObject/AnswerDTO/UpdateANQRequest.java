package com.group1.quiz.dataTransferObject.AnswerDTO;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
public class UpdateANQRequest {
    private String id;
    private String answer;
    private boolean isCorrect;
}
