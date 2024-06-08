package com.group1.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(value = "login")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginModel {
    @Id
    private String id;

    @Indexed(unique = true)
    @Field(name="userName")
    private String userName;

    @Field(name="refreshToken")
    private String refreshToken;
}
