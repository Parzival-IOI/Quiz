package com.group1.quiz.model;

import com.group1.quiz.enums.QuizVisibilityEnum;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(value="quizzes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class QuizModel {
    @Id
    private String id;
    private String name;
    private String description;
    @Field(name="visibility")
    private QuizVisibilityEnum visibility;
    private String userId;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    @Version
    private Integer version;
}
