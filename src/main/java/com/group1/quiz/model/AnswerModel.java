package com.group1.quiz.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Document(value="answers")
@Builder
@Data
public class AnswerModel {
    @Id
    private String id;

    @Field(name="answer")
    private String answer;

    @Field(name="isCorrect")
    private boolean isCorrect;

    @Field(name="questionId")
    private String questionId;

    @Field(name="createdAt")
    private Date createdAt;

    @Field(name="updatedAt")
    private Date updatedAt;
}
