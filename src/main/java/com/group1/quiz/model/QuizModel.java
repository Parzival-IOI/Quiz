package com.group1.quiz.model;

import com.group1.quiz.enums.QuizVisibilityEnum;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Field(name="visibility")
    private QuizVisibilityEnum visibility;
    private String userId;
    private Date createdAt;
    private Date updatedAt;
}
