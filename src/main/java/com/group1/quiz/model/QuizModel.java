package com.group1.quiz.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value="quizzes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class QuizModel {
    @Transient
    public static final String SEQUENCE_NAME = "projects_sequence";
    @Id
    private String id;
    private String name;
    private String description;
    private String visibility;
    private String user_id;
    private Date created_at;
    private Date updated_at;
}
