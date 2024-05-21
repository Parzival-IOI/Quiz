package com.group1.quiz.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(value="answers")
@Builder
@Data
public class AnswerModel {
    @Id
    private String id;
    private String answer;
    private boolean is_correct;
    private String question_id;
    private Date created_at;
    private Date updated_at;
}
